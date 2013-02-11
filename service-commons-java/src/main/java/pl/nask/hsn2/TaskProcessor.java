/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.0.
 * 
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.nask.hsn2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.connector.BusException;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;
import pl.nask.hsn2.protobuff.Process.TaskRequest;
import pl.nask.hsn2.task.Task;
import pl.nask.hsn2.task.TaskContextFactory;
import pl.nask.hsn2.task.TaskFactory;
import pl.nask.hsn2.wrappers.ObjectDataWrapper;
import pl.nask.hsn2.wrappers.ParametersWrapper;

import com.rabbitmq.client.ShutdownSignalException;


public class TaskProcessor implements Callable<Void>, TaskContextFactory {
    private final static Logger LOG = LoggerFactory.getLogger(TaskProcessor.class);

    private final TaskFactory jobFactory;
    private final TaskContextFactory taskContextFactory;
    private final ServiceConnector connector;
    private final FinishedJobsListener finishedJobsListener;

	private AtomicBoolean interrupted = new AtomicBoolean(false);

    public TaskProcessor(TaskFactory jobFactory, ServiceConnector serviceConnector, FinishedJobsListener finishedJobsListener) {
        this.jobFactory = jobFactory;
        this.connector = serviceConnector;
        this.taskContextFactory = this;
        this.finishedJobsListener = finishedJobsListener;
    }

    public TaskProcessor(TaskFactory jobFactory, ServiceConnector serviceConnector, TaskContextFactory contextFactory, FinishedJobsListener finishedJobsListener) {
        this.jobFactory = jobFactory;
        this.connector = serviceConnector;
        this.taskContextFactory = contextFactory;
        this.finishedJobsListener = finishedJobsListener;
    }
    
    public void setCanceled() {
    	interrupted.set(true);
    }

    @Override
    public Void call() throws Exception {
        processTasks();
        return null;
    }       

    protected void processTasks() {
        while (!interrupted.get() ) {
            try {
				processOneTask();
			} catch (InterruptedException e) {
				interrupted.set(true);
			}
        }
    }

	protected void processOneTask() throws InterruptedException {
		LOG.debug("Waiting for TaskRequest...");

		long jobId = 0;
		int reqId = 0;

		try {
			TaskRequest req = connector.getTaskRequest();
			long taskStartMilis = System.currentTimeMillis();
			jobId = req.getJob();
			reqId = req.getTaskId();
			
			if (finishedJobsListener != null && finishedJobsListener.isJobFinished(jobId)) {
				connector.ignoreLastTaskRequest();
				LOG.warn("Got TaskRequest for finished job. Ignored. (jobId={}, requestId={})", jobId, reqId);
			} else {
				ParametersWrapper params = new ParametersWrapper(req.getParametersList());
				LOG.info("Got TaskRequest (jobId={}, requestId={}) with params={}, ObjectData={}", new Object[] { jobId, reqId, params, req.getObject() });

				LOG.debug("Fetching data from object store, jobId={}, original ObjectData={}", jobId, req.getObject());
				ObjectData data = getDataFromObjectStore(jobId, req.getObject());
				LOG.debug("Got fresh data from object store: {}", data);

				ObjectDataWrapper objectData = new ObjectDataWrapper(data);
				TaskContext context = taskContextFactory.createTaskContext(jobId, reqId, objectData.getId(), connector);
				Task job = jobFactory.newTask(context, params, objectData);
				if (job.takesMuchTime()) {
					LOG.info("Sending TaskAccepted (jobId={}, reqId={}, ObjectData={})", new Object[] {jobId, reqId, req.getObject()});
					connector.sendTaskAccepted(jobId, reqId);
				}

				LOG.info("Processing task (jobId={}, reqId={})", jobId, reqId);
				job.process();
				LOG.debug("Task completed (jobId={}, reqId={})", jobId, reqId);

				context.flush();
				List<Long> newObjects = context.getAddedObjects();
				LOG.debug("Sending TaskComplete (jobId={}, reqId={})", jobId, reqId);
				if (context.hasWarnings()) {
					connector.sendTaskCompletedWithWarnings(jobId, reqId, newObjects, context.getWarnings());
				} else {
					connector.sendTaskComplete(jobId, reqId, newObjects);
				}
				LOG.info("TaskComplete sent (jobId={}, reqId={}, newObjects.count={})", new Object[] { jobId, reqId, newObjects.size() });
				LOG.debug("Task processing time: {} sec.", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - taskStartMilis));				
			}

		} catch (RequiredParameterMissingException e) {
			LOG.error("Required parameter missing (jobId={}, reqId={}): {}", new Object[] { jobId, reqId, e.getParamName() });
			connector.sendTaskError(jobId, reqId, e);
		} catch (InputDataException e) {
			logException("Input data error", jobId, reqId, e);
			connector.sendTaskError(jobId, reqId, e);
		} catch (ResourceException e) {
			logException("Error accessing resource", jobId, reqId, e);
			connector.sendTaskError(jobId, reqId, e);
		} catch (StorageException e) {
			logException("Error accessing storage", jobId, reqId, e);
			connector.sendTaskError(jobId, reqId, e);
		} catch (BusException e) {
			logError("Communication error!", jobId, reqId, e);
		} catch (InterruptedException e) {
			logError("Interrupted while processing job", jobId, reqId, e);
			throw e;
		} catch (Exception e) {
			logError("Exception caught while processing job", jobId, reqId, e);
			connector.sendTaskError(jobId, reqId, e);
		} catch (Throwable e) {
			logError("Fatal error caught while processing job", jobId, reqId, e);
			connector.sendTaskError(jobId, reqId, new RuntimeException(e));
		}
	}

    private void logError(String msg, long jobId, int reqId, Throwable e) {
        LOG.error("{} (jobId={}, reqId={}) :{}", new Object[]{msg, jobId, reqId, e.getMessage()});
        LOG.error(msg, e);
    }

    @Override
    public TaskContext createTaskContext(long jobId, int reqId, long objectDataId, ServiceConnector connector) {
        return new TaskContext(jobId, reqId, objectDataId, connector);
    }

    private ObjectData getDataFromObjectStore(long jobId, long objectId) throws StorageException, ShutdownSignalException, InterruptedException, IOException {
        List<Long> objectsId = new ArrayList<Long>();
        objectsId.add(objectId);
        List<ObjectData> objectDataList = getDataFromObjectStore(jobId, objectsId);
        if(!objectDataList.isEmpty()){
            return objectDataList.get(0);
        }
        else{
            throw new StorageException(String.format("Cannot retrieve ObjectData from object store, jobId=%s, dataId=%s", jobId, objectsId));
        }
    }

    private List<ObjectData> getDataFromObjectStore(long jobId, List<Long> objectsId) throws StorageException {
        try {
            ObjectResponse response = connector.getObjectStoreData(jobId, objectsId);
            return response.getDataList();
        } catch (StorageException e) {
            throw new StorageException(String.format("Cannot retrieve ObjectData from object store, jobId=%s, dataId=%s", jobId, objectsId), e);
        }
    }

    private void logException(String msg, long jobId, int reqId, Exception e) {
        LOG.error("{} (jobId={}, reqId={}): {}", new Object[]{msg, jobId, reqId, e.getMessage()});
        LOG.debug(msg, e);
    }
}
