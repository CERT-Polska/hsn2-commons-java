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

package pl.nask.hsn2.bus.api.endpoint.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;

/**
 * This <code>DeliveryObserver</code> is responsible for checking if
 * the message re-delivery count has exceeded. If so message will send
 * to deadletter. If there will be any processing problem message will
 * be re-delivered.
 * 
 *
 */
public class RetryDeliveryObserver implements DeliveryObserver {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetryDeliveryObserver.class);
	
	private static final int DEFULT_RETRIES = 3;
	private static final String DEFAULT_DEADLETTER_ROUTINGKEY = "deadletter";

	private int retries = DEFULT_RETRIES;
	private String deadletter = DEFAULT_DEADLETTER_ROUTINGKEY;
	private FireAndForgetEndPoint notificationEndPonit;
	
	/**
	 * Default constructor.
	 * @param notificationEndPonit
	 */
	public RetryDeliveryObserver(FireAndForgetEndPoint notificationEndPonit) {
		this.notificationEndPonit = notificationEndPonit;
	}

	/**
	 * Sets deadletter routing key.
	 * @param deadletter Deadletter routing key.
	 */
	public final void setDeadletterRoutingKey(String deadletter) {
		this.deadletter = deadletter;
	}

	/**
	 * Sets number of retries.
	 * 
	 * @param retries Number of retries.
	 */
	public final void setRetries(int retries) {
		this.retries = retries;
	}
	
	@Override
	public final boolean observeBefore(Message message) {
		if (message.getRetries() >= retries) {
			LOGGER.debug("Retries limit exeeded ({}) sending message to deadletter.", retries);
			try {
				message.setDestination(new Destination(deadletter));
				notificationEndPonit.sendNotify(message);
			} catch (BusException e) {
				LOGGER.error("Error sending message to deadletter. Dropped.");
			}
			return false;
		}
		return true;
	}

	@Override
	public final void observeAfter(Message message) {
		// correct consumption, nothing to do
	}

	@Override
	public final void observeOnError(Message message) {
		message.setRetries(message.getRetries()+1);
		try {
			LOGGER.debug("Redelivering message: {}", message);
			this.notificationEndPonit.sendNotify(message);
		} catch (BusException ex) {
			LOGGER.error("Error redelivery message. Dropped.");
		}
	}
}
