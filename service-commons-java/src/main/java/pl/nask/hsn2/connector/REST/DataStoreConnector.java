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

import java.io.IOException;
import java.io.InputStream;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.protobuff.DataStore.DataResponse;

import com.google.protobuf.GeneratedMessage;

public interface DataStoreConnector {

    public DataResponse sendGet(long jobId, long dataId) throws IOException;

    public DataResponse sendPost(byte[] data, long jobId) throws IOException;

    public DataResponse sendPost(InputStream dataInputStream, long jobId) throws IOException;

    public InputStream getResourceAsStream(long jobId, long referenceId) throws ResourceException, StorageException;

    public GeneratedMessage getResourceAsMsg(long jobId, long referenceId, String msgType) throws ResourceException, StorageException;

    public boolean ping();
}