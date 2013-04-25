package pl.nask.hsn2.connector.AMQP;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import mockit.NonStrictExpectations;
import pl.nask.hsn2.bus.rabbitmq.RbtDestination;
import pl.nask.hsn2.protobuff.Object.Attribute;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.Object.Attribute.Type;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse.ResponseType;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

@SuppressWarnings({ "unused", "rawtypes" })
public final class RbtMockUtil {
	public static final String QUEUE_NAME = "queueName-abc123";
	public static final String CLOSE_STRING = "close";
	public static final String CONTENT_TYPE = "application/hsn2+protobuf";
	public static final String TYPE = "test-7dshdt345";
	public static final String SERVICE = "srvc-nxh5w";
	public static final String EXCHANGE = "exch-kj42b9";
	public static final byte[] MESSAGE = { 1, 2, 3, 4 };
	public static final RbtDestination DESTINATION = new RbtDestination(EXCHANGE, SERVICE);
	public static final long DELIVERY_TAG = 1L;
	public static final ObjectResponse OBJECT_RESPONSE =ObjectResponse.newBuilder().setType(ResponseType.SUCCESS_GET).setError(EXCHANGE).build();
	public static String result = "";
	public static Set<ActionType> actions = new HashSet<>();
	public static String correlationId = "";

	private RbtMockUtil() {
		// Utility class.
	}

	public static enum ActionType {
		NEW_CONNECTION_THROW_IO_EXCEPTION, PUBLISH_THROW_IO_EXCEPTION, ACK_THROW_IO_EXCEPTION, THROW_INTERRUPT_EXCEPTION, WRONG_CONTENT_TYPE, CHANNEL_CLOSE_THROW_IO_EXCEPTION, CONNECTION_CLOSE_THROW_IO_EXCEPTION, CONNECTION_IS_CLOSED, WRONG_CORRELATION_ID, RECEIVE_OBJECT_RESPONSE;
	}

	public static void rbtMock(final ConnectionFactory connectionFactory, final Connection connection, final Channel channel,
			final QueueingConsumer consumer) throws IOException, InterruptedException {
		new NonStrictExpectations() {
			{
				connectionFactory.newConnection();
				result = connection;
				forEachInvocation = new Object() {
					void validate() throws IOException {
						if (actions.contains(ActionType.NEW_CONNECTION_THROW_IO_EXCEPTION)) {
							throw new IOException();
						}
					}
				};

				connection.createChannel();
				result = channel;

				connection.isOpen();
				result = new mockit.Delegate() {
					boolean isOpen() {
						if (actions.contains(ActionType.CONNECTION_IS_CLOSED)) {
							return false;
						} else {
							return true;
						}
					}
				};

				connection.close(anyInt);
				forEachInvocation = new Object() {
					void validate(int timeout) throws IOException {
						if (actions.contains(ActionType.CONNECTION_CLOSE_THROW_IO_EXCEPTION)) {
							throw new IOException();
						} else {
							addResults(CLOSE_STRING);
						}
					}
				};

				channel.basicPublish(anyString, anyString, null, withInstanceOf(byte[].class));
				forEachInvocation = new Object() {
					void validate(String exchange, String routingKey, BasicProperties props, byte[] body) throws IOException {
						if (actions.contains(ActionType.PUBLISH_THROW_IO_EXCEPTION)) {
							throw new IOException();
						} else {
							addResults(exchange, routingKey, props.getType(), Arrays.toString(body));
							correlationId = props.getCorrelationId();
						}
					}
				};

				channel.basicAck(anyLong, anyBoolean);
				forEachInvocation = new Object() {
					void validate(long deliveryTag, boolean multiple) throws IOException {
						if (actions.contains(ActionType.ACK_THROW_IO_EXCEPTION)) {
							throw new IOException();
						} else {
							addResults(Long.toString(deliveryTag), Boolean.toString(multiple));
						}
					}
				};

				channel.close();
				forEachInvocation = new Object() {
					void validate() throws IOException {
						if (actions.contains(ActionType.CHANNEL_CLOSE_THROW_IO_EXCEPTION)) {
							throw new IOException();
						} else {
							addResults(EXCHANGE, SERVICE, EXCHANGE, SERVICE);
						}
					}
				};

				consumer.nextDelivery();
				result = new mockit.Delegate() {
					Delivery nextDelivery() {
						Envelope envelope = new Envelope(DELIVERY_TAG, false, EXCHANGE, SERVICE);
						String contentType;

						byte[] tempMsg;
						if (actions.contains(ActionType.RECEIVE_OBJECT_RESPONSE)) {
							tempMsg = OBJECT_RESPONSE.toByteArray();
						} else {
							tempMsg = MESSAGE;
						}

						if (actions.contains(ActionType.WRONG_CONTENT_TYPE)) {
							contentType = CONTENT_TYPE + CONTENT_TYPE;
						} else {
							contentType = CONTENT_TYPE;
						}
						String tempCorrelationId;
						if (actions.remove(ActionType.WRONG_CORRELATION_ID)) {
							tempCorrelationId = "";
						} else {
							tempCorrelationId = correlationId;
						}
						BasicProperties properties = new BasicProperties.Builder().contentType(contentType)
								.correlationId(tempCorrelationId).replyTo(SERVICE).build();
						return new Delivery(envelope, properties, tempMsg);
					}
				};
				forEachInvocation = new Object() {
					void validate() throws InterruptedException {
						if (actions.contains(ActionType.THROW_INTERRUPT_EXCEPTION)) {
							throw new InterruptedException();
						}
					}
				};
			}
		};
	}

	public static void mockDeclareOK(final DeclareOk declareOk, final Channel channel) throws IOException {
		new NonStrictExpectations() {
			{
				channel.queueDeclare();
				result = declareOk;

				declareOk.getQueue();
				result = QUEUE_NAME;
			}
		};
	}

	public static void addResults(String... results) {
		for (String r : results) {
			result += r;
		}
	}
}
