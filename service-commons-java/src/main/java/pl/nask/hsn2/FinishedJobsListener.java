package pl.nask.hsn2;

import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.protobuff.Jobs.JobFinished;
import pl.nask.hsn2.protobuff.Jobs.JobFinishedReminder;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class FinishedJobsListener implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(FinishedJobsListener.class);
	private static final boolean AUTO_ACK = true;

	private ConcurrentSkipListSet<Long> knownFinishedJobs = new ConcurrentSkipListSet<>();
	private String rabbitMqHost;
	private String rabbitMqExchange;
	private AtomicBoolean isInitialized = new AtomicBoolean(false);

	public void initialize(String rbtHost, String rbtExchange) {
		if (!isInitialized.get()) {
			if (rbtHost == null || rbtExchange == null) {
				LOG.error("Finished jobs listener initialization error.");
			} else {
				synchronized (this) {
					rabbitMqHost = rbtHost;
					rabbitMqExchange = rbtExchange;
					isInitialized.set(true);
				}
				LOG.debug("Finished jobs listener initialized.");
			}
		}
	}

	@Override
	public void run() {
		if (isInitialized.get()) {
			// Listener initialized.

			LOG.info("Finished jobs listener started. (host={}, exchange={})", rabbitMqHost, rabbitMqExchange);
			try {
				// Prepare consumer.
				QueueingConsumer rabbitMqConsumer = prepareRabbitMqConsumer();

				// Main loop.
				boolean isMainLoopEnabled = true;
				while (isMainLoopEnabled) {
					try {
						QueueingConsumer.Delivery delivery = rabbitMqConsumer.nextDelivery();

						// Check if it's JobFinished or JobFinishedReminder and if so add to list.
						if (delivery.getProperties().getType().equals("JobFinished")) {
							JobFinished jobFinishedData = JobFinished.parseFrom(delivery.getBody());
							long jobId = jobFinishedData.getJob();
							knownFinishedJobs.add(jobId);
							LOG.info("Finished job added to list. (id={})", jobId);
						} else if (delivery.getProperties().getType().equals("JobFinishedReminder")) {
							JobFinishedReminder jobFinishedData = JobFinishedReminder.parseFrom(delivery.getBody());
							long jobId = jobFinishedData.getJob();
							knownFinishedJobs.add(jobId);
							LOG.info("Finished job added to list (reminded). (id={})", jobId);
						}

					} catch (ShutdownSignalException e) {
						LOG.debug("Shutdown signal received.", e);
						isMainLoopEnabled = false;
					} catch (ConsumerCancelledException e) {
						LOG.debug("Consumer cancelled operation.", e);
					} catch (InterruptedException e) {
						// nothing to do
					}
				}
			} catch (IOException e) {
				LOG.error("Error while communicating with message broker.", e);
			}
			LOG.info("Finished jobs listener terminated.");
		} else {
			// Listener not initialized.
			LOG.error("Listener has not been initialized properly and has not been started.");
		}
	}

	private synchronized QueueingConsumer prepareRabbitMqConsumer() throws IOException {
		ConnectionFactory rabbitMqConnectionFactory = new ConnectionFactory();
		rabbitMqConnectionFactory.setHost(rabbitMqHost);
		QueueingConsumer rabbitMqConsumer = null;
		Connection rabbitMqConnection = rabbitMqConnectionFactory.newConnection();
		Channel rabbitMqChannel = rabbitMqConnection.createChannel();
		rabbitMqChannel.exchangeDeclare(rabbitMqExchange, "fanout");

		String rabbitMqQueueName = rabbitMqChannel.queueDeclare().getQueue();
		rabbitMqChannel.queueBind(rabbitMqQueueName, rabbitMqExchange, "");

		rabbitMqConsumer = new QueueingConsumer(rabbitMqChannel);
		rabbitMqChannel.basicConsume(rabbitMqQueueName, AUTO_ACK, rabbitMqConsumer);
		return rabbitMqConsumer;
	}

	public boolean isJobFinished(long jobId) {
		return knownFinishedJobs.contains(jobId);
	}
}
