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

package pl.nask.hsn2.bus.connector.objectstore;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;
import pl.nask.hsn2.bus.connector.AbstractSerializableConnector;
import pl.nask.hsn2.bus.operations.JobStatus;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.ObjectRequest;
import pl.nask.hsn2.bus.operations.ObjectResponse;
import pl.nask.hsn2.bus.operations.Operation;
import pl.nask.hsn2.bus.operations.builder.AttributeBuilder;
import pl.nask.hsn2.bus.operations.builder.JobFinishedBuilder;
import pl.nask.hsn2.bus.operations.builder.ObjectRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

/**
 * This is technology independent <code>ObjectStoreConnector</code>.
 *
 *
 */
public class DefaultObjectStoreConnector extends AbstractSerializableConnector implements ObjectStoreConnector {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultObjectStoreConnector.class);

	private RequestResponseEndPoint requestResponseEndPoint = null;
	private FireAndForgetEndPoint notificationEndPoint = null;

	private Destination destination;
	
	public DefaultObjectStoreConnector(MessageSerializer<Operation> serializer,
			Destination destination,
			RequestResponseEndPoint requestResponseEndPoint, FireAndForgetEndPoint notificationEndPoint) throws BusException {

		super(serializer);
		
		this.destination = destination;
		this.requestResponseEndPoint = requestResponseEndPoint;
		this.notificationEndPoint = notificationEndPoint;
	}
	
	@Override
	public final Long sendObjectStoreData(long jobId, ObjectData object) throws ObjectStoreConnectorException {

		ObjectRequestBuilder builder = new ObjectRequestBuilder(
				ObjectRequest.RequestType.PUT, jobId).addData(object);

		LOGGER.debug("Sending PUT operation to OS: jobId={}, object={}", jobId, object);

		ObjectResponse response = sendToObjectStoreAndReceive(builder.build());

		if (response.getType() != ObjectResponse.ResponseType.SUCCESS_PUT) {
			LOGGER.debug("unexpected response={}", response);
			throw new ObjectStoreConnectorException("Unsuccessfull PUT, got:" + response.getType());
		}

		if (response.getObjects().size() == 0) {
			throw new ObjectStoreConnectorException("Expected to have an object created, but it's not");
		}
		return (Long) response.getObjects().toArray()[0];
	}

	@Override
	public Set<Long> sendObjectStoreData(long jobId, Integer taskId, Collection<? extends ObjectData> dataList)	throws ObjectStoreConnectorException {

		ObjectRequestBuilder builder = new ObjectRequestBuilder(
				ObjectRequest.RequestType.PUT, jobId).addAllData(dataList);

		if (taskId != null) {
			builder.setTaskId(taskId);
		}
		
		ObjectResponse response = sendToObjectStoreAndReceive(builder.build());

		if (response.getType() != ObjectResponse.ResponseType.SUCCESS_PUT) {
			LOGGER.debug("unexpected response={}", response);
			throw new ObjectStoreConnectorException("Unsuccessfull PUT, got:" + response.getType());
		}

		if (response.getObjects().size() == 0) {
			throw new ObjectStoreConnectorException("Expected to have an object created, but it's not");
		}
		return response.getObjects();
	}
	

	@Override
	public void sendJobFinished(long jobId, JobStatus status) throws ObjectStoreConnectorException {
		JobFinishedBuilder builder = new JobFinishedBuilder(jobId, status);
		sendToObjectStore(builder.build());
		LOGGER.info("JobFinished (id={}, status={}) was sent to ObjectStore.", jobId, status);
	}

	
	@Override
	public final void updateObjectStoreData(long jobId,	Collection<? extends ObjectData> dataList) throws ObjectStoreConnectorException {

			ObjectRequest request = new ObjectRequestBuilder(ObjectRequest.RequestType.UPDATE, jobId)
				.addAllData(dataList).build();
		
			ObjectResponse response = sendToObjectStoreAndReceive(request);

			if (response.getType() == ObjectResponse.ResponseType.PARTIAL_UPDATE) {
				throw new PartialUpdateException(
						"Conflicts during update data in the ObjectStore.", response.getConflicts());
			}
			
			if (response.getType() != ObjectResponse.ResponseType.SUCCESS_UPDATE) {
				throw new ObjectStoreConnectorException(
						"Failed to update data in the ObjectStore", response
								.getType().toString(), response.getError());
			}
	}

	@Override
	public final ObjectData getObjectStoreData(long jobId, long objectId) throws ObjectStoreConnectorException {

		ObjectRequest request = new ObjectRequestBuilder(
				ObjectRequest.RequestType.GET, jobId).addObject(objectId)
				.build();

		ObjectResponse response = sendToObjectStoreAndReceive(request);

		return response.getData(0);
	}

	@Override
	public final Set<Long> findByAttributeName(long jobId, String attributeName) throws ObjectStoreConnectorException {

		ObjectRequest request = new ObjectRequestBuilder(ObjectRequest.RequestType.QUERY, jobId)
			.addByAttributeNameQuery(attributeName)
			.build();

		ObjectResponse response = sendToObjectStoreAndReceive(request);
		return response.getObjects();
	}

	@Override
	public final Set<Long> findByAttributeValue(long jobId, String attributeName, boolean value) throws ObjectStoreConnectorException {

		ObjectRequest request = new ObjectRequestBuilder(ObjectRequest.RequestType.QUERY, jobId)
			.addByAttributeValueQuery(new AttributeBuilder(attributeName).setBool(value).build())
			.build();

		ObjectResponse response = sendToObjectStoreAndReceive(request);
		return response.getObjects();
	}

	@Override
	public final Set<Long> findByAttributeValue(long jobId, String attributeName, int value) throws ObjectStoreConnectorException {

		ObjectRequest request = new ObjectRequestBuilder(ObjectRequest.RequestType.QUERY, jobId)
			.addByAttributeValueQuery(new AttributeBuilder(attributeName).setInteger(value).build())
			.build();

		ObjectResponse response = sendToObjectStoreAndReceive(request);
		return response.getObjects();
	}

	@Override
	public final Set<Long> findByAttributeValue(long jobId, String attributeName, long value) throws ObjectStoreConnectorException {

		ObjectRequest request = new ObjectRequestBuilder(ObjectRequest.RequestType.QUERY, jobId)
			.addByAttributeValueQuery(new AttributeBuilder(attributeName).setTime(value).build())
			.build();

		ObjectResponse response = sendToObjectStoreAndReceive(request);
		return response.getObjects();
	}

	@Override
	public final Set<Long> findByAttributeValue(long jobId, String attributeName, String value) throws ObjectStoreConnectorException {

		ObjectRequest request = new ObjectRequestBuilder(ObjectRequest.RequestType.QUERY, jobId)
			.addByAttributeValueQuery(new AttributeBuilder(attributeName).setString(value).build())
			.build();

		ObjectResponse response = sendToObjectStoreAndReceive(request);
		return response.getObjects();
	}

	@Override
	public final Set<Long> findByObjectId(long jobId, String attributeName, long value)	throws ObjectStoreConnectorException {

		ObjectRequest request = new ObjectRequestBuilder(ObjectRequest.RequestType.QUERY, jobId)
			.addByAttributeValueQuery(new AttributeBuilder(attributeName).setObjectRef(value).build())
			.build();

		ObjectResponse response = sendToObjectStoreAndReceive(request);
		return response.getObjects();
	}

	/**
	 * This method sends message to Object Store
	 * and return response or throw BusException
	 * if message wasn't sent
	 * 
	 * @param objectRequest Message to be sent to ObjectStore.
	 * @return ObjectResponse received.
	 * @throws ObjectStoreConnectorException Any communication problem will raise it.
	 */
	private ObjectResponse sendToObjectStoreAndReceive(ObjectRequest objectRequest) throws ObjectStoreConnectorException {
		try {
			Message request = getSerializer().serialize(objectRequest);
			request.setDestination(destination);
			String correlationId = java.util.UUID.randomUUID().toString();
			request.setCorrelationId(correlationId);
			
			LOGGER.debug("Sending request to OS: {}", objectRequest.getRequestType());
			LOGGER.trace("Sending message to OS: {}", request);
			
			Message response = requestResponseEndPoint.sendAndGet(request);

			LOGGER.debug("Got response from OS for {}", objectRequest.getRequestType());
			LOGGER.trace("Response message from OS: {}", response);
			
			if (!correlationId.equals(response.getCorrelationId())) {
				LOGGER.warn("Correlarion id doesn't match. We are relax but it should be fixed! expected: {} got: {}",
						correlationId, response.getCorrelationId());
			}

			Operation respOp = getSerializer().deserialize(response);

			if (respOp instanceof ObjectResponse) {

				return (ObjectResponse) respOp;
				
			}
			throw new ObjectStoreConnectorException("Unexpected response type from the ObjectStore: " + response.getType());
		} catch (BusException e) {
			throw new ObjectStoreConnectorException("Communication error while sending or waiting for the response from ObjectStore", e);
	    } catch (MessageSerializerException e) {
			throw new ObjectStoreConnectorException("Communication error. Invalid request taken or response receive from objectStore.", e);
		}
	}
	
	
	/**
	 * This method sends generic message to Object Store
	 * without wait for response
	 * 
	 * @param operation Message to be sent to Object Store.
	 * @throws ObjectStoreConnectorException Any communication problem will raise it.
	 */
	private void sendToObjectStore(Operation operation) throws ObjectStoreConnectorException{
		try {
			Message request = getSerializer().serialize(operation);
			request.setDestination(destination);
			String correlationId = java.util.UUID.randomUUID().toString();
			request.setCorrelationId(correlationId);
			
			LOGGER.debug("Sending notification to OS: {}", request);

			notificationEndPoint.sendNotify(request);
		} catch (BusException e) {
			throw new ObjectStoreConnectorException("Communication error while sending message to ObjectStore!", e);
	    } catch (MessageSerializerException e) {
			throw new ObjectStoreConnectorException("Communication error. Invalid request taken.", e);
		}
	}

}
