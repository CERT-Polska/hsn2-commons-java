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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.MulticastEndPoint;
import pl.nask.hsn2.bus.rabbitmq.RbtDestination;
import pl.nask.hsn2.bus.rabbitmq.RbtUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;

public class RbtMulticastEndPoint implements MulticastEndPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(RbtMulticastEndPoint.class); 
	
	private static final String DEFAULT_CONTENT_TYPE = "application/hsn2+protobuf";
	private static final int TEMPORARY_QUEUE_LIVE_TIME = 300000; // 5min

	private static final int DEFAULT_WAIT = 300; // 5 min
	private Connection connection;
	private Channel channel = null;
	private boolean closed = true;
	private final String responseQueue = "tmp-" + java.util.UUID.randomUUID().toString();
	private int timeout = DEFAULT_WAIT;

	/**
	 * Constructor of <code>RbtMulticastEndPoint</code>.
	 * 
	 * @param connection Connection used for creation channel.
	 * @param timeout Maximum timeout in seconds for single
	 *        request. Default 5 min.
	 * @throws BusException Will thrown if any error occurs.
	 */
	public RbtMulticastEndPoint(Connection connection, int timeout) throws BusException {
		this.connection = connection;
		this.timeout = timeout;
		open();
	}

	/**
	 * Constructor of <code>RbtMulticastEndPoint</code>.
	 * Default timeout for single request is 5 min.
	 * 
	 * @param connection Connection used for creation channel. 
	 * @throws BusException Will thrown if any error occurs.
	 */
	public RbtMulticastEndPoint(Connection connection) throws BusException {
		this(connection, DEFAULT_WAIT);
	}

	@Override
	public final void open() throws BusException {
		if (isClosed()) {
			try {
				channel = connection.createChannel();
				channel.basicQos(1);
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("x-expires", TEMPORARY_QUEUE_LIVE_TIME);
				channel.queueDeclare(responseQueue, false, true, true, args);
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
	public final List<Message> sendMulticast(Message message, String[] servicesNames)
			throws BusException {

		if (servicesNames == null)
			throw new IllegalArgumentException("No multicast destinations provided.");
		
		BasicProperties.Builder propertiesBuilder = new BasicProperties.Builder()
				.contentType(DEFAULT_CONTENT_TYPE).type(message.getType())
				.replyTo(responseQueue);

		// setup correct correlation id if provided
		if (message.getCorrelationId() != null && !"".equals(message.getCorrelationId())) {
			propertiesBuilder.correlationId(message.getCorrelationId());
		}
		
		message.setDestination(new RbtDestination(responseQueue));
		try {
			for (String destination : servicesNames) {
				channel.basicPublish("", destination, propertiesBuilder.build(),
						message.getBody());
			}

			long currTime = System.currentTimeMillis();

			GetResponse response = null;
			List<Message> responseList = new LinkedList<Message>();

			int i = 0;
			while (System.currentTimeMillis() < currTime + ((long)timeout) * 1000
					&& i < servicesNames.length) {
				response = channel.basicGet(responseQueue, true);
				if (response != null) {
					responseList.add(new Message(response.getProps().getType(),
							response.getBody(), response.getProps().getCorrelationId(),
							new RbtDestination(response.getProps().getReplyTo())));
					i++;
				}
			}

			if (i < servicesNames.length) {
				LOGGER.debug(
						"There are no responses from all services (expected={}, got={}).",
						servicesNames.length, i);
			}
			return responseList;
		} catch (IOException e) {
			throw new BusException("Cannot send the message!", e);
		}
	}

	@Override
	public final boolean isClosed() {
		return closed;
	}

	@Override
	public final void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public final int getTimeout() {
		return this.timeout;
	}
}
