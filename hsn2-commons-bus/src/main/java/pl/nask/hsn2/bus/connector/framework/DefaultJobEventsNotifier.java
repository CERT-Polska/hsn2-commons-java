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

package pl.nask.hsn2.bus.connector.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.EndPointFactory;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.connector.AbstractSerializableConnector;
import pl.nask.hsn2.bus.operations.JobFinished;
import pl.nask.hsn2.bus.operations.JobFinishedReminder;
import pl.nask.hsn2.bus.operations.JobStarted;
import pl.nask.hsn2.bus.operations.JobStatus;
import pl.nask.hsn2.bus.operations.Operation;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class DefaultJobEventsNotifier extends AbstractSerializableConnector implements JobEventsNotifier {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJobEventsNotifier.class);	
	private FireAndForgetEndPoint notifyEndPoint = null;
	private Destination destination = null;

	public DefaultJobEventsNotifier(MessageSerializer<Operation> serializer,
			EndPointFactory factory, Destination destination) throws BusException {
		super(serializer);
		this.notifyEndPoint = factory.createNotificationEndPoint(10);
		this.destination = destination;
	}

	@Override
	public void jobStarted(long jobId) {
		JobStarted jobStarted = new JobStarted(jobId);
		sendOut(jobStarted);
	}

	@Override
	public void jobFinished(long jobId, JobStatus status) {
		JobFinished jobFinished = new JobFinished(jobId, status);
		sendOut(jobFinished);
	}

	private void sendOut(Operation operation) {
		try {
			Message message = getSerializer().serialize(operation);
			message.setDestination(destination);
			message.setCorrelationId(null);
			message.setReplyTo(null);
			notifyEndPoint.sendNotify(message);
		} catch (MessageSerializerException e) {
			LOGGER.error("Serialization error.", e);
		} catch (BusException e) {
			LOGGER.error("Cant send out notification.", e);
		}
	}

	@Override
	public void jobFinishedReminder(long jobId, JobStatus status, int offendingTask) {
		JobFinishedReminder reminder = new JobFinishedReminder(jobId);
		reminder.setOffendingTask(offendingTask);
		if (status != null) {
			reminder.setStatus(status);
		}
		sendOut(reminder);
	}
}
