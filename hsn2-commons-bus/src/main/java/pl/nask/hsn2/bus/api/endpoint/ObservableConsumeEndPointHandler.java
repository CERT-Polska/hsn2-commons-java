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

package pl.nask.hsn2.bus.api.endpoint;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.observer.DeliveryObserver;

/**
 * This is implementation of <code>ConsumeEndPointHandler</code> which allows to
 * add a list of <code>DeliveryObserver</code>s. The observers will be invoked
 * each time when message will handle. There are 3 hooks: before execution,
 * after execution and on error.
 * 
 * 
 */
public class ObservableConsumeEndPointHandler implements ConsumeEndPointHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObservableConsumeEndPointHandler.class);
	
	private List<DeliveryObserver> observers = new LinkedList<DeliveryObserver>();

	private ConsumeEndPointHandler delegate;

	/**
	 * Default constructor.
	 * 
	 * @param delegate <code>ConsumeEndPointHandler</code> to be wrapped.
	 */
	public ObservableConsumeEndPointHandler(ConsumeEndPointHandler delegate) {
		this.delegate = delegate;
	}

	@Override
	public final void handleMessage(Message message) throws ConsumeHandlerException {
		boolean process = true;
		for (DeliveryObserver observer : observers) {
			process = process && observer.observeBefore(message);
		}
		if (process) {
			try {
				delegate.handleMessage(message);
			} catch (ConsumeHandlerException ex) {
				for (DeliveryObserver observer : observers) {
					observer.observeOnError(message);
				}
				throw ex;
			}
			for (DeliveryObserver observer : observers) {
				observer.observeAfter(message);
			}
		} else {
			LOGGER.debug("Message processing stoped by observers.");
		}
	}

	/**
	 * Adds <code>DeliveryObserver</code> to the list of the observers.
	 * 
	 * @param observer Single <code>DeliveryObserver</code> to be added.
	 */
	public final void addDeliveryObserver(DeliveryObserver observer) {
		this.observers.add(observer);
	}
}
