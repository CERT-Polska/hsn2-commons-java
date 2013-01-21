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

package pl.nask.hsn2.bus.rabbitmq.endpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.TimeoutException;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;
import pl.nask.hsn2.bus.rabbitmq.RbtDestination;
import pl.nask.hsn2.bus.rabbitmq.RbtUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;

/**
 * This is RabbitMQ implementation of <code>RequestResponseEndPoint</code>.
 * This class is NOT tread-safe we need instance for each thread,
 * because <code>Channel</code> is not safe and cannot be shared
 * between threads.
 * 
 *
 */
public class RbtRequestResponseEndPoint implements RequestResponseEndPoint {
	private static final String DEFAULT_CONTENT_TYPE = "application/hsn2+protobuf";
	private static final int DEFAULT_WAIT = 300; // 5 min

	private Connection connection;
	private Channel channel = null;
	private boolean closed = true;
	private final String responseQueue = "tmp-" + java.util.UUID.randomUUID().toString();
	private int timeout = DEFAULT_WAIT;
	
	/**
	 * Constructor of <code>RbtRequestResponseEndPoint</code>.
	 * 
	 * @param connection Connection used for creation channel.
	 * @param timeout Maximum timeout in seconds for single
	 *        request. Default 5 min.
	 * @throws BusException Will thrown if any error occurs.
	 */
	public RbtRequestResponseEndPoint(Connection connection, int timeout) throws BusException {
		this.connection = connection;
		this.timeout = timeout;
		open();
	}

	/**
	 * Constructor of <code>RbtRequestResponseEndPoint</code>.
	 * Default timeout for single request is 5 min.
	 * 
	 * @param connection Connection used for creation channel. 
	 * @throws BusException Will thrown if any error occurs.
	 */
	public RbtRequestResponseEndPoint(Connection connection) throws BusException {
		this(connection, DEFAULT_WAIT);
	}

	@Override
	public final void open() throws BusException {
		if (isClosed()) {
			try {
				channel = connection.createChannel();
				channel.basicQos(1);
				Map<String, Object> args = new HashMap<String, Object>();
				channel.queueDeclare(responseQueue, false, true, true, args);
			} catch (IOException e) {
				throw new BusException("Can't create channel.", e);
			}
			closed = false;
		}
	}
	
	@Override
	public final void close() throws BusException {
		RbtUtils.deleteQueue(channel, responseQueue);
		RbtUtils.closeChannel(channel);
		channel = null;
		closed = true;
	}

	@Override
	public final Message sendAndGet(Message message) throws BusException {
		BasicProperties.Builder propertiesBuilder = new BasicProperties.Builder()
		.contentType(DEFAULT_CONTENT_TYPE)
		.type(message.getType())
		.replyTo(responseQueue);
		
		// setup correct correlation id if provided
		if (message.getCorrelationId() != null && !"".equals(message.getCorrelationId())) {
			propertiesBuilder.correlationId(message.getCorrelationId());
		}
		
		String destinationRoutingKey = message.getDestination().getService();
		String destinationExchange = ((RbtDestination)(message.getDestination())).getExchange();
		message.setReplyTo(new RbtDestination(responseQueue));
		
		try {
			channel.basicPublish(destinationExchange, destinationRoutingKey, propertiesBuilder.build(), message.getBody());
			
			long currTime = System.currentTimeMillis();
			
			GetResponse response = null;

			while (response == null  && System.currentTimeMillis() < currTime + ((long)timeout) * 1000) {
				response = channel.basicGet(responseQueue, true);
			}
				
			if (response == null) {
				throw new TimeoutException("Cannot get message, timeout after " + timeout + " seconds!");
			}

			return new Message(
					response.getProps().getType(),
					response.getBody(),
					response.getProps().getCorrelationId(),
					new RbtDestination(response.getProps().getReplyTo()));
		}
		catch (IOException e) {
			throw new BusException("Cannot sent message!", e);
		}
	}

	@Override
	public final boolean isClosed() {
		return closed;
	}
	
	/**
	 * Sets timeout.
	 * 
	 * @param timeout Timeout in seconds.
	 */
	public final void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public final int getTimeout() {
		return this.timeout;
	}
}
