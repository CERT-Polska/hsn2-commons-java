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

package pl.nask.hsn2.bus.test.inmemory;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.TimeoutException;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPoint;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPointHandler;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledRequestResponseEndPoint;
import pl.nask.hsn2.bus.inmemory.InMemoryBroker;
import pl.nask.hsn2.bus.inmemory.endpoint.InMemoryConsumeEndPoint;
import pl.nask.hsn2.bus.inmemory.endpoint.InMemoryFireAndForgetEndPoint;
import pl.nask.hsn2.bus.inmemory.endpoint.InMemoryRequestResponseEndPoint;

public class InMemoryRREndPointTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryRREndPointTest.class);

	private FireAndForgetEndPoint responseEp = new InMemoryFireAndForgetEndPoint();
	private ConsumeEndPointHandler handler = new ConsumeEndPointHandler() {
		@Override
		public void handleMessage(Message message) {
			LOGGER.info("Got message: type={}", message.getType());
			message.setDestination(message.getReplyTo());
			message.setReplyTo(null);
			message.setType("SomeResponse");
			try {
				responseEp.sendNotify(message);
			} catch (BusException e) {
				throw new IllegalStateException("Error sending response!");
			}
		}
	};

	@BeforeMethod
	public void setUp() {
		InMemoryBroker.restart();
		InMemoryBroker.createQueue("serviceQueue");
	}
	
	@Test
	public void singleMessageTest() throws IOException, BusException, TimeoutException {

		RequestResponseEndPoint ep = null;
		ConsumeEndPoint consumerEp = null;
		try {
			ep = new InMemoryRequestResponseEndPoint();
			consumerEp = new InMemoryConsumeEndPoint("serviceQueue", handler);
			Message req = new Message("SomeType", "".getBytes(), new Destination("serviceQueue"));
			Message resp = ep.sendAndGet(req);
			Assert.assertNotNull(resp);
			Assert.assertEquals(resp.getType(), "SomeResponse");
		} finally {
			LOGGER.info("Closing Connection....");
			if (consumerEp != null) {
				consumerEp.close();
			}
			if (ep != null) {
				ep.close();
			}
		}
	}

	@Test
	public void singleThreadTest() throws IOException, BusException, TimeoutException {
		
		RequestResponseEndPoint ep = null;
		ConsumeEndPoint consumerEp = null;
		try {
			ep = new InMemoryRequestResponseEndPoint();
			consumerEp = new InMemoryConsumeEndPoint("serviceQueue", handler);
			long start = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				Message req = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
				Message resp = ep.sendAndGet(req);
				Assert.assertNotNull(resp);
				Assert.assertEquals(resp.getType(), "SomeResponse");
			}
			LOGGER.info("Time: " + (System.currentTimeMillis() - start)/1000);
			
		} finally {
			if (consumerEp != null) {
				consumerEp.close();
			}
			if (ep != null) {
				ep.close();
			}
		}
	}

	@Test(invocationCount=10, threadPoolSize=10)
	public void multithreadedTest() throws IOException, BusException, InterruptedException {

		RequestResponseEndPoint ep = null;
		ConsumeEndPoint consumerEp = null;
		try {
			long start = System.currentTimeMillis();
			ep = new PooledRequestResponseEndPoint(
					new PooledRequestResponseEndPoint.PooledRREPCreateCallback(){
						@Override
						public RequestResponseEndPoint create() {
							return new InMemoryRequestResponseEndPoint();
						}
					}
					);
			consumerEp = new InMemoryConsumeEndPoint("serviceQueue", handler);
			for (int i=0;i<100;i++) {
				Message message = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
				LOGGER.info("Seding message: {}", i);
				Message response = ep.sendAndGet(message);
				LOGGER.info("Message: {}", response.getType());
				Assert.assertNotNull(response);
				Assert.assertEquals(response.getType(), "SomeResponse");
			}
			ep.close();
			LOGGER.info("Time: " + (System.currentTimeMillis() - start)/1000);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			LOGGER.info("Closing Connection....");
			if (consumerEp != null) {
				consumerEp.close();
			}
		}
	}

}
