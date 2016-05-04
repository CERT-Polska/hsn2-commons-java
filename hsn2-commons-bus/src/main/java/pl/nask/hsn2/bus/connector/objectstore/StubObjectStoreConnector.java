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

package pl.nask.hsn2.bus.connector.objectstore;

import java.util.Collection;
import java.util.Set;

import pl.nask.hsn2.bus.operations.JobStatus;
import pl.nask.hsn2.bus.operations.ObjectData;

public class StubObjectStoreConnector implements ObjectStoreConnector {

	@Override
	public Long sendObjectStoreData(long jobId, ObjectData object)
			throws ObjectStoreConnectorException {
		return null;
	}

	@Override
	public Set<Long> sendObjectStoreData(long jobId, Integer taskId,
			Collection<? extends ObjectData> dataList)
			throws ObjectStoreConnectorException {
		return null;
	}

	@Override
	public void updateObjectStoreData(long jobId,
			Collection<? extends ObjectData> dataList)
			throws ObjectStoreConnectorException {
	}

	@Override
	public ObjectData getObjectStoreData(long jobId,
			long objectId)
			throws ObjectStoreConnectorException {
		return null;
	}

	@Override
	public Set<Long> findByAttributeName(long jobId, String attributeName)
			throws ObjectStoreConnectorException {
		return null;
	}

	@Override
	public Set<Long> findByAttributeValue(long jobId, String attributeName,
			String value) throws ObjectStoreConnectorException {
		return null;
	}

	@Override
	public Set<Long> findByAttributeValue(long jobId, String attributeName,
			boolean value) throws ObjectStoreConnectorException {
		return null;
	}

	@Override
	public Set<Long> findByAttributeValue(long jobId, String attributeName,
			int value) throws ObjectStoreConnectorException {
		return null;
	}

	@Override
	public Set<Long> findByAttributeValue(long jobId, String attributeName,
			long value) throws ObjectStoreConnectorException {
		return null;
	}

	@Override
	public Set<Long> findByObjectId(long jobId, String attributeName, long value)
			throws ObjectStoreConnectorException {
		return null;
	}

	@Override
	public void sendJobFinished(long jobId, JobStatus status)
			throws ObjectStoreConnectorException {
	}

	@Override
	public void releaseResources() {
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not implemented!.");
		
	}


}
