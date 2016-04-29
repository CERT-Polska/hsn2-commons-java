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

package pl.nask.hsn2.connector.AMQP;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.connector.AbstractConnector;
import pl.nask.hsn2.connector.BusException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.QueueingConsumer;

public class OutConnector extends AbstractConnector {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutConnector.class);
	private String objectStoreReplyQueueName = null;
	private String corrId = null;

	public OutConnector(String connectorAddress, String publisherQueueName) throws BusException{
		super(connectorAddress,publisherQueueName);
		try {
			objectStoreReplyQueueName = channel.queueDeclare().getQueue();
		} catch (IOException e) {
			throw new BusException("Can't create reply queue for ObjectStore.", e);
		}
		setConsumer(objectStoreReplyQueueName,true);
	}

	/* (non-Javadoc)
	 * @see pl.nask.hsn2.connector.Connector#send(byte[], java.lang.String)
	 */
	@Override
	public void send(byte[] msg, String msgTypeName) throws BusException {
		corrId = java.util.UUID.randomUUID().toString();
		BasicProperties properties = new BasicProperties.Builder()
				.contentType(DEFAULT_MESSAGE_CONTENT_TYPE)
				.type(msgTypeName)
				.replyTo(objectStoreReplyQueueName)
				.correlationId(corrId)
				.build();

		try {
			channel.basicPublish("", publisherQueueName, properties, msg);
			LOGGER.debug("Message sent to {}, type: {}, corrId: {}", new Object[]{publisherQueueName,msgTypeName,corrId});
		} catch (IOException e) {
			throw new BusException("Can't send message.", e);
		}
	}

	/* (non-Javadoc)
	 * @see pl.nask.hsn2.connector.Connector#receive()
	 */
	@Override
	public byte[] receive() throws BusException {
		QueueingConsumer.Delivery delivery = null;
		while(true){
			try {
				delivery = consumer.nextDelivery();
			} catch (InterruptedException e) {
				throw new BusException("Can't receive message.", e);
			}

			try {
				return checkDeliveredMessage(delivery);
			} catch (ResourceException e) {
				LOGGER.info(e.getMessage());
			}
		}
	}

	private byte[] checkDeliveredMessage(QueueingConsumer.Delivery delivery) throws ResourceException  {
		String contextType = delivery.getProperties().getContentType();
		if(!DEFAULT_MESSAGE_CONTENT_TYPE.equals(contextType)){
			throw new IllegalArgumentException("Unknown context: " + contextType);
		}
		if(corrId.equals(delivery.getProperties().getCorrelationId()) ){
			corrId = null;
			return delivery.getBody();
		}
		else{
			throw new ResourceException("Invalid correlationID: " + corrId);
		}
	}

	/* (non-Javadoc)
	 * @see pl.nask.hsn2.connector.AbstractConnector#closeChannel()
	 */
	@Override
	protected void closeChannel() {
		try {
			channel.close();
		} catch (IOException e) {
			LOGGER.error("Can't close channel!", e);
		}
	}
}
