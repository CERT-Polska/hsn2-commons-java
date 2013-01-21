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
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledFireAndForgetEndPoint;
import pl.nask.hsn2.bus.inmemory.InMemoryBroker;
import pl.nask.hsn2.bus.inmemory.endpoint.InMemoryFireAndForgetEndPoint;

public class InMemoryNotificationEndPointTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryNotificationEndPointTest.class);
	
	@BeforeMethod
	public void setUp() {
		InMemoryBroker.restart();
	}
	
	@Test
	public void sinleMessageTest() throws BusException {
		FireAndForgetEndPoint ep = null;
		try {
			ep = new InMemoryFireAndForgetEndPoint();
			Message req = new Message("SomeType", "".getBytes(), new Destination("serviceQueue"));
			ep.sendNotify(req);
			LOGGER.info("Message sent.");
			
			Assert.assertEquals(InMemoryBroker.count("serviceQueue"), 1);
		} finally {
			if (ep != null) {
				ep.close();
			}
		}
	}
	
	@Test
	public void singleThreadTest() throws BusException {
		FireAndForgetEndPoint ep = null;
		try {
			ep = new InMemoryFireAndForgetEndPoint();
			long start = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				Message req = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
				ep.sendNotify(req);
				LOGGER.info("Message sent.");
			}
			LOGGER.info("Time: " + (System.currentTimeMillis() - start)/1000);
			Assert.assertEquals(InMemoryBroker.count("serviceQueue"), 10);
		} finally {
			if (ep != null) {
				ep.close();
			}
		}
	}
	
	@Test
	public void spreadSingleThreadTest() throws IOException, BusException, TimeoutException {
		FireAndForgetEndPoint ep = null;
		try {
			ep = new InMemoryFireAndForgetEndPoint();
			long start = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				Message req = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
				ep.spread(req, new String[]{"S1", "S2", "S3"});
				LOGGER.info("Messages sent.");
			}
			LOGGER.info("Time: " + (System.currentTimeMillis() - start)/1000);
			Assert.assertEquals(InMemoryBroker.count("S1"), 10);
			Assert.assertEquals(InMemoryBroker.count("S2"), 10);
			Assert.assertEquals(InMemoryBroker.count("S3"), 10);
		} finally {
			if (ep != null) {
				ep.close();
			}
		}
	}

	@Test(invocationCount=10, threadPoolSize=10)
	public void multithreadedNotifyTest() throws BusException {

		try {
			long start = System.currentTimeMillis();
			
			FireAndForgetEndPoint ep = new PooledFireAndForgetEndPoint(
					new PooledFireAndForgetEndPoint.PooledNEPCreateCallback(){
						
						@Override
						public FireAndForgetEndPoint create() {
							return new InMemoryFireAndForgetEndPoint();
						}
					});
			for (int i=0;i<1000;i++) {
				Message message = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
				ep.sendNotify(message);
			}
			ep.close();
			LOGGER.info("Time: " + (System.currentTimeMillis() - start)/1000);
			LOGGER.info("NoOfM: {}", InMemoryBroker.count("serviceQueue1"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test(invocationCount=5, threadPoolSize=5)
	public void multithreadedSpreedTest() throws BusException {

		try {
			long start = System.currentTimeMillis();
			
			FireAndForgetEndPoint ep = new PooledFireAndForgetEndPoint(
					50,
					new PooledFireAndForgetEndPoint.PooledNEPCreateCallback(){
						
						@Override
						public FireAndForgetEndPoint create() {
							return new InMemoryFireAndForgetEndPoint();
						}
					});
			for (int i=0;i<500;i++) {
				Message message = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
				ep.spread(message, new String[]{"S1", "S2", "S3"});
			}
			ep.close();
			LOGGER.info("Time: " + (System.currentTimeMillis() - start)/1000);
			LOGGER.info("NoOfM S1: {}", InMemoryBroker.count("S1"));
			LOGGER.info("NoOfM S2: {}", InMemoryBroker.count("S2"));
			LOGGER.info("NoOfM S3: {}", InMemoryBroker.count("S3"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
