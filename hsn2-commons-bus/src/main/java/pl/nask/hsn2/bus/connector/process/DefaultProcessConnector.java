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

package pl.nask.hsn2.bus.connector.process;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.TimeoutException;
import pl.nask.hsn2.bus.api.endpoint.EndPointFactory;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.connector.AbstractSerializableConnector;
import pl.nask.hsn2.bus.operations.Operation;
import pl.nask.hsn2.bus.operations.TaskAccepted;
import pl.nask.hsn2.bus.operations.TaskErrorReasonType;
import pl.nask.hsn2.bus.operations.builder.TaskCompletedBuilder;
import pl.nask.hsn2.bus.operations.builder.TaskErrorBuilder;
import pl.nask.hsn2.bus.operations.builder.TaskRequestBuilder;
import pl.nask.hsn2.bus.rabbitmq.RbtDestination;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

/**
 * Technology independent <code>ProcessConnector</code> implementation.
 * 
 *
 */
public class DefaultProcessConnector extends AbstractSerializableConnector implements ProcessConnector {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProcessConnector.class);
	private FireAndForgetEndPoint notificationEndPoint = null;
	private Map<String, RbtDestination> servicesDestinationsLowPriority = new HashMap<String, RbtDestination>();
	private Destination frameworkDestination;

	public DefaultProcessConnector(
			MessageSerializer<Operation> serializer,
			EndPointFactory endpointFactory,
			Destination frameworkDestination) throws BusException {
		this(serializer, endpointFactory, frameworkDestination, null);
	}

	public DefaultProcessConnector(
			MessageSerializer<Operation> serializer,
			EndPointFactory endpointFactory,
			Destination frameworkDestination,
			Map<String, RbtDestination> servicesDestinationsLowPriority) throws BusException {
		super(serializer);
		this.servicesDestinationsLowPriority = servicesDestinationsLowPriority;
		this.frameworkDestination = frameworkDestination;
		this.notificationEndPoint = endpointFactory.createNotificationEndPoint(10);
	}

	@Override
	public final void sendTaskRequest(String serviceName, String serviceId,
			long jobId, int taskId, long objectDataId, Properties params)
			throws ProcessConnectorException {

		checkIfServiceDefined(serviceName);

		String printableServiceId = serviceId == null ? "" : "/" + serviceId;
		LOGGER.debug(
				"Will publish a new task for service {}{}. JobId={}, taskId={}, objectId={}, params={}",
				new Object[] {
						serviceName,
						printableServiceId,
						jobId,
						taskId,
						params == null ? null : params
				}
		);

		TaskRequestBuilder builder = new TaskRequestBuilder(jobId, taskId, objectDataId);
		if (params != null) {
			Enumeration<?> keys = params.propertyNames();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				builder.addParameter(key, params.getProperty(key));
			}
		}
		
		sendOperation(builder.build(), serviceName, frameworkDestination);
	}
	
	@Override
	public final void sendTaskRequest(String serviceName, String serviceId,
			long jobId, int taskId, long objectDataId)
			throws ProcessConnectorException {
		this.sendTaskRequest(serviceName, serviceId, jobId, taskId, objectDataId, null);
	}

	@Override
	public final void sendTaskAccepted(long jobId, int taskId)
			throws ProcessConnectorException {
		sendOperationToFramework(new TaskAccepted(jobId, taskId));
	}

	@Override
	public final void sendTaskError(long jobId, int taskId, TaskErrorReasonType reason)
			throws ProcessConnectorException {
		this.sendTaskError(jobId, taskId, reason, null);
	}

	@Override
	public final void sendTaskError(long jobId, int taskId,
			TaskErrorReasonType reason, String description)
			throws ProcessConnectorException {
		TaskErrorBuilder builder = new TaskErrorBuilder(jobId, taskId, reason);
		if (description != null && !"".equals(description)) {
			builder.setDescription(description);
		}
		sendOperationToFramework(builder.build());
	}

	@Override
	public final void sendTaskCompleted(long jobId, int taskId)
			throws ProcessConnectorException {
		this.sendTaskCompleted(jobId, taskId, null);
	}

	@Override
	public final void sendTaskCompleted(long jobId, int taskId, Set<Long> objects)
			throws ProcessConnectorException {
		this.sendTaskCompleted(jobId, taskId, objects, null);
	}

	@Override
	public final void sendTaskCompleted(long jobId, int taskId, Set<Long> objects,
			List<String> warnings) throws ProcessConnectorException {
		TaskCompletedBuilder builder = new TaskCompletedBuilder(jobId, taskId);
		if (objects != null && !objects.isEmpty()) {
			builder.setObjects(objects);
		}
		if (warnings != null && !warnings.isEmpty()) {
			builder.setWarnings(warnings);
		}
		sendOperationToFramework(builder.build());
	}

	private void sendOperation(Operation operation, String serviceName, Destination replyTo) throws ProcessConnectorException {
		try {
			Message request = createMessage(operation, serviceName, replyTo);
			sendMessage(request);
		} catch (MessageSerializerException e) {
			throw new ProcessConnectorException(e);
		} catch (BusException e) {
			throw new ProcessConnectorException(e);
		}
	}

    private void sendOperationToFramework(Operation operation) throws ProcessConnectorException {
		try {
			Message request = createMessage(operation, frameworkDestination, null);
			sendMessage(request);
		} catch (MessageSerializerException e) {
			throw new ProcessConnectorException(e);
		} catch (BusException e) {
			throw new ProcessConnectorException(e);
		}
    }

	/**
	 * Sends technical message.
	 * 
	 * @param message Message to be sent.
	 * @param prioritySuffix Priority of the message.
	 * @throws BusException Any communication problem will report the exception.
	 */
	protected final void sendMessage(Message message) throws BusException {
		try {
			LOGGER.debug("Sending message={}", message);
			notificationEndPoint.sendNotify(message);
		} catch (TimeoutException e) {
			LOGGER.error("Cannot send message to the service '{}', timeout!", message.getDestination());
			throw new BusException(e);
		}
	}

	/**
	 * Checks if service name is defined on the list of available services.
	 * @param serviceName Name of the service to be verified.
	 * @throws UnknownServiceException If service is not available the exception will be thrown.
	 */
    private void checkIfServiceDefined(String serviceName) throws UnknownServiceException {
        if (!servicesDestinationsLowPriority.containsKey(serviceName)){
            throw new UnknownServiceException("There is no queue defined in the config for this service " + serviceName);
        }
    }

    /**
	 * Creates message.
	 * 
	 * @param operation
	 * @param serviceName
	 * @param replyTo
	 * @return
	 * @throws MessageSerializerException
	 */
    protected Message createMessage(Operation operation, String serviceName, Destination replyTo) throws MessageSerializerException {
    	Destination destination = servicesDestinationsLowPriority.get(serviceName);
    	return createMessage(operation, destination, replyTo);
	}
    
    /**
     * Creates message.
     * 
     * @param operation
     * @param destination
     * @param replyTo
     * @return
     * @throws MessageSerializerException
     */
    protected Message createMessage(Operation operation, Destination destination, Destination replyTo) throws MessageSerializerException {
    	Message msg = getSerializer().serialize(operation);
    	msg.setDestination(destination);
    	String correlationId = java.util.UUID.randomUUID().toString();
    	msg.setCorrelationId(correlationId);
    	msg.setReplyTo(replyTo);
    	return msg;
    }

	@Override
	public void releaseResources() {
		try {
			notificationEndPoint.close();
		} catch (BusException e) {
			LOGGER.warn("Close failed.",e);
		}
		
	}
}
