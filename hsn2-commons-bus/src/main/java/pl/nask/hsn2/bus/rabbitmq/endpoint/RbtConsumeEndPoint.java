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

package pl.nask.hsn2.bus.rabbitmq.endpoint;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPoint;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPointHandler;
import pl.nask.hsn2.bus.rabbitmq.RbtUtils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RbtConsumeEndPoint implements ConsumeEndPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(RbtConsumeEndPoint.class);

	private String consumerQueueName = null;
	private ConsumeEndPointHandler responseHandler = null;
	private Connection connection;
	private Boolean closed = true;
	private final Object lock = new Object();
	private boolean autoack = true;
	private int numberOfThreads = 1;

	private Set<Channel> channels = new HashSet<Channel>(); 
	
	public RbtConsumeEndPoint(Connection connection,
			ConsumeEndPointHandler responseHandler,
			String consumerQueueName, boolean autoack)
					throws BusException {
		this(connection, responseHandler, consumerQueueName, autoack, 1);
	}

	public RbtConsumeEndPoint(Connection connection,
			ConsumeEndPointHandler responseHandler,
			String consumerQueueName, boolean autoack, int numberOfThreads)
					throws BusException {
		this.connection = connection;
		this.consumerQueueName = consumerQueueName;
		this.responseHandler = responseHandler;
		this.autoack = autoack;
		this.numberOfThreads = numberOfThreads;
		open();
	}

	@Override
	public final boolean isClosed() {
		return closed;
	}

	@Override
	public final void open() throws BusException {
		synchronized(lock) {
			if (isClosed()) {
				try {
					for (int consumeCount = 0; consumeCount<this.numberOfThreads; consumeCount++) {
						createConsumer();
					}
					closed = false;
				} catch (BusException ex) {
					for (Channel channel : this.channels) {
						RbtUtils.closeChannel(channel);
					}
					this.channels.clear();
					closed = true;
					throw ex;
				}
			}
		}
	}

	/**
	 * This method creates single consumer.
	 * 
	 * @throws BusException If there is a problem with creating consumer.
	 */
	private void createConsumer() throws BusException {
		Channel channel = null;
		try {
			channel = connection.createChannel();
		} catch (IOException e) {
			LOGGER.error("Error creating channel.");
			throw new BusException("Can't create channel.", e);
		}
		
		Consumer consumer = new RbtDefaultConsumer(autoack, channel, responseHandler) {
    		@Override
    		public void handleShutdownSignal(String consumerTag,
    				ShutdownSignalException sig) {
    			try {
    				close();
    			} catch(BusException ex) {
    				LOGGER.error("Error during shutdown customer.", ex);
    			}
    			super.handleShutdownSignal(consumerTag, sig);
    		}
		};

		try {
			channel.basicConsume(consumerQueueName, this.autoack, consumer);
			this.channels.add(channel);
		} catch (IOException e) {
			LOGGER.error("Error creating consumer.");
			throw new BusException("Can't create consumer.", e);
		}
	}
	
	@Override
	public final void close() throws BusException {
		synchronized(lock) {
			for (Channel channel : this.channels) {
				RbtUtils.closeChannel(channel);
			}
			this.channels.clear();
		}
		closed = true;
	}
}
