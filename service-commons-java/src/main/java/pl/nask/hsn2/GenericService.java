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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.task.TaskContextFactory;
import pl.nask.hsn2.task.TaskFactory;


public class GenericService implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericService.class);

    private String connectorAddress = null;
	private String objectStoreQueueName = null;
	private String dataStoreAddress = null;
	private String serviceQueueName = null;
    private String serviceName = null;
    private final String commonExchangeName;
    private final String notifyExchangeName;

    private ExecutorService executor;
    private int maxThreads = 1;
    private final CountDownLatch startUpLatch = new CountDownLatch(1);
    
    private FinishedJobsListener finishedJobsListener;

    List<TaskProcessor> taskProcessors;

    private final TaskFactory jobFactory;

    private TaskContextFactory contextFactory;

	private UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    public GenericService(TaskFactory jobFactory, Integer maxThreads, String rbtCommonExchangeName, String rbtNotifyExchangeName) {
        this(jobFactory, null, maxThreads, rbtCommonExchangeName, rbtNotifyExchangeName);
    }

    public GenericService(TaskFactory jobFactory, TaskContextFactory contextFactory, Integer maxThreads, String rbtCommonExchangeName, String rbtNotifyExchangeName) {
        this.jobFactory = jobFactory;
        this.contextFactory = contextFactory;
        if (maxThreads != null && maxThreads > 0) {
            this.maxThreads = maxThreads;
        }
        executor = Executors.newFixedThreadPool(this.maxThreads);
        taskProcessors = new ArrayList<>(this.maxThreads);
        this.commonExchangeName = rbtCommonExchangeName;
        this.notifyExchangeName = rbtNotifyExchangeName;
        this.finishedJobsListener = new FinishedJobsListener();
    }

	public void run() {
		Thread.setDefaultUncaughtExceptionHandler(defaultUncaughtExceptionHandler);
	    startTaskProcessors();
        startFinishedJobsListener();
        startUpLatch.countDown();
    }

	List<Future<Void>> startTaskProcessors() {
		List<Future<Void>> results = new ArrayList<>(maxThreads);
		for (int i = 0; i < maxThreads; i++) {
	        TaskProcessor processor = prepareTaskProcessor();
            taskProcessors.add(processor);
            Future<Void> result = executor.submit(processor);
            results.add(result);
            LOGGER.info("TaskProcessor {} started.", result);
        }
	    return results;
	}
	
	private void startFinishedJobsListener(){
		finishedJobsListener.initialize(connectorAddress, notifyExchangeName);
		new Thread(finishedJobsListener).start();
	}
	
	public void stop() {
		LOGGER.info("Shutting down");
		for (TaskProcessor p: taskProcessors) {
			p.setCanceled();
		}
		executor.shutdownNow();
		finishedJobsListener.shutdown();
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
	
	public void waitForStartUp() throws InterruptedException {
			startUpLatch.await();
	}

	public String getServiceName(){
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
		GenericServiceInfo.setServiceName(serviceName);
	}

	public void setDefaultUncaughtExceptionHandler(
			UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
		this.defaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
		
	}
}
