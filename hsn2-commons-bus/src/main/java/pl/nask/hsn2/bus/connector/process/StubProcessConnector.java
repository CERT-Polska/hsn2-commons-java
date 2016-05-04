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

package pl.nask.hsn2.bus.connector.process;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import pl.nask.hsn2.bus.operations.TaskErrorReasonType;

public class StubProcessConnector implements ProcessConnector {

	@Override
	public void sendTaskRequest(String serviceName, String serviceId,
			long jobId, int taskId, long objectDataId)
			throws ProcessConnectorException {
	}

	@Override
	public void sendTaskRequest(String serviceName, String serviceId,
			long jobId, int taskId, long objectDataId, Properties params)
			throws ProcessConnectorException {
	}

	@Override
	public void sendTaskAccepted(long jobId, int taskId)
			throws ProcessConnectorException {
	}

	@Override
	public void sendTaskError(long jobId, int taskId, TaskErrorReasonType reason)
			throws ProcessConnectorException {
	}

	@Override
	public void sendTaskError(long jobId, int taskId,
			TaskErrorReasonType reason, String description)
			throws ProcessConnectorException {
	}

	@Override
	public void sendTaskCompleted(long jobId, int taskId)
			throws ProcessConnectorException {
	}

	@Override
	public void sendTaskCompleted(long jobId, int taskId, Set<Long> objects)
			throws ProcessConnectorException {
	}

	@Override
	public void sendTaskCompleted(long jobId, int taskId, Set<Long> objects,
			List<String> warnings) throws ProcessConnectorException {
	}

	@Override
	public void releaseResources() {
	
		throw new IllegalStateException("Method unimplemented");
		
	}
}
