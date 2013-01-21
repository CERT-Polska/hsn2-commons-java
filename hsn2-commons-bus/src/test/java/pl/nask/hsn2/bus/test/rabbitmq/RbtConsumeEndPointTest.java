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
import java.util.Random;
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
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledFireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledRequestResponseEndPoint;
import pl.nask.hsn2.bus.rabbitmq.RbtUtils;
import pl.nask.hsn2.bus.rabbitmq.endpoint.RbtConsumeEndPoint;
import pl.nask.hsn2.bus.rabbitmq.endpoint.RbtFireAndForgetEndPoint;
import pl.nask.hsn2.bus.rabbitmq.endpoint.RbtRequestResponseEndPoint;

import com.rabbitmq.client.Connection;

public class RbtConsumeEndPointTest extends RbtAbstractEndPointTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RbtConsumeEndPointTest.class);
	
	private final boolean testEnabler = false;
	
	private String destination = "serviceQueue";
	
	@Test(enabled = testEnabler)
	public void consumeTest() throws IOException, BusException, InterruptedException, TimeoutException {

		int numberOfMessages = 1000;
		
		final Connection connection = getConnection(20);

		// removing previous messages
		RbtUtils.purgeQueue(connection.createChannel(), this.destination);
		
		final AtomicInteger num = new AtomicInteger(0);
		ConsumeEndPointHandler handler = new ConsumeEndPointHandler() {

			final Random randomGenerator = new Random();
			@Override
			public void handleMessage(Message message) {
				LOGGER.info("Got message: type={}", message.getType());
				num.incrementAndGet();
				
				int wait = randomGenerator.nextInt(10);
				LOGGER.info("Random wait is {} secs.", wait);
				try {
					Thread.sleep(1000 * wait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		// produce 1000 messages
		FireAndForgetEndPoint nEp = new PooledFireAndForgetEndPoint(
				new PooledFireAndForgetEndPoint.PooledNEPCreateCallback() {
					
					@Override
					public FireAndForgetEndPoint create() {
						try {
							return new RbtFireAndForgetEndPoint(connection);
						} catch (BusException e) {
							throw new RuntimeException(e);
						}
					}
				});
		for (int i=0;i<numberOfMessages;i++) {
			Message message = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
			nEp.sendNotify(message);
			LOGGER.info("Producing message: type={}", message.getType());
		}
		LOGGER.info("Test messages produced.");

		ConsumeEndPoint endPoint = new RbtConsumeEndPoint(connection,
				handler,
				"serviceQueue", false, 10);

		
		long startTime = System.currentTimeMillis();
		while(num.get() < 1000 && (System.currentTimeMillis() - startTime < 1500000)) {
			Thread.sleep(10);
		}
		endPoint.close();
		LOGGER.info("Processed {} messags", num.get());
		Assert.assertEquals(num.get(), numberOfMessages);
	}

	@Test(enabled = testEnabler)
	public void consumeWithResponseTest() throws IOException, BusException, InterruptedException, TimeoutException {

		final Connection connection = getConnection(1);

		final AtomicInteger num = new AtomicInteger(0);

		final FireAndForgetEndPoint responseEndpoint = new RbtFireAndForgetEndPoint(connection);
		
		ConsumeEndPointHandler handler = new ConsumeEndPointHandler() {

			@Override
			public void handleMessage(Message message) {
				LOGGER.info("Got message: type={}", message.getType());
				message.setDestination(message.getReplyTo());
				message.setReplyTo(new Destination(""));
				try {
					responseEndpoint.sendNotify(message);
				} catch (BusException e) {
					e.printStackTrace();
				}
				num.incrementAndGet();
			}
		};

		ConsumeEndPoint endPoint = new RbtConsumeEndPoint(connection,
				handler,
				"serviceQueue", false, 10);

		// produce 1000 messages
		RequestResponseEndPoint rrEp = new PooledRequestResponseEndPoint(
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
		for (int i=0;i<1000;i++) {
			Message message = new Message("SomeType " + i, "".getBytes(), new Destination("serviceQueue"));
			LOGGER.info("Seding message: {}", i);
			Message response = rrEp.sendAndGet(message);
			LOGGER.info("Received response: {}", response.getType());
		}
		LOGGER.info("Test messages produced.");

		long startTime = System.currentTimeMillis();
		while(num.get() < 1000 && (System.currentTimeMillis() - startTime < 15000)) {
			Thread.sleep(10);
		}
		endPoint.close();
		rrEp.close();
		LOGGER.info("Processed {} messags", num.get());
	}
}
