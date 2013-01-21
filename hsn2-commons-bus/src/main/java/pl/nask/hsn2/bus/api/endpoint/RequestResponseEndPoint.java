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

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;

/**
 * This type of <code>EndPoint</code> is addressed
 * to be used for request-response calls for single calls.
 * E.g. when temporary queues will used is bus is based on it.
 * 
 *
 */
public interface RequestResponseEndPoint extends EndPoint {

	/**
	 * Sends single message and receive response.
	 * 
	 * @param message Message to be sent.
	 * 
	 * @return Response message.
	 * 
	 * @throws BusException Exception will thrown if error.
	 */
	Message sendAndGet(Message message) throws BusException;

	/**
	 * Sets timeout.
	 * 
	 * @param timeout Timeout in seconds.
	 */
	void setTimeout(int timeout);

	/**
	 * Gets a current value of the timeout property.
	 * 
	 * @return Timeout in seconds.
	 */
	int getTimeout();

}
