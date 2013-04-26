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

package pl.nask.hsn2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.rabbitmq.RbtDestination;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;
import pl.nask.hsn2.bus.serializer.protobuf.ProtoBufObjectDataSerializer;
import pl.nask.hsn2.bus.serializer.protobuf.ProtoBufObjectResponseSerializer;
import pl.nask.hsn2.connector.BusException;
import pl.nask.hsn2.connector.ObjectStoreConnector;
import pl.nask.hsn2.connector.AMQP.InConnector;
import pl.nask.hsn2.connector.AMQP.ObjectStoreConnectorImpl;
import pl.nask.hsn2.connector.REST.DataResponse;
import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.connector.REST.DataStoreConnectorImpl;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;
import pl.nask.hsn2.protobuff.Process.TaskAccepted;
import pl.nask.hsn2.protobuff.Process.TaskCompleted;
import pl.nask.hsn2.protobuff.Process.TaskCompleted.Builder;
import pl.nask.hsn2.protobuff.Process.TaskError;
import pl.nask.hsn2.protobuff.Process.TaskError.ReasonType;
import pl.nask.hsn2.protobuff.Process.TaskRequest;

import com.google.protobuf.InvalidProtocolBufferException;

public class ServiceConnectorImpl implements ServiceConnector {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConnectorImpl.class);

	private final InConnector frameworkConnector;
    private final ObjectStoreConnector objectStoreConnector;
    private final DataStoreConnector dataStoreConnector;

    public ServiceConnectorImpl(String connectorAddress, String serviceQueueName, String commonExchangeName, String objectStoreQueueName, String dataStoreAddress) {
    	try {
    		LOGGER.debug("SERVICE QUEUE = {}", serviceQueueName);
			this.frameworkConnector = new InConnector(connectorAddress, new RbtDestination(commonExchangeName, serviceQueueName));
			this.objectStoreConnector = new ObjectStoreConnectorImpl(connectorAddress, objectStoreQueueName);
			this.dataStoreConnector = new DataStoreConnectorImpl(dataStoreAddress);
			
			if (dataStoreAddress != null && !dataStoreConnector.ping()){
				LOGGER.error("Error in connection with DataStore.");
				frameworkConnector.close();
				throw new RuntimeException("Error in connection with DataStore.");
			}
    	} catch (BusException e) {
    		LOGGER.error(e.getMessage(),e);
			throw new RuntimeException("Error in connection with Bus.", e);
    	}
    }
    			
    /* (non-Javadoc)
     * @see pl.nask.hsn2.ServiceConnector#getTaskRequest()
     */
    @Override
    public TaskRequest getTaskRequest() throws BusException {
        try {
        	byte[] bytes = frameworkConnector.receive();
        	return TaskRequest.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
        	LOGGER.error(e.getMessage(), e);
            throw new IllegalStateException("Unexpected message received", e);
        }
    }

    /* (non-Javadoc)
     * @see pl.nask.hsn2.ServiceConnector#sendTaskAccepted(long, int)
     */
    @Override
    public void sendTaskAccepted(long jobId, int requestId) throws BusException {
        TaskAccepted taskAcceptedMsg = TaskAccepted.newBuilder()
        .setJob(jobId)
        .setTaskId(requestId).build();

       frameworkConnector.send(taskAcceptedMsg.toByteArray(),"TaskAccepted");
    }

    /* (non-Javadoc)
     * @see pl.nask.hsn2.ServiceConnector#sendTaskComplete(long, int)
     */
    @Override
    public void sendTaskComplete(long jobId, int requestId, List<Long> newObjects) throws BusException {
    	sendTaskCompletedWithWarnings(jobId, requestId, newObjects, null);
    }
    
    @Override
    public void sendTaskCompletedWithWarnings(long jobId, int requestId,List<Long> newObjects, List<String> warnings) {
    	Builder builder = TaskCompleted.newBuilder()
    			.setJob(jobId)
    			.setTaskId(requestId);
    	if (newObjects != null)
    		builder.addAllObjects(newObjects);
    	if ( warnings != null)
    		builder.addAllWarnings(warnings);
    	
    	TaskCompleted taskCompletedMsg = builder.build();
    	frameworkConnector.sendWithAck(taskCompletedMsg.toByteArray(), "TaskCompleted");
    			
    }

    @Override
    public void sendTaskError(long jobId, int reqId, Exception e){
        sendTaskErrorMessage(jobId, reqId, ReasonType.DEFUNCT, e);
    }

    @Override
    public void sendTaskError(long jobId, int reqId, ParameterException e){
        sendTaskErrorMessage(jobId, reqId, ReasonType.PARAMS, e);
    }

    @Override
    public void sendTaskError(long jobId, int reqId, ResourceException e){
        sendTaskErrorMessage(jobId, reqId, ReasonType.RESOURCE, e);
    }

    @Override
    public void sendTaskError(long jobId, int reqId, InputDataException e){
    	sendTaskErrorMessage(jobId, reqId, ReasonType.INPUT, e);
    }

    private void sendTaskErrorMessage(long jobId, int reqId, ReasonType params, Exception e){
        String description = e.getMessage() != null ? e.getMessage() : e.toString();
        TaskError taskErrorMsg = TaskError.newBuilder()
		        .setJob(jobId)
		        .setTaskId(reqId)
		        .setReason(params)
		        .setDescription(GenericServiceInfo.getServiceName() + ": " + description).build();

        frameworkConnector.sendWithAck(taskErrorMsg.toByteArray(),"TaskError");
    }

	@Override
	public DataResponse sendDataStoreData(long jobId, byte[] data) throws IOException {
		return dataStoreConnector.sendPost(data, jobId);
	}

	@Override
	public DataResponse sendDataStoreData(long jobId, InputStream is) throws IOException{
	    return dataStoreConnector.sendPost(is, jobId);
	}

	@Override
	public InputStream getDataStoreData(long jobId, long dataId) throws IOException {
		return dataStoreConnector.sendGet(jobId, dataId);
	}

	@Override
	public InputStream getDataStoreDataAsStream(long jobId, long referenceId) throws ResourceException, StorageException {
	    return dataStoreConnector.getResourceAsStream(jobId, referenceId);
	}

	@Override
	public String toString() {
	    return new StringBuilder("ServiceConnector(fw=")
	    			.append(frameworkConnector)
	    			.append(", ds=")
	    			.append(dataStoreConnector)
	    			.append(", os=")
	    			.append(objectStoreConnector)
	    			.append(")")
	    			.toString();
	}

	@Override
	public ObjectResponse sendObjectStoreData(long jobId, int requestId, Iterable<? extends ObjectData> dataList) throws StorageException {
		return objectStoreConnector.sendObjectStoreData(jobId, requestId, dataList);
	}
	
	@Override
	public pl.nask.hsn2.bus.operations.ObjectResponse saveObjects(long jobId, List<pl.nask.hsn2.bus.operations.ObjectData> dataList)
			throws StorageException {
		try {
			ObjectResponse res = objectStoreConnector.sendObjectStoreData(jobId, 0, ProtoBufObjectDataSerializer.serializeObjectData(dataList));
			return ProtoBufObjectResponseSerializer.deserializeObjectResponse(res);
		} catch (MessageSerializerException e) {
			throw new StorageException("Unable to prepare a request for saving an object in the ObjectStore", e);
		}
	}

	@Override
	public ObjectResponse updateObjectStoreData(long jobId,	Iterable<? extends ObjectData> dataList) throws StorageException {
		return objectStoreConnector.updateObjectStoreData(jobId, dataList);
	}
	
	@Override
	public pl.nask.hsn2.bus.operations.ObjectResponse updateObject(long jobId, pl.nask.hsn2.bus.operations.ObjectData objectData)
			throws StorageException { 
		try {
			ObjectResponse res = objectStoreConnector.updateObjectStoreData(jobId, Collections.singletonList(ProtoBufObjectDataSerializer.serializeObjectData(objectData)));
			return ProtoBufObjectResponseSerializer.deserializeObjectResponse(res);
		} catch (MessageSerializerException e) {
			throw new StorageException("Unable to prepare a request for object update", e);
		}
	}

	@Override
	public ObjectResponse getObjectStoreData(long jobId, List<Long> objectsId) throws StorageException {
		return objectStoreConnector.getObjectStoreData(jobId, objectsId);
	}

	@Override
	public void ignoreLastTaskRequest() {
		frameworkConnector.ackLastMessage();
	}

	@Override
	public void close() {
		try {
			frameworkConnector.close();
			objectStoreConnector.close();
		} catch (BusException e) {
			LOGGER.warn("Exception when closing",e);
		}
		
	}

}
