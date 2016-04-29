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

package pl.nask.hsn2.bus.api.endpoint.pooled;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.EndPointFactory;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;

/**
 * Very specific type of a Pooled end point: each thread will get it's own RequestResponseEndPoint (bounded to the thread using a ThreadLocal).  
 * 
 */
public class ThreadLocalRequestResponseEndPoint implements RequestResponseEndPoint {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalRequestResponseEndPoint.class);
	private ThreadLocal<RequestResponseEndPoint> threadLocalEndPoint = new ThreadLocal<RequestResponseEndPoint>();
	private int timeout = -1;
	private Deque<RequestResponseEndPoint> endPoints = new ConcurrentLinkedDeque<RequestResponseEndPoint>();
	private final EndPointFactory endPointFactory;		
	
	public ThreadLocalRequestResponseEndPoint(EndPointFactory endPointFactory) {
		this.endPointFactory = endPointFactory;
	}
	
	@Override
	public boolean isClosed() {
		if (threadLocalEndPoint.get() != null) {
			return threadLocalEndPoint.get().isClosed();
		} else {
			return true;
		}
	}

	@Override
	public void open() throws BusException {
		if (threadLocalEndPoint.get() == null) {
			createNewRequestResponseEndPoint();
		}		
		threadLocalEndPoint.get().open();
	}

	private void createNewRequestResponseEndPoint() throws BusException {
		RequestResponseEndPoint ep = endPointFactory.createRequestResponseEndPoint();
		if (timeout != -1) {
			ep.setTimeout(timeout);
		} 
		
		endPoints.add(ep);
		threadLocalEndPoint.set(ep);
	}

	@Override
	public void close() throws BusException {
		// close all!
		for (RequestResponseEndPoint ep: endPoints) {
			ep.close();
		}				
	}
	
	public void closeBackingEndPoint() throws BusException {
		if (threadLocalEndPoint.get() != null) { 
			RequestResponseEndPoint ep = threadLocalEndPoint.get();
			threadLocalEndPoint.remove();
			ep.close();
		}
	}

	@Override
	public Message sendAndGet(Message message) throws BusException {
		if (threadLocalEndPoint.get() == null || threadLocalEndPoint.get().isClosed()) {
			open();
		}
		try {
			return threadLocalEndPoint.get().sendAndGet(message);
		} catch (BusException e) {
			LOGGER.error(e.getMessage(),e);
			// must close the response queue - it's not working properly
			closeBackingEndPoint();
			throw e;
		}
	}

	@Override
	public void setTimeout(int timeout) {
		this.timeout = timeout;
		if (threadLocalEndPoint.get() != null) {
			threadLocalEndPoint.get().setTimeout(timeout);
		}
	}

	@Override
	public int getTimeout() {
		if (threadLocalEndPoint.get() != null) {
			return threadLocalEndPoint.get().getTimeout();
		} else {
			return timeout;
		}
	}
	
}
