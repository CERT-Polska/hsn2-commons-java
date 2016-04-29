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

package pl.nask.hsn2.bus.test.connector;

import mockit.Mock;
import mockit.MockUp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPoint;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPointHandler;
import pl.nask.hsn2.bus.api.endpoint.EndPointFactory;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.MulticastEndPoint;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;
import pl.nask.hsn2.bus.connector.framework.DefaultJobEventsNotifier;
import pl.nask.hsn2.bus.operations.JobFinished;
import pl.nask.hsn2.bus.operations.JobStarted;
import pl.nask.hsn2.bus.operations.JobStatus;
import pl.nask.hsn2.bus.operations.Operation;
import pl.nask.hsn2.bus.serializer.MessageSerializer;

public class DefaultJobEventsNotifierTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJobEventsNotifierTest.class);
	
	public static class DefaultSerializer implements MessageSerializer<Operation> {
		@Override	public Message serialize(Operation operation) { return null; }
		@Override	public Operation deserialize(Message message) { return null; }
	}
	
	public static class DefaultFireAndForgetEndPoint implements FireAndForgetEndPoint {
		@Override	public boolean isClosed() {	return false; }
		@Override	public void open() { }
		@Override	public void close() { }
		@Override	public void sendNotify(Message message) { }
		@Override	public void spread(Message message, String[] servicesNames) { }
	}
	
	public static class DefaultEndPointFactory implements EndPointFactory {
		@Override public ConsumeEndPoint createConsumeEndPoint(ConsumeEndPointHandler messagesHandler, String routingKey) { return null; }
		@Override public ConsumeEndPoint createConsumeEndPoint(ConsumeEndPointHandler messagesHandler, String routingKey, boolean autoack) throws BusException { return null; }
		@Override public RequestResponseEndPoint createRequestResponseEndPoint() { return null; }
		@Override public RequestResponseEndPoint createRequestResponseEndPoint(int maxThreads) { return null; }
		@Override public FireAndForgetEndPoint createFireAndForgetEndPoint() { return null; }
		@Override public FireAndForgetEndPoint createNotificationEndPoint(int maxThreads) { return null; }
		@Override public MulticastEndPoint createMulticastEndPoint() throws BusException { return null; }
		@Override public MulticastEndPoint createMulticastEndPoint(int maxThreads) { return null; }
		@Override public RequestResponseEndPoint createThreadSafeRequestResponseEndPoint() {return null;}
	}
	
	@BeforeTest
	public void setUpMocks() {
		new MockUp<DefaultEndPointFactory>() {
			@Mock
			public FireAndForgetEndPoint createNotificationEndPoint(int maxThreads) {
				return new DefaultFireAndForgetEndPoint();
			}
		};
	}
	
	@Test
	public void jobStartedTest() throws BusException {
		
		new MockUp<DefaultSerializer>() {
			@Mock
			public Message serialize(Operation operation) {
				LOGGER.info("Operation: {}", operation);
				Assert.assertTrue(operation instanceof JobStarted);
				JobStarted js = (JobStarted) operation;
				Assert.assertEquals(js.getJobId(), 3L);
				return new Message("some-type", null, new Destination("dest"));
			}
		};
		
		new MockUp<DefaultFireAndForgetEndPoint>() {
			@Mock
			public void sendNotify(final Message message) {
				LOGGER.info("Message: {}", message);
				Assert.assertEquals(message.getDestination().getService(), "destination-1");
				Assert.assertNull(message.getCorrelationId());
				Assert.assertEquals(message.getReplyTo().getService(), "");
			}
		};

		DefaultJobEventsNotifier notifier = new DefaultJobEventsNotifier(
				new DefaultSerializer(), new DefaultEndPointFactory(),
				new Destination("destination-1"));
		
		notifier.jobStarted(3L);
	}

	@Test
	public void jobFinishedTest() throws BusException {
		
		new MockUp<DefaultSerializer>() {
			@Mock
			public Message serialize(Operation operation) {
				LOGGER.info("Operation: {}", operation);
				Assert.assertTrue(operation instanceof JobFinished);
				JobFinished jf = (JobFinished) operation;
				Assert.assertEquals(jf.getJobId(), 4L);
				Assert.assertEquals(jf.getStatus(), JobStatus.PROCESSING);
				return new Message("some-type", null, new Destination("dest"));
			}
		};
		
		new MockUp<DefaultFireAndForgetEndPoint>() {
			@Mock
			public void sendNotify(final Message message) {
				LOGGER.info("Message: {}", message);
				Assert.assertEquals(message.getDestination().getService(), "destination-1");
				Assert.assertNull(message.getCorrelationId());
				Assert.assertEquals(message.getReplyTo().getService(), "");
			}
		};

		DefaultJobEventsNotifier notifier = new DefaultJobEventsNotifier(
				new DefaultSerializer(), new DefaultEndPointFactory(),
				new Destination("destination-1"));
		
		notifier.jobFinished(4L, JobStatus.PROCESSING);
	}

}