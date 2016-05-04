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

import pl.nask.hsn2.bus.connector.AbstractServicesConnector;
import pl.nask.hsn2.bus.operations.TaskErrorReasonType;

/**
 * This is unified connector for process related operations.
 * 
 *
 */
public interface ProcessConnector extends AbstractServicesConnector{

	/**
	 * Sends <code>TaskRequest</code> operation with no additional parameters.
	 * 
	 * @param serviceName
	 *            Service name to process task.
	 * @param serviceId
	 *            Internal identifier of the service.
	 * @param jobId
	 *            Job identifier the task belongs to.
	 * @param taskId
	 *            Task identifier.
	 * @param objectDataId
	 *            Identifier of ObjectStore object associated with the task.
	 * @throws ProcessConnectorException
	 *             Any communication errors will report the exception.
	 */
	void sendTaskRequest(String serviceName, String serviceId,
			long jobId, int taskId, long objectDataId)
			throws ProcessConnectorException;

	/**
	 * Sends <code>TaskRequest</code> operation with additional parameters.
	 * 
	 * @param serviceName
	 *            Service name to process task.
	 * @param serviceId
	 *            Internal identifier of the service.
	 * @param jobId
	 *            Job identifier the task belongs to.
	 * @param taskId
	 *            Task identifier.
	 * @param objectDataId
	 *            Identifier of ObjectStore object associated with the task.
	 * @param params
	 *            Additional parameters for the task processing.
	 * @throws ProcessConnectorException
	 *             Any communication errors will report the exception.
	 */
	void sendTaskRequest(String serviceName, String serviceId,
			long jobId, int taskId, long objectDataId, Properties params)
			throws ProcessConnectorException;

	/**
	 * Sends <code>TaskAccepted</code> operation to the framework.
	 * 
	 * @param jobId
	 *            Job identifier the task belongs to.
	 * @param taskId
	 *            Accepted task identifier.
	 * @throws ProcessConnectorException
	 *             Any communication errors will report the exception.
	 */
	void sendTaskAccepted(long jobId, int taskId)
			throws ProcessConnectorException;

	/**
	 * Sends <code>TaskError</code> operation to the framework with no
	 * description.
	 * 
	 * @param jobId
	 *            Job identifier the task belongs to.
	 * @param taskId
	 *            Identifier of the task which cannot be processed.
	 * @param reason
	 *            Reason why the task cannot be processed.
	 * @throws ProcessConnectorException
	 *             Any communication errors will report the exception.
	 */
	void sendTaskError(long jobId, int taskId, TaskErrorReasonType reason)
			throws ProcessConnectorException;

	/**
	 * Sends <code>TaskError</code> operation to the framework with more
	 * detailed description.
	 * 
	 * @param jobId
	 *            Job identifier the task belongs to.
	 * @param taskId
	 *            Identifier of the task which cannot be processed.
	 * @param reason
	 *            Reason why the task cannot be processed.
	 * @param description
	 *            More detailed description about error.
	 * @throws ProcessConnectorException
	 *             Any communication errors will report the exception.
	 */
	void sendTaskError(long jobId, int taskId,
			TaskErrorReasonType reason, String description)
			throws ProcessConnectorException;

	/**
	 * Sends <code>TaskCompleted</code> operation to the framework with no new
	 * objects and no warnings.
	 * 
	 * @param jobId
	 *            Job identifier the task belongs to.
	 * @param taskId
	 *            Completed task identifier.
	 * @throws ProcessConnectorException
	 *             Any communication errors will report the exception.
	 */
	void sendTaskCompleted(long jobId, int taskId)
			throws ProcessConnectorException;

	/**
	 * Sends <code>TaskCompleted</code> operation to the framework with
	 * identifiers of new objects created during task processing and no
	 * warnings.
	 * 
	 * @param jobId
	 *            Job identifier the task belongs to.
	 * @param taskId
	 *            Completed task identifier.
	 * @param objects
	 *            New objects identifiers created during processing the task.
	 * @throws ProcessConnectorException
	 *             Any communication errors will report the exception.
	 */
	void sendTaskCompleted(long jobId, int taskId, Set<Long> objects)
			throws ProcessConnectorException;

	/**
	 * Sends <code>TaskCompleted</code> operation to the framework with
	 * identifiers of new objects created during processing and any non-critical
	 * warnings.
	 * 
	 * @param jobId
	 *            Job identifier the task belongs to.
	 * @param taskId
	 *            Completed task identifier.
	 * @param objects
	 *            New objects identifiers created during processing the task.
	 * @param warnings
	 *            Non-critical warnings reported during processing the task.
	 * @throws ProcessConnectorException
	 *             Any communication errors will report the exception.
	 */
	void sendTaskCompleted(long jobId, int taskId, Set<Long> objects,
			List<String> warnings) throws ProcessConnectorException;
}
