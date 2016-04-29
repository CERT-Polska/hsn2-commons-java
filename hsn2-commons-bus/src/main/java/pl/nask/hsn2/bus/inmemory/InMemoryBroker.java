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

package pl.nask.hsn2.bus.inmemory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.Message;

/**
 * This is simple implemnetation of in memory message broker.
 * 
 * Intention of this class is to be used for test purpose only.
 * 
 *
 */
public final class InMemoryBroker {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryBroker.class);
	
	/**
	 * Internal queues.
	 */
	private static ConcurrentMap<String, BlockingQueue<Message>> queues = new ConcurrentHashMap<String, BlockingQueue<Message>>();

	/**
	 * Publishing single message onto the <code>destination</code> queue.
	 * 
	 * @param destination Destination queue.
	 * @param message Message to be published.
	 */
	public static void publish(final String destination, final Message message) {
		
		createQueue(destination);
		
		queues.get(destination).add(message);
	}

	/**
	 * Created queue if not exists.
	 * 
	 * @param qname Name of the queue to be created.
	 */
	public static void createQueue(final String qname) {
		queues.putIfAbsent(qname, new LinkedBlockingQueue<Message>());
		LOGGER.debug("Queue created: {}", qname);
	}

	/**
	 * Deletes queue.
	 * 
	 * @param qname Name of the queue to be deleted.
	 */
	public static void deleteQueue(final String qname) {
		queues.remove(qname);
		LOGGER.debug("Queue deleted: {}", qname);
	}
	
	/**
	 * Polls selected queue for message.
	 * 
	 * @param destination Where the message must coming from
	 * @param timeout How long to wait for the message (in seconds).
	 * @return Message or <code>null</code> if massage has not be found in timeout.
	 */
	public static Message get(final String destination, final int timeout) {
		if (!queues.containsKey(destination)) {
			throw new IllegalStateException("Queue is closed! qname=" + destination);
		}
		try {
			return queues.get(destination).poll(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new IllegalStateException("Cannot get message.");
		}
	}

	/**
	 * Count all messages in specified queue.
	 * 
	 * @param destination Name of the queue where messages will counted.
	 * @return Number of messages currently stored in the queue.
	 */
	public static int count(final String destination) {
		return queues.get(destination) != null ? queues.get(destination).size() : 0;
	}

	/**
	 * Deletes all messages from specified queue.
	 * @param destination Name of the queue to be purged.
	 */
	public static void purge(final String destination) {
		if (queues.get(destination) != null) {
			queues.get(destination).clear();
		}
	}
	
	/**
	 * Restarts the broker. In facts deletes all internal queues.
	 */
	public static void restart() {
		queues.clear();
	}
}
