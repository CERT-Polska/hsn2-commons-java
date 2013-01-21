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

package pl.nask.hsn2.connector.AMQP;

import java.util.List;

import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.connector.BusException;
import pl.nask.hsn2.connector.ObjectStoreConnector;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.RequestType;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;

import com.google.protobuf.InvalidProtocolBufferException;

public class ObjectStoreConnectorImpl implements ObjectStoreConnector {

	private OutConnector connector;
    public ObjectStoreConnectorImpl(String connectorAddress, String objectStoreQueueName) throws BusException {
		connector = new OutConnector(connectorAddress, objectStoreQueueName);
	}

	/* (non-Javadoc)
	 * @see pl.nask.hsn2.connector.ObjectStoreConnector#sendObjectStoreData(long, int, java.lang.Iterable)
	 */
	@Override
	public ObjectResponse sendObjectStoreData(long jobId, int requestId, Iterable<? extends ObjectData> dataList) throws StorageException {
        ObjectRequest objectRequest = ObjectRequest.newBuilder()
	        .setType(RequestType.PUT)
	        .setJob(jobId)
	        .setTaskId(requestId)
	        .addAllData(dataList)
	        .build();

        return sendToObjectStore(objectRequest);
    }

    /* (non-Javadoc)
	 * @see pl.nask.hsn2.connector.ObjectStoreConnector#updateObjectStoreData(long, java.lang.Iterable)
	 */
    @Override
	public ObjectResponse updateObjectStoreData(long jobId, Iterable<? extends ObjectData> dataList) throws StorageException {
        ObjectRequest objectRequest = ObjectRequest.newBuilder()
            .setType(RequestType.UPDATE)
            .setJob(jobId)
            .addAllData(dataList)
            .build();

        return sendToObjectStore(objectRequest);
    }

    /* (non-Javadoc)
	 * @see pl.nask.hsn2.connector.ObjectStoreConnector#getObjectStoreData(long, java.util.List)
	 */
    @Override
	public ObjectResponse getObjectStoreData(long jobId, List<Long> objectsId) throws StorageException {
        ObjectRequest objectRequest = ObjectRequest.newBuilder()
            .setType(RequestType.GET)
            .setJob(jobId)
            .addAllObjects(objectsId)
            .build();
        return sendToObjectStore(objectRequest);
    }

	@Override
	public ObjectResponse query(long jobId, List<QueryStructure> queryStructures) throws StorageException {
		ObjectRequest objectRequest = ObjectRequest.newBuilder()
	        .setType(RequestType.QUERY)
	        .setJob(jobId)
	        .addAllQuery(queryStructures)
	        .build();
    return sendToObjectStore(objectRequest);
	}

    private ObjectResponse sendToObjectStore(ObjectRequest objectRequest) throws StorageException {
    	try {
            connector.send(objectRequest.toByteArray(), "ObjectRequest");
            return ObjectResponse.parseFrom(connector.receive());
        } catch (InvalidProtocolBufferException e) {
            throw new StorageException("Message malformed", e);
        } catch (BusException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }
}
