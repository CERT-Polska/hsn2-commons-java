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

package pl.nask.hsn2.bus.test.pooled;

import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.pooled.ThreadLocalRequestResponseEndPoint;
import pl.nask.hsn2.bus.inmemory.endpoint.InMemoryEndPointFactory;

public class ThreadLocalRequestResponseEndPointTest {
	
	InMemoryEndPointFactory factory = new InMemoryEndPointFactory();
	ThreadLocalRequestResponseEndPoint ep = new ThreadLocalRequestResponseEndPoint(factory);
	{
		ep.setTimeout(0);
	}
	String serviceName = "testService";
	Destination requestDestination = new Destination(serviceName);
	Message anyRequest= new Message("test", null, requestDestination);
	Message anyResponse = new Message("test", null, anyRequest.getReplyTo());	
	
	@Test(invocationCount=100, threadPoolSize=10)
	public void everyThreadShouldGetAnExclusiveRRQUeue() throws BusException {		
		// if threads were sharing the same instance of the ep, some of them would get face BusException (queue closed)		
		ep.open();
		ep.sendAndGet(anyRequest);
		ep.sendAndGet(anyRequest);
		ep.sendAndGet(anyRequest);
		ep.sendAndGet(anyRequest);
		ep.sendAndGet(anyRequest);
		ep.closeBackingEndPoint();
	}
}
