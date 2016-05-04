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

package pl.nask.hsn2.bus.test.rabbitmq;

import java.io.IOException;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.TimeoutException;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledFireAndForgetEndPoint;
import pl.nask.hsn2.bus.rabbitmq.RbtUtils;
import pl.nask.hsn2.bus.rabbitmq.endpoint.RbtFireAndForgetEndPoint;

import com.rabbitmq.client.Connection;

/**
 * This test suite has disabled all test because
 * is using real RabbitMQ service. Could be useful
 * for real functional testing and performance tests. 
 * 
 *
 */
public class RbtNotificationEndPointTest extends RbtAbstractEndPointTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RbtNotificationEndPointTest.class);
	
	private static final String TEST_QUEUE_NAME = "serviceQueue";
	private static final String TEST_SERVICES_EXCHANGE_NAME = "serviceExchange";
	
	private final boolean testEnabler = false;
	
	@Test(enabled = testEnabler, expectedExceptions=BusException.class)
	public void singleMessageNoQueueTest() throws IOException, BusException, TimeoutException {

		Connection connection = getConnection(10);
		
		try {
			RbtUtils.deleteQueue(connection, TEST_QUEUE_NAME);
			FireAndForgetEndPoint ep = new RbtFireAndForgetEndPoint(connection);
			Message req = new Message("SomeType", "".getBytes(), new Destination(TEST_QUEUE_NAME));
			ep.sendNotify(req);
			LOGGER.info("Message sent.");
		} finally {
			LOGGER.info("Closing Connection....");
			RbtUtils.closeConnection(connection);
		}
	}

	@Test(enabled = testEnabler, dependsOnMethods="singleMessageNoQueueTest")
	public void singleMessageTest() throws IOException, BusException, TimeoutException {

		Connection connection = getConnection(10);
		
		try {
			RbtUtils.createQueue(connection, TEST_SERVICES_EXCHANGE_NAME, TEST_QUEUE_NAME);
			RbtUtils.purgeQueue(connection, TEST_QUEUE_NAME);
			FireAndForgetEndPoint ep = new RbtFireAndForgetEndPoint(connection);
			Message req = new Message("SomeType", "".getBytes(), new Destination(TEST_QUEUE_NAME));
			ep.sendNotify(req);
			LOGGER.info("Message sent.");
			
			Assert.assertEquals(1, RbtUtils.getMessageCount(connection, TEST_QUEUE_NAME));
			
		} finally {
			RbtUtils.deleteQueue(connection, TEST_QUEUE_NAME);
			LOGGER.info("Closing Connection....");
			RbtUtils.closeConnection(connection);
		}
	}

	@Test(enabled = testEnabler, dependsOnMethods="singleMessageTest")
	public void singleThreadTest() throws IOException, BusException, TimeoutException {
		
		Connection connection = getConnection(10);		

		try {
			RbtUtils.createQueue(connection, TEST_SERVICES_EXCHANGE_NAME, TEST_QUEUE_NAME);
			RbtUtils.purgeQueue(connection, TEST_QUEUE_NAME);
			FireAndForgetEndPoint ep = new RbtFireAndForgetEndPoint(connection);
			long start = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				Message req = new Message("SomeType " + i, "".getBytes(), new Destination(TEST_QUEUE_NAME));
				ep.sendNotify(req);
				LOGGER.info("Message sent.");
			}
			LOGGER.info("Time: {}", (System.currentTimeMillis() - start)/1000);
			Assert.assertEquals(10, RbtUtils.getMessageCount(connection, TEST_QUEUE_NAME));
			
		} finally {
			RbtUtils.deleteQueue(connection, TEST_QUEUE_NAME);
			LOGGER.info("Closing Connection....");
			RbtUtils.closeConnection(connection);
		}
	}

	@Test(enabled = testEnabler, dependsOnMethods="singleThreadTest")
	public void spreadSingleThreadTest() throws IOException, BusException, TimeoutException {
		Connection connection = getConnection(10);
		try {
			RbtUtils.createQueue(connection, TEST_SERVICES_EXCHANGE_NAME, "S1");
			RbtUtils.purgeQueue(connection, "S1");
			RbtUtils.createQueue(connection, TEST_SERVICES_EXCHANGE_NAME, "S2");
			RbtUtils.purgeQueue(connection, "S2");
			RbtUtils.createQueue(connection, TEST_SERVICES_EXCHANGE_NAME, "S3");
			RbtUtils.purgeQueue(connection, "S3");
			FireAndForgetEndPoint ep = new RbtFireAndForgetEndPoint(connection);
			long start = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				Message req = new Message("SomeType " + i, "".getBytes(), new Destination(TEST_QUEUE_NAME));
				ep.spread(req, new String[]{"S1", "S2", "S3"});
				LOGGER.info("Messages sent.");
			}
			LOGGER.info("Time: {}", (System.currentTimeMillis() - start)/1000);
			Assert.assertEquals(10, RbtUtils.getMessageCount(connection, "S1"));
			Assert.assertEquals(10, RbtUtils.getMessageCount(connection, "S2"));
			Assert.assertEquals(10, RbtUtils.getMessageCount(connection, "S3"));
			
		} finally {
			RbtUtils.deleteQueue(connection, "S1");
			RbtUtils.deleteQueue(connection, "S2");
			RbtUtils.deleteQueue(connection, "S3");
			LOGGER.info("Closing Connection....");
			RbtUtils.closeConnection(connection);
		}
	}
	
	@Test(invocationCount=10, threadPoolSize=10, enabled = testEnabler, dependsOnMethods="spreadSingleThreadTest")
	public void multithreadedNotifyTest() throws IOException, BusException, InterruptedException {

		final Connection connection = getConnection(10);
		
		RbtUtils.createQueue(connection, TEST_SERVICES_EXCHANGE_NAME, TEST_QUEUE_NAME);
		RbtUtils.purgeQueue(connection, TEST_QUEUE_NAME);

		try {
			long start = System.currentTimeMillis();
			
			FireAndForgetEndPoint ep = new PooledFireAndForgetEndPoint(
					new PooledFireAndForgetEndPoint.PooledNEPCreateCallback(){
						
						@Override
						public FireAndForgetEndPoint create() {
							try {
								return new RbtFireAndForgetEndPoint(connection);
							} catch (BusException e) {
								throw new RuntimeException(e);
							}
						}
					});
			for (int i=0;i<1000;i++) {
				Message message = new Message("SomeType " + i, "".getBytes(), new Destination(TEST_QUEUE_NAME));
				ep.sendNotify(message);
			}
			ep.close();
			LOGGER.info("Time: {}", (System.currentTimeMillis() - start)/1000);
			LOGGER.info("Messages: {}", RbtUtils.getMessageCount(connection, TEST_QUEUE_NAME));
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			LOGGER.info("Closing Connection....");
			RbtUtils.closeConnection(connection);
		}
	}

	@Test(enabled = testEnabler, dependsOnMethods="multithreadedNotifyTest")
	public void multithreadedNotifyVerifyTest() throws BusException {
		final Connection connection = getConnection(1);
		try {
			Assert.assertEquals(10000, RbtUtils.getMessageCount(connection, TEST_QUEUE_NAME));
		} finally {
			RbtUtils.closeConnection(connection);
		}
	}
	
	
	@Test(invocationCount=5, threadPoolSize=5, enabled = testEnabler, dependsOnMethods="multithreadedNotifyVerifyTest")
	public void multithreadedSpreedTest() throws IOException, BusException, InterruptedException {

		final Connection connection = getConnection(10);

		RbtUtils.createQueue(connection, TEST_SERVICES_EXCHANGE_NAME, "S1");
		RbtUtils.purgeQueue(connection, "S1");
		RbtUtils.createQueue(connection, TEST_SERVICES_EXCHANGE_NAME, "S2");
		RbtUtils.purgeQueue(connection, "S2");
		RbtUtils.createQueue(connection, TEST_SERVICES_EXCHANGE_NAME, "S3");
		RbtUtils.purgeQueue(connection, "S3");

		try {

			long start = System.currentTimeMillis();
			
			FireAndForgetEndPoint ep = new PooledFireAndForgetEndPoint(
					50,
					new PooledFireAndForgetEndPoint.PooledNEPCreateCallback(){
						
						@Override
						public FireAndForgetEndPoint create() {
							try {
								return new RbtFireAndForgetEndPoint(connection);
							} catch (BusException e) {
								throw new RuntimeException(e);
							}
						}
					});
			for (int i=0;i<500;i++) {
				Message message = new Message("SomeType " + i, "".getBytes(), new Destination(TEST_QUEUE_NAME));
				ep.spread(message, new String[]{"S1", "S2", "S3"});
			}
			ep.close();
			LOGGER.info("Time: {}", (System.currentTimeMillis() - start)/1000);
			LOGGER.info("Messages: {}", RbtUtils.getMessageCount(connection, "S1"));
			LOGGER.info("Messages: {}", RbtUtils.getMessageCount(connection, "S2"));
			LOGGER.info("Messages: {}", RbtUtils.getMessageCount(connection, "S3"));
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			LOGGER.info("Closing Connection....");
			RbtUtils.closeConnection(connection);
		}
	}

	@Test(enabled = testEnabler, dependsOnMethods="multithreadedSpreedTest")
	public void multithreadedSpreedVerifyTest() throws BusException {
		final Connection connection = getConnection(1);
		try {
			Assert.assertEquals(2500, RbtUtils.getMessageCount(connection, "S1"));
			Assert.assertEquals(2500, RbtUtils.getMessageCount(connection, "S2"));
			Assert.assertEquals(2500, RbtUtils.getMessageCount(connection, "S3"));
		} finally {
			RbtUtils.closeConnection(connection);
		}
	}
}
