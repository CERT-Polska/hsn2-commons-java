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

package pl.nask.hsn2.bus.test.rabbitmq;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.TimeoutException;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledRequestResponseEndPoint;
import pl.nask.hsn2.bus.rabbitmq.RbtUtils;
import pl.nask.hsn2.bus.rabbitmq.endpoint.RbtRequestResponseEndPoint;

import com.rabbitmq.client.Connection;

/**
 * This test suite has disabled all test because
 * is using real RabbitMQ service. Could be useful
 * for real functional testing and perfirmance tests. 
 * 
 *
 */
public class RbtRREndPointTest extends RbtAbstractEndPointTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RbtRREndPointTest.class);
	private final boolean testEnabler = false;

	@Test(enabled = testEnabler)
	public void singleMessageTest() throws IOException, BusException, TimeoutException {

		Connection connection = getConnection(10);
		
		try {
			RequestResponseEndPoint ep = new RbtRequestResponseEndPoint(connection);
			Message req = new Message("SomeType", "".getBytes(), new Destination("serviceQueue"));
			Message resp = ep.sendAndGet(req);
			LOGGER.info("RESP: " + resp.getType());
		} finally {
			LOGGER.info("Closing Connection....");
			RbtUtils.closeConnection(connection);
		}
	}
	
	@Test(enabled = testEnabler)
	public void singleThreadTest() throws IOException, BusException, TimeoutException {
		
		Connection connection = getConnection(10);		

		try {
			RequestResponseEndPoint ep = new RbtRequestResponseEndPoint(connection);
			long start = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				Message req = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
				Message resp = ep.sendAndGet(req);
				LOGGER.info("RESP: " + resp.getType());
			}
			LOGGER.info("Time: " + (System.currentTimeMillis() - start)/1000);
			
		} finally {
			LOGGER.info("Closing Connection....");
			RbtUtils.closeConnection(connection);
		}
	}

	@Test(invocationCount=10, threadPoolSize=10, enabled = testEnabler)
	public void multithreadedTest() throws IOException, BusException, InterruptedException {

		final Connection connection = getConnection(10);

		try {
			long start = System.currentTimeMillis();
			
			RequestResponseEndPoint ep = new PooledRequestResponseEndPoint(
					new PooledRequestResponseEndPoint.PooledRREPCreateCallback(){
						@Override
						public RequestResponseEndPoint create() {
							try {
								return new RbtRequestResponseEndPoint(connection);
							} catch (BusException e) {
								throw new RuntimeException(e);
							}
						}
					}
					);
			for (int i=0;i<100;i++) {
				Message message = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
				LOGGER.info("Seding message: {}", i);
				Message response = ep.sendAndGet(message);
				LOGGER.info("Message: {}", response.getType());
			}
			ep.close();
			LOGGER.info("Time: " + (System.currentTimeMillis() - start)/1000);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			LOGGER.info("Closing Connection....");
			RbtUtils.closeConnection(connection);
		}
	}
}
