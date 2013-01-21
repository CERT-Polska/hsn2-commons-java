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

package pl.nask.hsn2.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.ServiceConnector;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.protobuff.DataStore;

public abstract class DataStoreHelper {
	private static final Logger LOG = LoggerFactory.getLogger(DataStoreHelper.class);
	
	public static final long DEFAULT_REFERENCE_ID = -1;
	
    public static final int DEFAULT_STORE_ID = 0;

	private DataStoreHelper() {
		// util class
	}
	
	/**
	 * saves the given content in the DataStore 
	 * 
	 * @param connector connection to be used for saving the object
	 * @param jobId data context (id of the job)
	 * @param contentAsBytes content to be saved
	 * @return
	 * 	ID of the resource in the DataStore
	 * @throws StorageException if the save was unsuccessful.
	 */
	public static long saveInDataStore(ServiceConnector connector, long jobId, byte[] contentAsBytes) throws StorageException {
        try {
            LOG.debug("Adding new data to the data store...");
            DataStore.DataResponse response = connector.sendDataStoreData(jobId, contentAsBytes);
            if (DataStore.DataResponse.ResponseType.OK.equals(response.getType())) {
                long referenceId = response.getRef().getKey();
                LOG.debug("...New data added to data store. Reference id : {}", referenceId);
                return referenceId;
            } else {
            	LOG.debug("Failed to add new data to data store, response is {}", response);
            	throw new StorageException("Error saving bytes in data store, response is: " + response);
            }
        } catch (IOException e) {
            String msg = "Error adding bytes to data store.";
            LOG.error(msg, e);
            throw new StorageException(msg, e);
        }
    }

    public static long saveInDataStore(ServiceConnector connector, long jobId, InputStream is) throws StorageException, ResourceException {
        try {
            LOG.debug("Adding new data to the data store...");
            DataStore.DataResponse response = connector.sendDataStoreData(jobId, is);
            if (response.getType().equals(DataStore.DataResponse.ResponseType.OK)) {
                long referenceId = response.getRef().getKey();
                LOG.debug("...New data added to data store. Reference id : {}", referenceId);
                return referenceId;
            } else {
                LOG.debug("Failed to add new data to data store.");
                throw new StorageException("Error saving bytes in data store");
            }
        } catch (IOException e) {
            String msg = "Error adding bytes to data store.";
            LOG.error(msg, e);
            throw new StorageException(msg, e);
        } finally {
        	IOUtils.closeQuietly(is);
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
