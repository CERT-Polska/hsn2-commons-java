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

package pl.nask.hsn2.connector.AMQP;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectStoreConnectorImpl.class);

	private final int retries;

	private OutConnector connector;

	public ObjectStoreConnectorImpl(String connectorAddress, String objectStoreQueueName) throws BusException {
		this(connectorAddress, objectStoreQueueName, 4);
	}

	public ObjectStoreConnectorImpl(String connectorAddress, String objectStoreQueueName, int retries) throws BusException {
		this.connector = new OutConnector(connectorAddress, objectStoreQueueName);
		this.retries = retries;
	}

	/* (non-Javadoc)
	 * @see pl.nask.hsn2.connector.ObjectStoreConnector#sendObjectStoreData(long, int, java.lang.Iterable)
	 */
	@Override
	public final ObjectResponse sendObjectStoreData(long jobId, int requestId, Iterable<? extends ObjectData> dataList) throws StorageException {
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
	public final ObjectResponse updateObjectStoreData(long jobId, Iterable<? extends ObjectData> dataList) throws StorageException {
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
	public final ObjectResponse getObjectStoreData(long jobId, List<Long> objectsId) throws StorageException {
		ObjectRequest objectRequest = ObjectRequest.newBuilder()
			.setType(RequestType.GET)
			.setJob(jobId)
			.addAllObjects(objectsId)
			.build();
		return sendToObjectStore(objectRequest);
	}

	@Override
	public final ObjectResponse query(long jobId, List<QueryStructure> queryStructures) throws StorageException {
		ObjectRequest objectRequest = ObjectRequest.newBuilder()
			.setType(RequestType.QUERY)
			.setJob(jobId)
			.addAllQuery(queryStructures)
			.build();
		return sendToObjectStore(objectRequest);
	}

	private ObjectResponse sendToObjectStore(ObjectRequest objectRequest) throws StorageException {
		int retry = 0;
		while (true) {
			try {
				connector.send(objectRequest.toByteArray(), "ObjectRequest");
				return ObjectResponse.parseFrom(connector.receive());
			} catch (InvalidProtocolBufferException e) {
				throw new StorageException("Message malformed", e);
			} catch (BusException e) {
				if (retry < this.retries) {
					long timeToWait = (long) Math.pow(2, retry);
					LOGGER.info("Problem with connection to ObjectStore. Retrying in {} min.", timeToWait);
					try {
						TimeUnit.MINUTES.sleep(timeToWait);
					} catch (InterruptedException ex) {
						throw new StorageException(ex.getMessage(), ex);
					}
					retry++;
				} else {
					LOGGER.error("Cannot connect to ObjectStore: {}", e.getMessage());
					LOGGER.debug(e.getMessage(), e);
					throw new StorageException(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public final void close() {
		try {
			connector.close();
		} catch (BusException e) {
			LOGGER.error("Cannot close connection: {}", e.getMessage());
			LOGGER.debug(e.getMessage(), e);
		}
	}
}
