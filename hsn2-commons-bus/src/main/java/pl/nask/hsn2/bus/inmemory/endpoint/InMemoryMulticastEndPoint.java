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

package pl.nask.hsn2.bus.inmemory.endpoint;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.MulticastEndPoint;
import pl.nask.hsn2.bus.inmemory.InMemoryBroker;

public class InMemoryMulticastEndPoint implements MulticastEndPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryMulticastEndPoint.class);
	
	private static final int DEFAULT_WAIT = 300;
	
	private final String responseQueue = "tmp-" + java.util.UUID.randomUUID().toString();
	private int timeout;
	private boolean closed = true;

	public InMemoryMulticastEndPoint() {
		this(DEFAULT_WAIT);
	}
	
	public InMemoryMulticastEndPoint(int timeout) {
		this.timeout = timeout;
		open();
	}
	
	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void open() {
		if (isClosed()) {
			InMemoryBroker.createQueue(responseQueue);
		}
		closed = false;
	}

	@Override
	public void close() throws BusException {
		InMemoryBroker.deleteQueue(responseQueue);
		closed = true;
	}

	@Override
	public List<Message> sendMulticast(Message message, String[] servicesNames)
			throws BusException {
		
		message.setReplyTo(new Destination(responseQueue));

		for (String destination : servicesNames) {
			InMemoryBroker.publish(destination, message);
		}
		
		long currTime = System.currentTimeMillis();
		List<Message> responseList = new LinkedList<Message>();
		int i = 0;
		while (System.currentTimeMillis() < currTime + ((long)timeout) * 1000
				&& i < servicesNames.length) {
			Message response = InMemoryBroker.get(responseQueue, 1);
			if (response != null) {
				responseList.add(response);
				i++;
			}
		}
		if (i < servicesNames.length) {
			LOGGER.debug(
					"There are no responses from all services (expected={}, got={}).",
					servicesNames.length, i);
		}
		return responseList;
	}

	@Override
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public int getTimeout() {
		return this.timeout;
	}

}
