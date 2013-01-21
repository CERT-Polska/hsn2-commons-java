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

package pl.nask.hsn2.bus.rabbitmq;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * This is utility class for RabbitMQ endpoint implementation.
 * 
 *
 */
public abstract class RbtUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(RbtUtils.class);
	
	/**
	 * Hidden constructor
	 */
	private RbtUtils() {
		// this is utility class
	}
	
	/**
	 * Closing quietly RabbitMQ connection.
	 * 
	 * @param connection <code>com.rabbitmq.client.Connection</code> to close.
	 */
	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				if (connection.isOpen()) {
					connection.close();
				}
			} catch (IOException ex) {
				LOGGER.debug("Ignoring Connection state exception - assuming already closed: "
						+ ex);
			} catch (Exception ex) {
				LOGGER.debug(
						"Unexpected exception on closing RabbitMQ Connection",
						ex);
			}
		}
	}

	/**
	 * Closing quietly RabbitMQ channel.
	 * 
	 * @param channel <code>com.rabbitmq.client.Channel</code> to close.
	 */
	public static void closeChannel(Channel channel) {
		if (channel != null) {
			try {
				channel.close();
			} catch (IOException ex) {
				LOGGER.trace("Could not close RabbitMQ channel", ex);
			} catch (Exception ex) {
				LOGGER.trace(
						"Unexpected exception on closing RabbitMQ channel", ex);
			}
		}
	}
	
	/**
	 * Removes queue.
	 * 
	 * @param channel Channel for queue deletion.
	 * @param queueName Name of the queue to be deleted.
	 */
	public static void deleteQueue(Channel channel, String queueName) {
		try {
			channel.queueDelete(queueName);
		} catch (IOException ex) {
			LOGGER.error("Error during deletion queue: {}", queueName);
		}
	}

	/**
	 * Removes queue.
	 * 
	 * @param connection Connection to Rabbit MQ server.
	 * @param queueName Name of the queue to be deleted.
	 */
	public static void deleteQueue(Connection connection, String queueName) {
		Channel channel = null;
		try {
			channel = createChannel(connection);
			deleteQueue(channel, queueName);
		} catch (BusException e) {
			LOGGER.error("Error during deletion queue: {}", queueName);
		} finally {
			closeChannel(channel);
		}
	}

	/**
	 * Removed all messages from the queue.
	 * 
	 * @param connection Connection to Rabbit MQ server.
	 * @param queueName Name of the queue to be purged.
	 */
	public static void purgeQueue(Connection connection, String queueName) {
		Channel channel = null;
		try {
			channel = createChannel(connection);
			purgeQueue(channel, queueName);
		} catch (BusException e) {
			LOGGER.error("Error during purging queue: {}", queueName);
		} finally {
			closeChannel(channel);
		}
	}

	/**
	 * Removed all messages from the queue.
	 * 
	 * @param connection Connection to Rabbit MQ server.
	 * @param queueName Name of the queue to be purged.
	 */
	public static void purgeQueue(Channel channel, String queueName) {
		try {
			channel.queuePurge(queueName);
		} catch (IOException ex) {
			LOGGER.error("Error during purging queue: {}", queueName);
		}
	}

	/**
	 * Creates single queue.
	 * 
	 * @param channel Channel to create a queue.
	 * @param queueName Name of the queue to be created.
	 * @throws BusException Any problem will rise the exception.
	 */
	public static void createQueue(Channel channel, String servicesExchangeName, String queueName) throws BusException {
		try {
			channel.queueDeclare(queueName,false,false,false,null);
			channel.queueBind(queueName, servicesExchangeName, queueName);
		} catch (IOException e) {
			throw new BusException("Can not create queue: " + queueName, e);
		}
	}

	/**
	 * Creates single queue.
	 * 
	 * @param connection Connection to Rabbit MQ server.
	 * @param queueName Name of the queue to be created.
	 * @throws BusException Any problem will rise the exception.
	 */
	public static void createQueue(Connection connection, String servicesExchangeName, String queueName) throws BusException {
		Channel channel = null;
		try {
			channel = createChannel(connection);
			channel.queueDeclare(queueName,false,false,false,null);
			channel.queueBind(queueName, servicesExchangeName, queueName);
		} catch (IOException e) {
			throw new BusException("Can not create queue: " + queueName, e);
		} finally {
			closeChannel(channel);
		}
	}

	/**
	 * Creates many queues.
	 * 
	 * @param connection Connection to Rabbit MQ server.
	 * @param queues Array of queue names.
	 * @throws BusException Any problem will rise the exception.
	 */
	public static void createQueues(Connection connection, String servicesExchangeName, String[] queues) throws BusException {
		createQueues(connection, servicesExchangeName, queues, false);
	}
	
	/**
	 * Creates many queues. Purges the queue if 'purge' is set to true
	 * 
	 * @param connection Connection to the Rabbit MQ server
	 * @param queueNames names of the queues to be created
	 * @param purge indicates, if the queues need to be purged on creation.
	 */
	public static void createQueues(Connection connection, String servicesExchangeName, String[] queueNames, boolean purge) throws BusException {
		Channel channel = createChannel(connection);
		try {
			for (String queueName : queueNames) {
				createQueue(channel, servicesExchangeName, queueName);
				if (purge)
					purgeQueue(channel, queueName);
			}
		} finally {
			closeChannel(channel);
		}
		
	}
	
	/**
	 * Creates channel.
	 * 
	 * @param connection Connection to Rabbit MQ server.
	 * @throws BusException Any problem will rise the exception.
	 */
	public static Channel createChannel(Connection connection) throws BusException {
		try {
			return connection.createChannel();
		} catch (IOException e) {
			throw new BusException("Can not create channel.", e);
		}
	}
	
	/**
	 * Gets a count of messages in given queue.
	 * 
	 * @param connection Connection to Rabbit MQ server.
	 * @param queueName Name of the queue to check.
	 * @return Number of messages in the queue.
	 * @throws BusException Any problem with connection or queue will thrown an exception.
	 */
	public static int getMessageCount(Connection connection, String queueName) throws BusException {
		Channel channel = createChannel(connection);
		try {
			DeclareOk ok = channel.queueDeclarePassive(queueName);
			return ok.getMessageCount();
		} catch (IOException ex) {
			throw new BusException(ex);
		} finally {
			closeChannel(channel);
		}
	}

	/**
	 * Gets a count of consumers attached to given queue.
	 * 
	 * @param connection Connection to Rabbit MQ server.
	 * @param queueName Name of the queue to check.
	 * @return Number of consumers attached to the queue.
	 * @throws BusException Any problem with connection or queue will thrown an exception.
	 */
	public static int getConsumersCount(Connection connection, String queueName) throws BusException {
		Channel channel = createChannel(connection);
		try {
			DeclareOk ok = channel.queueDeclarePassive(queueName);
			return ok.getConsumerCount();
		} catch (IOException ex) {
			throw new BusException(ex);
		} finally {
			closeChannel(channel);
		}
	}

	/**
	 * Creates exchanges and static bindings between them.
	 * 
	 * @param connection Connection to Rabbit MQ server.
	 * @param exchangeCommonName Common exchange name.
	 * @param exchangeServicesName Services exchange name.
	 * @param exchangeMonitoringName Monitoring exchange name.
	 * @throws BusException Any problem with connection or queue will thrown an exception.
	 */
	public static void createExchanges(Connection connection, String exchangeCommonName, String exchangeServicesName, String exchangeMonitoringName) throws BusException {
		Channel channel = createChannel(connection);
		try {
			channel.exchangeDeclare(exchangeCommonName, "fanout");
			channel.exchangeDeclare(exchangeMonitoringName, "fanout");
			channel.exchangeDeclare(exchangeServicesName, "direct");
			channel.exchangeBind(exchangeMonitoringName, exchangeCommonName, "");
			channel.exchangeBind(exchangeServicesName, exchangeCommonName, "");
		} catch (IOException ex) {
			throw new BusException(ex);
		} finally {
			closeChannel(channel);
		}
	}
}
