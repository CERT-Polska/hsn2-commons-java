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

package pl.nask.hsn2.bus.api.endpoint.observer;

import pl.nask.hsn2.bus.api.Message;

/**
 * This is an interface used by observers during processing
 * single message. It's very useful mechanism for validating
 * messages or add more features e.g. retry
 * 
 *
 */
public interface DeliveryObserver {

	/**
	 * This method will be invoked before processing message.
	 * 
	 * @param message
	 *            Message to be processed.
	 * @return <code>true</code> if there is no issues and message can be
	 *         processd, <code>false</code> otherwise.
	 */
	boolean observeBefore(Message message);
	
	/**
	 * This method will be invoked after processing message.
	 * 
	 * @param message Processed message.
	 */
	void observeAfter(Message message);

	/**
	 * This method will be invoked if there is a problem with processing message.
	 * 
	 * @param message Message which cannot be processed.
	 */
	void observeOnError(Message message);

}
