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

/**
 * This is unified interface of the factory for creating endpoints.
 * 
 *
 */
public interface EndPointFactory {

	/**
	 * Creates consuming endpoint.
	 * 
	 * @param messagesHandler Callback for processing incoming messages.
	 * @param routingKey Where consumer should be attached to.
	 * 
	 * @return New instance of <code>ConsumeEndPoint</code>.
	 * @throws BusException Any problem with creating endpoint will rise the exception.
	 */
	ConsumeEndPoint createConsumeEndPoint(
			ConsumeEndPointHandler messagesHandler,
			String routingKey) throws BusException;

	/**
	 * Creates consuming endpoint with autoack falg.
	 * 
	 * @param messagesHandler Callback for processing incoming messages.
	 * @param routingKey Where consumer should be attached to.
	 * @param autoack Flag if messages should be auto acknowledges or not.
	 * 
	 * @return New instance of <code>ConsumeEndPoint</code>.
	 * @throws BusException Any problem with creating endpoint will rise the exception.
	 */
	ConsumeEndPoint createConsumeEndPoint(
			ConsumeEndPointHandler messagesHandler,
			String routingKey, boolean autoack) throws BusException;

	/**
	 * Creates request-response type endpoint (based on single thread).
	 * 
	 * @return New instance of <code>RequestResponseEndPoint</code>.
	 * @throws BusException Any problem with creating endpoint will rise the exception.
	 */
	RequestResponseEndPoint createRequestResponseEndPoint()
			throws BusException;	

	/**
	 * Creates request-response type endpoint (multi-threaded implementation).
	 * 
	 * @param maxThreads Number of threads, if 0 or 1 single thread implementation will force.
	 * 
	 * @return New instance of <code>RequestResponseEndPoint</code>.
	 * @throws BusException Any problem with creating endpoint will rise the exception.
	 */
	RequestResponseEndPoint createRequestResponseEndPoint(int maxThreads)
			throws BusException;	

	/**
	 * Creates fire&forget type endpoint (based on single thread).
	 * 
	 * @return New instance of <code>FireAndForgetEndPoint</code>.
	 * @throws BusException Any problem with creating endpoint will rise the exception.
	 */
	FireAndForgetEndPoint createFireAndForgetEndPoint()
			throws BusException;

	/**
	 * Creates fire&forget type endpoint (multi-threaded implementation).
	 * 
	 * @param maxThreads Number of threads, if 0 or 1 single thread implementation will force.
	 * 
	 * @return New instance of <code>FireAndForgetEndPoint</code>.
	 * @throws BusException Any problem with creating endpoint will rise the exception.
	 */
	FireAndForgetEndPoint createNotificationEndPoint(int maxThreads)
			throws BusException;

	/**
	 * Creates multicast type endpoint (based on single thread).
	 * 
	 * @return New instance of <code>MulticastEndPoint</code>.
	 * @throws BusException Any problem with creating endpoint will rise the exception.
	 */
	MulticastEndPoint createMulticastEndPoint()
			throws BusException;

	/**
	 * Creates multicast type endpoint (multi-threaded implementation).
	 * 
	 * @param maxThreads Number of threads, if 0 or 1 single thread implementation will force.
	 * 
	 * @return New instance of <code>MulticastEndPoint</code>.
	 * @throws BusException Any problem with creating endpoint will rise the exception.
	 */
	MulticastEndPoint createMulticastEndPoint(int maxThreads)
			throws BusException;

	/**
	 * Creates a thread safe instance of RequestResponseEndPoint 
	 * 
	 * @return new instance of <code>RequestResponseEndPoint</code>
	 * @throws BusException if there was any problem with creating an endpoint
	 */
	RequestResponseEndPoint createThreadSafeRequestResponseEndPoint()
			throws BusException;
}
