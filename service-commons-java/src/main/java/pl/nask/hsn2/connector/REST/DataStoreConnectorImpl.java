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

package pl.nask.hsn2.connector.REST;

import static org.apache.commons.httpclient.HttpStatus.SC_CREATED;
import static org.apache.commons.httpclient.HttpStatus.SC_OK;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;

import com.google.protobuf.GeneratedMessage;

public class DataStoreConnectorImpl implements DataStoreConnector {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataStoreConnectorImpl.class);
	private final String address;

	public DataStoreConnectorImpl(String dataStoreAddress) {
		address = dataStoreAddress;
	}

	/* (non-Javadoc)
     * @see pl.nask.hsn2.connector.REST.DataStoreConnector#sendGet(long, long)
     */
	@Override
	public InputStream sendGet(long jobId, long dataId) throws IOException {
		String fullAddress = DSUtils.dsAddress(address, jobId, dataId);
		RestRequestor client = RestRequestor.get(fullAddress);
		LOGGER.debug(client.getResponseMessage());
		if (client.getResponseCode() == SC_OK) {
			return new BufferedInputStream(client.getInputStream());
		} else {
			return null;
		}
	}

	private String msg(InputStream inputStream) throws IOException {
		if (inputStream != null) {
			return IOUtils.toString(inputStream);
		} else {
			return null;
		}

	}

	/* (non-Javadoc)
	 * @see pl.nask.hsn2.connector.REST.DataStoreConnector#sendPost(java.io.InputStream, long)
	 */
	@Override
	public DataResponse sendPost(byte[] data, long jobId) throws IOException {
		return sendPost(new ByteArrayInputStream(data), jobId);
	}

	/* (non-Javadoc)
	 * @see pl.nask.hsn2.connector.REST.DataStoreConnector#sendPost(java.io.InputStream, long)
	 */
	@Override
	public DataResponse sendPost(InputStream dataInputStream, long jobId) throws IOException {
	    String fullAddress = DSUtils.dsAddress(address, jobId);

	    RestRequestor client = RestRequestor.post(fullAddress, dataInputStream);
	    try (InputStream inputStream = client.getInputStream()) {
	        String message = msg(inputStream);
	        LOGGER.debug(message);
	        LOGGER.debug("Response code: {}", client.getResponseCode());
	        if (client.getResponseCode() == SC_CREATED) {
	            long key = Long.parseLong(client.getHeaderField("Content-ID"));
	            DataResponse dr = new DataResponse(key);
	            return dr;
	        } else {
	        	return new DataResponse(message);
	        }
	    }
	}

	/* (non-Javadoc)
     * @see pl.nask.hsn2.connector.REST.DataStoreConnector#getResourceAsStream(long, long)
     */
	@Override
	public InputStream getResourceAsStream(long jobId, long referenceId) throws ResourceException, StorageException {
	    String fullAddress = DSUtils.dsAddress(address, jobId, referenceId);
	    InputStream errorStream = null;
	    
	    try {
	        RestRequestor client = RestRequestor.get(fullAddress);
	        LOGGER.debug(client.getResponseMessage());
	        int respCode = client.getResponseCode();
	        if (respCode == SC_OK){
	            return client.getInputStream();
	        } else {
	        	errorStream = client.getErrorStream();
	        	String errorMessage = msg(errorStream);
	        	
	        	throw new ResourceException(String.format("Expected dataStore to respond with code 200, got %s. Message is: %s, %s",respCode, client.getResponseMessage(), errorMessage));
	        }
	    } catch (IOException e) {
	        throw new StorageException("Storage error (IOException)", e);
	    } finally {
	    	IOUtils.closeQuietly(errorStream);
	    }
	}

	@Override
	public GeneratedMessage getResourceAsMsg(long jobId, long referenceId, String msgType) throws ResourceException, StorageException {

	    String fullAddress = DSUtils.dsAddress(address, jobId, referenceId);

        InputStream inputStream = null;
        try{
            RestRequestor client = RestRequestor.get(fullAddress);
            inputStream = client.getInputStream();
            LOGGER.debug(client.getResponseMessage());
            if (client.getResponseCode() == SC_OK) {
                return buildMessageObject(inputStream, msgType);
            } else {
                throw new StorageException("Error getting object from DataStore: " + msg(inputStream));
            }
        } catch (IOException e) {
            throw new StorageException("Storage error (IOException)", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
	}

	@SuppressWarnings("unchecked")
	private GeneratedMessage buildMessageObject(InputStream is, String msgType) {
	    Class<GeneratedMessage> clazz = null;
        try {
            clazz = (Class<GeneratedMessage>) Class.forName("pl.nask.hsn2.protobuff.Resources$" + msgType);

            Method parseFrom = clazz.getMethod("parseFrom", InputStream.class);
            GeneratedMessage msg = (GeneratedMessage) parseFrom.invoke(null, is);

            return msg;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Incorrect msgType: '" + msgType + "'", e);
        } catch (SecurityException e) {
            throw new IllegalStateException("Couldn't load a class for " + msgType + " due to security exception", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Couldn't run a parseFrom method from " + msgType + " - no such method in class " + clazz, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("InvocationTargetException", e);
        }
	}


	@Override
	public boolean ping(){
		try {
			RestRequestor client = RestRequestor.get(address);
		    int responseCode = client.getResponseCode();
		    String responseMessage = client.getResponseMessage();
		    LOGGER.debug(responseMessage);
			if (responseCode != SC_OK){
				LOGGER.error("DataStore does not work properly. Response code: {} response message: {}", responseCode, responseMessage);
				return false;
		    }
		    return true;
		} catch (IOException e) {
			LOGGER.error("Storage error (IOException)", e);
			return false;
		}
	}
}
