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

package pl.nask.hsn2.connector.AMQP;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.rabbitmq.RbtDestination;
import pl.nask.hsn2.connector.AbstractConnector;
import pl.nask.hsn2.connector.BusException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.QueueingConsumer;

public class InConnector extends AbstractConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(InConnector.class);

	private long deliveryTag;
	private final RbtDestination destination;

	public InConnector(String connectorAddress, RbtDestination destination)  throws BusException {
		super(connectorAddress, null, destination.getService());
		this.destination = destination;

		LOGGER.info(InConnector.class.getName() + " created. ConnectorAddress={}, Destination={} " + this, connectorAddress, destination);
	}

	@Override
	public void send(byte[] msg, String msgTypeName) {
		BasicProperties properties = new BasicProperties.Builder()
				.contentType(DEFAULT_MESSAGE_CONTENT_TYPE)
				.type(msgTypeName)
				.build();

		try {
			channel.basicPublish(destination.getExchange(), destination.getService(), properties, msg);
			LOGGER.debug("sent to {}: {}", destination, msg);
		} catch (IOException e) {
			LOGGER.error("Message was not sent!", e);
		}
	}
	public void sendWithAck(byte[] msg, String msgTypeName) {
		BasicProperties properties = new BasicProperties.Builder()
				.contentType(DEFAULT_MESSAGE_CONTENT_TYPE)
				.type(msgTypeName)
				.build();
		try {
			channel.basicPublish(destination.getExchange(), destination.getService(), properties, msg);
			channel.basicAck(deliveryTag, false);
			LOGGER.debug("sent to {}: {}", destination, msg);
		} catch (IOException e) {
			LOGGER.error("Message was not sent!", e);
		}
	}

	public void ackLastMessage() {
		try {
			channel.basicAck(deliveryTag, false);
		} catch (IOException e) {
			LOGGER.error("Last message was not acked!", e);
		}
	}

	@Override
	public byte[] receive() throws BusException {
	    QueueingConsumer.Delivery delivery;
		try {
			delivery = consumer.nextDelivery();
		} catch (InterruptedException e) {
			throw new BusException("Can't receive message.", e);
		}

	    String contentType = delivery.getProperties().getContentType();
	    deliveryTag = delivery.getEnvelope().getDeliveryTag();
		destination.setService(delivery.getProperties().getReplyTo());

		if (!DEFAULT_MESSAGE_CONTENT_TYPE.equals(contentType)){
			try {
				channel.basicAck(deliveryTag, false);
			} catch (IOException e) {
				LOGGER.error("Can't send ack.", e);
			}
			throw new IllegalArgumentException("Unknown context: " + contentType);
		}

		LOGGER.debug("receives: {}", delivery);
		return delivery.getBody();
	}

	@Override
	public void closeChannel() {
		try {
			channel.close();
		} catch (IOException e) {
			LOGGER.error("Can't close channel!", e);
		}
	}
}
