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

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;
import pl.nask.hsn2.bus.inmemory.InMemoryBroker;

public class InMemoryRequestResponseEndPoint implements RequestResponseEndPoint {

	private static final int DEFAULT_WAIT = 300;
	
	private int timeout;
	private final String responseQueue = "tmp-" + java.util.UUID.randomUUID().toString();
	private boolean closed = true;

	public InMemoryRequestResponseEndPoint() {
		this(DEFAULT_WAIT);
	}
	
	public InMemoryRequestResponseEndPoint(int timeout) {
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
	public void close() {
		InMemoryBroker.deleteQueue(responseQueue);
		closed = true;
	}

	@Override
	public Message sendAndGet(Message message) throws BusException {
		
		Destination destination = message.getDestination();
		message.setReplyTo(new Destination(responseQueue));
		InMemoryBroker.publish(destination.getService(), message);
		
		return InMemoryBroker.get(responseQueue, this.timeout);
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
