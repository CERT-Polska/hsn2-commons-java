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
import java.util.HashMap;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.rabbitmq.RbtDestination;
import pl.nask.hsn2.bus.rabbitmq.RbtUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * This is RabbitMQ implementation of <code>NotificationEndPoint</code>.
 * This class is NOT tread-safe we need instance for each thread,
 * because <code>Channel</code> is not safe and cannot be shared
 * between threads.
 * 
 *
 */
public class RbtFireAndForgetEndPoint implements FireAndForgetEndPoint {

	private static final String DEFAULT_CONTENT_TYPE = "application/hsn2+protobuf";

	private Connection connection;
	private Channel channel = null;
	private boolean closed = true;

	/**
	 * Constructor of <code>RbtNotificationEndPoint</code>.
	 * 
	 * @param connection Connection used for creation channel.
	 * @throws BusException Will thrown if any error occurs.
	 */
	public RbtFireAndForgetEndPoint(Connection connection) throws BusException {
		this.connection = connection;
		open();
	}
	
	@Override
	public final boolean isClosed() {
		return closed;
	}

	@Override
	public final void open() throws BusException {
		if (isClosed()) {
			try {
				channel = connection.createChannel();
				channel.basicQos(1);
			} catch (IOException e) {
				throw new BusException("Can't create channel.", e);
			}
			closed = false;
		}
	}

	@Override
	public final void close() throws BusException {
		RbtUtils.closeChannel(channel);
		channel = null;
		closed = true;
	}

	@Override
	public final void sendNotify(Message message) throws BusException {
		// Properties below uses only service name (String, not Destination) as replyTo parameter.
		String replyToQueueName = message.getReplyTo().getService();
		
		HashMap<String, Object> headers = new HashMap<String, Object>();
		headers.put("x-retries", message.getRetries());
		BasicProperties.Builder propertiesBuilder = new BasicProperties.Builder()
		.headers(headers)
		.contentType(DEFAULT_CONTENT_TYPE)
		.replyTo(replyToQueueName)
		.type(message.getType());
		
		// setup correct correlation id if provided
		if (message.getCorrelationId() != null && !"".equals(message.getCorrelationId())) {
			propertiesBuilder.correlationId(message.getCorrelationId());
		}
		String destinationRoutingKey = ((RbtDestination)(message.getDestination())).getService();
		String destinationExchange = ((RbtDestination)(message.getDestination())).getExchange();
		try {
			channel.basicPublish(destinationExchange, destinationRoutingKey, propertiesBuilder.build(), message.getBody());
		}
		catch (IOException e) {
			throw new BusException("Cannot sent message!", e);
		}

	}

	@Override
	public final void spread(Message message, String[] servicesNames)
			throws BusException {
		BasicProperties.Builder propertiesBuilder = new BasicProperties.Builder()
		.contentType(DEFAULT_CONTENT_TYPE)
		.type(message.getType());
		
		try {
			for (String destination : servicesNames) {
				channel.basicPublish("", destination, propertiesBuilder.build(), message.getBody());
			}
		} catch (IOException e) {
			throw new BusException("Cannot sent message!", e);
		}
	}
}
