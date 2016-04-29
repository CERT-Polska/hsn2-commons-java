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

package pl.nask.hsn2.connector;

import java.util.List;

import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure;
import pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse;

public interface ObjectStoreConnector {

	ObjectResponse sendObjectStoreData(long jobId, int requestId,
			Iterable<? extends ObjectData> dataList)
			throws StorageException;

	ObjectResponse updateObjectStoreData(long jobId,
			Iterable<? extends ObjectData> dataList)
			throws StorageException;

	ObjectResponse getObjectStoreData(long jobId, List<Long> objectsId)
			throws StorageException;

	ObjectResponse query(long jobId, List<QueryStructure> queryStructures)
			throws StorageException;
	void close();

}