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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.TimeoutException;
import pl.nask.hsn2.bus.api.endpoint.MulticastEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledMulticastEndPoint;
import pl.nask.hsn2.bus.rabbitmq.RbtUtils;
import pl.nask.hsn2.bus.rabbitmq.endpoint.RbtMulticastEndPoint;

import com.rabbitmq.client.Connection;

public class RbtMulticastEndPointTest extends RbtAbstractEndPointTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RbtMulticastEndPointTest.class);
	private final boolean testEnabler = false;

	@Test(enabled = testEnabler)
	public void singleMessageTest() throws IOException, BusException, TimeoutException {

		final Connection connection = getConnection(10);
		
		MulticastEndPoint ep = new RbtMulticastEndPoint(connection);
		Message message = new Message("SomeType", "".getBytes(), new Destination(""));
		List<Message> resp = ep.sendMulticast(message, new String[]{"S1", "S2", "S3"});
		LOGGER.info("RESP-NUM: " + resp.size());
		int i = 0;
		for (Message m : resp) {
			LOGGER.info("Message-{}: {}", ++i, m.getType());
		}
	}

	@Test(invocationCount=10, threadPoolSize=10, enabled = testEnabler)
	public void multithreadedTest() throws IOException, BusException, InterruptedException {

		final Connection connection = getConnection(10);

		try {
			long start = System.currentTimeMillis();
			
			MulticastEndPoint ep = new PooledMulticastEndPoint(
					50,
					new PooledMulticastEndPoint.PooledMulticastEPCreateCallback(){
						
						@Override
						public MulticastEndPoint create() {
							try {
								return new RbtMulticastEndPoint(connection);
							} catch (BusException e) {
								throw new RuntimeException(e);
							}
						}
					}
			);
			for (int i=0;i<100;i++) {
				Message message = new Message("SomeType-" + i, "".getBytes(), new Destination(""));
				LOGGER.info("Seding message: {}", i);
				List<Message> response = ep.sendMulticast(message, new String[]{"S1", "S2", "S3"});
				LOGGER.info("RESP-NUM: " + response.size());
				int j = 0;
				for (Message m : response) {
					LOGGER.info("Message-{}: {}", ++j, m.getType());
				}
			}
			ep.close();
			LOGGER.info("Time: " + (System.currentTimeMillis() - start)/1000);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			RbtUtils.closeConnection(connection);
		}
	}

}
