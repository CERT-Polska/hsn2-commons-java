/*
 * Copyright (c) NASK, NCSC
 * 
 * This file is part of HoneySpider Network 2.1.
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

package pl.nask.hsn2.bus.inmemory.endpoint;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPoint;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPointHandler;
import pl.nask.hsn2.bus.api.endpoint.ConsumeHandlerException;
import pl.nask.hsn2.bus.inmemory.InMemoryBroker;
import pl.nask.hsn2.bus.utils.ConfigurableExecutorService;

public class InMemoryConsumeEndPoint implements ConsumeEndPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryConsumeEndPoint.class);

	private Boolean closed = true;
	private final Object lock = new Object();
	private int numberOfThreads = 10;
	private String consumerQueueName = null;
	private ConsumeEndPointHandler responseHandler = null;
	
	private final ThreadPoolExecutor executor = new ConfigurableExecutorService("test-consumer-", numberOfThreads);

	public InMemoryConsumeEndPoint(String consumerQueueName, ConsumeEndPointHandler responseHandler) {
		this.consumerQueueName = consumerQueueName;
		this.responseHandler = responseHandler;
		open();
	}
	
	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void open() {
		synchronized(lock) {
			if (isClosed()) {
				for (int i=0 ; i<this.numberOfThreads; i++) {
					executor.execute(new Runnable() {
						@Override
						public void run() {
							while (!executor.isTerminating()) {
								Message message = InMemoryBroker.get(consumerQueueName, 1);
								if (message != null) {
									LOGGER.debug("Consuming: {}", message);
									try {
										responseHandler.handleMessage(message);
									} catch (ConsumeHandlerException e) {
										LOGGER.error("Error consuming message.");
									}
								}
							}
						}
					});
				}
				
				closed = false;
			}
		}
	}

	@Override
	public void close() throws BusException {
		synchronized (lock) {
			executor.shutdown();
			closed = true;
		}
	}


}
