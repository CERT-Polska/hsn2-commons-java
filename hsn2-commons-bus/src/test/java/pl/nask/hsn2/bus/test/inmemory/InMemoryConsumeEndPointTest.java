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

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.TimeoutException;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPoint;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPointHandler;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledFireAndForgetEndPoint;
import pl.nask.hsn2.bus.inmemory.InMemoryBroker;
import pl.nask.hsn2.bus.inmemory.endpoint.InMemoryConsumeEndPoint;
import pl.nask.hsn2.bus.inmemory.endpoint.InMemoryFireAndForgetEndPoint;

public class InMemoryConsumeEndPointTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryConsumeEndPointTest.class);
	
	private String destination = "serviceQueue";
	
	@Test
	public void consumeTest() throws BusException, TimeoutException, InterruptedException {

		int numberOfMessages = 1000;
		
		// removing previous messages
		InMemoryBroker.purge(this.destination);
		
		final AtomicInteger num = new AtomicInteger(0);
		ConsumeEndPointHandler handler = new ConsumeEndPointHandler() {

			@Override
			public void handleMessage(Message message) {
				LOGGER.info("Got message: type={}", message.getType());
				num.incrementAndGet();
			}
		};

		// produce 1000 messages
		FireAndForgetEndPoint nEp = new PooledFireAndForgetEndPoint(
				new PooledFireAndForgetEndPoint.PooledNEPCreateCallback() {
					
					@Override
					public FireAndForgetEndPoint create() {
						return new InMemoryFireAndForgetEndPoint();
					}
				});
		for (int i=0;i<numberOfMessages;i++) {
			Message message = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
			nEp.sendNotify(message);
			LOGGER.info("Producing message: type={}", message.getType());
		}
		LOGGER.info("Test messages produced.");

		ConsumeEndPoint endPoint = new InMemoryConsumeEndPoint("serviceQueue", handler);
		
		long startTime = System.currentTimeMillis();
		while(num.get() < numberOfMessages && (System.currentTimeMillis() - startTime < 1500000)) {
			Thread.sleep(10);
		}
		LOGGER.info("Number of processed: {}, in queue: {}", num.get(), InMemoryBroker.count("serviceQueue"));
		endPoint.close();
		LOGGER.info("Processed {} messags", num.get());
		Assert.assertEquals(num.get(), numberOfMessages);

	}

	
}
