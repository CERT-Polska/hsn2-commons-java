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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.task.TaskContextFactory;
import pl.nask.hsn2.task.TaskFactory;


public class GenericService {
    private static final Logger LOG = LoggerFactory.getLogger(GenericService.class);

    private String connectorAddress = null;
	private String objectStoreQueueName = null;
	private String dataStoreAddress = null;
	private String serviceQueueName = null;
    private String serviceName = null;
    private final String commonExchangeName;
    private final String notifyExchangeName;

    private ExecutorService executor;
    private int maxThreads;
    private final CountDownLatch startUpLatch = new CountDownLatch(1);
    
    private FinishedJobsListener finishedJobsListener;

    List<TaskProcessor> taskProcessors;

    private final TaskFactory jobFactory;

    private TaskContextFactory contextFactory;

    public GenericService(TaskFactory jobFactory, Integer maxThreads, String rbtCommonExchangeName, String rbtNotifyExchangeName) {
        this(jobFactory, null, maxThreads, rbtCommonExchangeName, rbtNotifyExchangeName);
    }

    public GenericService(TaskFactory jobFactory, TaskContextFactory contextFactory, Integer maxThreads, String rbtCommonExchangeName, String rbtNotifyExchangeName) {
        this.jobFactory = jobFactory;
        this.contextFactory = contextFactory;
        if (maxThreads == null || maxThreads == 0) {
            this.maxThreads = 1;
        } else {
            this.maxThreads = maxThreads;
        }
        executor = Executors.newFixedThreadPool(this.maxThreads);
        taskProcessors = new ArrayList<TaskProcessor>(this.maxThreads);
        this.commonExchangeName = rbtCommonExchangeName;
        this.notifyExchangeName = rbtNotifyExchangeName;
        this.finishedJobsListener = new FinishedJobsListener();
    }

	public void run() throws InterruptedException {
	    List<Future<Void>> results = start();
        for (Future<Void> res: results) {
            LOG.info("TaskProcessor {} started.", res);
        }

		finishedJobsListener.initialize(connectorAddress, notifyExchangeName);
		new Thread(finishedJobsListener).start();
		startUpLatch.countDown();
    }

	List<Future<Void>> start() throws InterruptedException {
		List<Future<Void>> results = new ArrayList<Future<Void>>(maxThreads);
		for (int i=0; i<maxThreads; i++) {
	        TaskProcessor processor = prepareTaskProcessor();
            taskProcessors.add(processor);
            results.add(executor.submit(processor));
        }
	    return results;
	}
	
	public void stop() {
		LOG.info("Shutting down");
		for (TaskProcessor p: taskProcessors) {
			p.setCanceled();
		}
		executor.shutdownNow();		
	}

    private TaskProcessor prepareTaskProcessor() {
        ServiceConnector connector = new ServiceConnectorImpl(connectorAddress, serviceQueueName, commonExchangeName, objectStoreQueueName, dataStoreAddress);
        if (contextFactory == null) {
            return new TaskProcessor(jobFactory, connector, finishedJobsListener);
        } else {
            return new TaskProcessor(jobFactory, connector, contextFactory, finishedJobsListener);
        }
    }

    public void setDataStoreAddress(String dataStoreAddress) {
        this.dataStoreAddress = dataStoreAddress;
    }

    public String getDataStoreAddress() {
        return dataStoreAddress;
    }

    public String getConnectorAddress() {
		return connectorAddress;
	}

	public void setConnectorAddress(String connectorAddress) {
		this.connectorAddress = connectorAddress;
	}

	public String getObjectStoreQueueName() {
		return objectStoreQueueName;
	}

	public void setObjectStoreQueueName(String objectStoreQueueName) {
		this.objectStoreQueueName = objectStoreQueueName;
	}

	public String getServiceQueueName() {
		return serviceQueueName;
	}

	public void setServiceQueueName(String serviceQueueName) {
		this.serviceQueueName = serviceQueueName;
	}
	
	public boolean waitForStartUp(Long waitTime) {
		try {
			if ( waitTime == null || waitTime < 1l) {
				startUpLatch.await();
				return true;
			} else {
				return startUpLatch.await(waitTime, TimeUnit.MILLISECONDS);
			}

		} catch (InterruptedException e) {
			LOG.warn("Interrupted while waiting for startup");
		}
		return false;
	}

	public String getServiceName(){
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
		GenericServiceInfo.setServiceName(serviceName);
	}
}
