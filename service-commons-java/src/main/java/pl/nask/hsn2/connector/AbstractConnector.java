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

package pl.nask.hsn2.connector;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public abstract class AbstractConnector implements Connector {

	protected static Connection connection = null;
	protected Channel channel = null;
	protected QueueingConsumer consumer = null;
	protected String publisherQueueName = null;

	protected AbstractConnector(String connectorAddress, String publisherQueueName) throws BusException{
		if(connection == null){
			connect(connectorAddress);
		}
		try {
			channel = connection.createChannel();
			channel.basicQos(1);
		} catch (IOException e) {
			throw new BusException("Can't create channel.", e);
		}

		this.publisherQueueName = publisherQueueName;
	}

	private void connect(String connectorAddress) throws BusException{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(connectorAddress);
		try {
			connection = factory.newConnection();
		} catch (IOException e) {
			throw new BusException("Can't create connection.", e);
		}
	}
	protected AbstractConnector(String connectorAddress, String publisherQueueName, String consumerQueueName) throws BusException{
		this(connectorAddress, publisherQueueName);
		setConsumer(consumerQueueName,false);
	}

	/* (non-Javadoc)
	 * @see pl.nask.hsn2.connector.Connector#close()
	 */
	@Override
	public void close() throws BusException {
		try {
			if (connection != null && connection.isOpen()){
				connection.close(500);
			}
		} catch (IOException e) {
			throw new BusException("Can't close connection.",e);
		}
	}

	protected void setConsumer(String consumerQueueName, boolean autoACK) throws BusException{
		consumer = new QueueingConsumer(channel);
		try {
			channel.basicConsume(consumerQueueName, autoACK, consumer);
		} catch (IOException e) {
			close();
			throw new BusException(e);
		}
	}

	abstract protected void closeChannel();
}
