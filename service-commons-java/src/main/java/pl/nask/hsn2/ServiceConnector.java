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

package pl.nask.hsn2;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pl.nask.hsn2.connector.BusException;
import pl.nask.hsn2.connector.REST.DataResponse;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;
import pl.nask.hsn2.protobuff.Process.TaskRequest;

public interface ServiceConnector {
    TaskRequest getTaskRequest() throws BusException;

    void sendTaskAccepted(long jobId, int requestId) throws BusException;

    void sendTaskComplete(long jobId, int requestId, List<Long> newObjects) throws BusException;
    
    void sendTaskCompletedWithWarnings(long jobId, int requestId,List<Long> newObjects, List<String> warnings);

    /**
     * @deprecated
     * use saveObjects() instead
     */
    ObjectResponse sendObjectStoreData(long jobId, int requestId, Iterable<? extends ObjectData> dataList) throws StorageException;

    pl.nask.hsn2.bus.operations.ObjectResponse saveObjects(long jobId, List<pl.nask.hsn2.bus.operations.ObjectData> dataList) throws StorageException;

    void sendTaskError(long jobId, int reqId, ParameterException e);

    void sendTaskError(long jobId, int reqId, Exception e);

    void sendTaskError(long jobId, int reqId, ResourceException e);
    
    void sendTaskError(long jobId, int reqId, InputDataException e);

    @Deprecated
    /**
     * @depreceated
     * use updateObject(jobId, ObjectData) instead
     */
    ObjectResponse updateObjectStoreData(long jobId, Iterable<? extends ObjectData> dataList) throws StorageException;
    
    pl.nask.hsn2.bus.operations.ObjectResponse updateObject(long jobId, pl.nask.hsn2.bus.operations.ObjectData objectData) throws StorageException;
    
    ObjectResponse getObjectStoreData(long jobId, List<Long> objectsId) throws StorageException;

    DataResponse sendDataStoreData(long jobId, byte[] data) throws IOException;

    DataResponse sendDataStoreData(long jobId, InputStream is) throws IOException;

    InputStream getDataStoreData(long jobId, long dataId) throws IOException;

    InputStream getDataStoreDataAsStream(long jobId, long referenceId) throws ResourceException, StorageException;

    void ignoreLastTaskRequest();
    
    void close();
}