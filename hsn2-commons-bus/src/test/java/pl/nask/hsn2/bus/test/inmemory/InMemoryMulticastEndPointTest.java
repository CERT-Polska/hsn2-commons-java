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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
import pl.nask.hsn2.bus.api.endpoint.MulticastEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledMulticastEndPoint;
import pl.nask.hsn2.bus.inmemory.InMemoryBroker;
import pl.nask.hsn2.bus.inmemory.endpoint.InMemoryConsumeEndPoint;
import pl.nask.hsn2.bus.inmemory.endpoint.InMemoryFireAndForgetEndPoint;
import pl.nask.hsn2.bus.inmemory.endpoint.InMemoryMulticastEndPoint;

public class InMemoryMulticastEndPointTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryMulticastEndPointTest.class);

	private FireAndForgetEndPoint responseEp = new InMemoryFireAndForgetEndPoint();
	private ConsumeEndPointHandler handler = new ConsumeEndPointHandler() {
		private final AtomicInteger counter = new AtomicInteger(0);
		@Override
		public void handleMessage(final Message message) {
			LOGGER.info("Got message: type={}", message.getType());
			Message response = new Message(
					"SomeResponse " + counter.incrementAndGet(),
					null,
					message.getReplyTo());
			response.setReplyTo(null);
			try {
				responseEp.sendNotify(response);
			} catch (BusException e) {
				throw new IllegalStateException("Error sending response!");
			}
		}
	};

	@BeforeMethod
	public void setUp() {
		InMemoryBroker.restart();
		InMemoryBroker.createQueue("S1");
		InMemoryBroker.createQueue("S2");
		InMemoryBroker.createQueue("S3");
	}

	@Test
	public void singleMessageTest() throws BusException, TimeoutException {

		MulticastEndPoint ep = null;
		List<Message> resp = null;
		ep = new InMemoryMulticastEndPoint();
		new InMemoryConsumeEndPoint("S1", handler);
		new InMemoryConsumeEndPoint("S2", handler);
		new InMemoryConsumeEndPoint("S3", handler);
		
		Message message = new Message("SomeType", "".getBytes(), new Destination(""));
		resp = ep.sendMulticast(message, new String[]{"S1", "S2", "S3"});
		Assert.assertNotNull(resp);
		LOGGER.info("RESP-NUM: " + resp.size());
		Assert.assertNotNull(resp);
		Assert.assertEquals(resp.size(), 3);
		int i = 0;
		for (Message m : resp) {
			LOGGER.info("Message-{}: {}", ++i, m.getType());
		}
	}

	@Test(invocationCount=10, threadPoolSize=10)
	public void multithreadedTest() throws BusException, InterruptedException {

		ConsumeEndPoint c1 = null, c2 = null, c3 = null;
		try {
			long start = System.currentTimeMillis();
			
			MulticastEndPoint ep = new PooledMulticastEndPoint(
					50,
					new PooledMulticastEndPoint.PooledMulticastEPCreateCallback(){
						
						@Override
						public MulticastEndPoint create() {
							return new InMemoryMulticastEndPoint();
						}
					}
			);
			c1 = new InMemoryConsumeEndPoint("S1", handler);
			c2 = new InMemoryConsumeEndPoint("S2", handler);
			c3 = new InMemoryConsumeEndPoint("S3", handler);

			for (int i=0;i<100;i++) {
				Message message = new Message("SomeType-" + i, "".getBytes(), new Destination(""));
				LOGGER.info("Seding message: {}", i);
				List<Message> response = ep.sendMulticast(message, new String[]{"S1", "S2", "S3"});
				LOGGER.info("RESP-NUM: " + response.size());
				Assert.assertNotNull(response);
				Assert.assertEquals(response.size(), 3);
				int j = 0;
				for (Message m : response) {
					LOGGER.info("Message-{}: {}", ++j, m.getType());
				}
			}
			LOGGER.info("Time: " + (System.currentTimeMillis() - start)/1000);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (c1 !=null)
				c1.close();
			if (c2 != null)
				c2.close();
			if (c3 != null)
				c3.close();
		}
	}

}
