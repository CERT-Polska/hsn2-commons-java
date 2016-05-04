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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPointHandler;
import pl.nask.hsn2.bus.api.endpoint.ConsumeHandlerException;
import pl.nask.hsn2.bus.rabbitmq.RbtDestination;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RbtDefaultConsumer extends DefaultConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(RbtDefaultConsumer.class);
	private static final String RABBITMQ_DEFAULT_EXCHANGE_NAME = "";
	
	private ConsumeEndPointHandler responseHandler = null;
	
	private boolean autoack = true;
	
	public RbtDefaultConsumer(Channel channel, ConsumeEndPointHandler responseHandler) {
		super(channel);
		this.responseHandler = responseHandler;
	}

	public RbtDefaultConsumer(boolean autoack, Channel channel, ConsumeEndPointHandler responseHandler) {
		super(channel);
		this.responseHandler = responseHandler;
		this.autoack = autoack;
	}

	@Override
	public final void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {

		super.handleDelivery(consumerTag, envelope, properties, body);

		Message message = new Message(
		properties.getType(),
		body,
		properties.getCorrelationId(),
		new RbtDestination(RABBITMQ_DEFAULT_EXCHANGE_NAME, properties.getReplyTo()));
		
		message.setDestination(new RbtDestination(RABBITMQ_DEFAULT_EXCHANGE_NAME, envelope.getRoutingKey()));
		
		message.setContentType(properties.getContentType());

		// take retries count
		try {
			if (properties.getHeaders() != null) {
				Object xretriesObject = properties.getHeaders().get("x-retries");
				if (xretriesObject != null) {
					int xretries = 0;
					if (xretriesObject instanceof Integer) {
						xretries = (Integer) xretriesObject;
					} else if (xretriesObject instanceof String) {
						xretries = Integer.parseInt((String)xretriesObject);
					} else {
						LOGGER.error("Unknown object type of x-retries property.");
					}
					message.setRetries(xretries);
				}
			}
		} catch(NumberFormatException ex) {
			// not important
		}

		try {
			responseHandler.handleMessage(message);
			if (!autoack) {
				getChannel().basicAck(envelope.getDeliveryTag(), false);
			}
		} catch (ConsumeHandlerException ex) {
			if (!autoack) {
				getChannel().basicReject(envelope.getDeliveryTag(), true);
			}
			// nothing can do :(
		} catch (Throwable t) {
			if (!autoack) {
				getChannel().basicReject(envelope.getDeliveryTag(), true);
			}
			LOGGER.error("Error handling message.", t);
		}
	}
}
