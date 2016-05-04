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

package pl.nask.hsn2.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.ServiceConnector;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.connector.REST.DataResponse;

public abstract class DataStoreHelper {
	private static final Logger LOG = LoggerFactory.getLogger(DataStoreHelper.class);
	public static final long DEFAULT_REFERENCE_ID = -1;
	public static final int DEFAULT_STORE_ID = 0;

	private DataStoreHelper() {
		// Utility class. No instantiation allowed.
	}

	/**
	 * Saves the given content in the DataStore.
	 * 
	 * @param connector
	 *            Connection to be used for saving the object.
	 * @param jobId
	 *            Data context (id of the job).
	 * @param contentAsBytes
	 *            Content to be saved.
	 * @return ID of the resource in the DataStore.
	 * @throws StorageException
	 *             In case of error during storage process.
	 */
	public static long saveInDataStore(ServiceConnector connector, long jobId, byte[] contentAsBytes) throws StorageException {
		try {
			LOG.debug("Adding new data to the data store...");
			DataResponse response = connector.sendDataStoreData(jobId, contentAsBytes);
			return processResponse(response);
		} catch (IOException e) {
			String msg = "Error adding bytes to data store.";
			LOG.error(msg, e);
			throw new StorageException(msg, e);
		}
	}

	public static long saveInDataStore(ServiceConnector connector, long jobId, InputStream is) throws StorageException {
		try {
			LOG.debug("Adding new data to the data store...");
			DataResponse response = connector.sendDataStoreData(jobId, is);
			return processResponse(response);
		} catch (IOException e) {
			String msg = "Error adding bytes to data store.";
			LOG.error(msg, e);
			throw new StorageException(msg, e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Process DataStore response.
	 * 
	 * @param response
	 *            DS response.
	 * @return Newly created object's key id.
	 * @throws StorageException
	 *             In case of error during storage process.
	 */
	private static long processResponse(DataResponse response) throws StorageException {
		if (response.isSuccesful()) {
			long referenceId = response.getKeyId();
			LOG.debug("...New data added to data store. Reference id : {}", referenceId);
			return referenceId;
		} else {
			LOG.debug("Failed to add new data to data store.");
			throw new StorageException("Error saving bytes in data store");
		}
	}

	public static InputStream getFileAsInputStream(ServiceConnector connector, long jobId, long referenceId) throws StorageException {
		LOG.debug("Getting file from data store, referenceId: {}", referenceId);

		try {
			return connector.getDataStoreDataAsStream(jobId, referenceId);
		} catch (ResourceException e) {
			throw new StorageException("Error getting resource from data store.", e);
		}
	}
}
