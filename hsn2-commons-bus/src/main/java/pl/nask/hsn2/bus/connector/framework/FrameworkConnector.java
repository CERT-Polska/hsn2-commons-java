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

package pl.nask.hsn2.bus.connector.framework;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import pl.nask.hsn2.bus.connector.AbstractServicesConnector;
import pl.nask.hsn2.bus.operations.JobInfo;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.WorkflowBasicInfo;

/**
 *  This is unified connector to the framework.
 *  
 *
 */
public interface FrameworkConnector extends AbstractServicesConnector{

	/**
	 * Sends JobDescriptor operation to the framework.
	 * 
	 * @param workflowId Identifier of the workflow to run job against of.
	 * @param workflowVersion Version of the workflow.
	 * @param servicesConfigs Additional services configuration for the job.
	 * @return Accepted job identifier.
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	long sendJobDescriptor(String workflowId, String workflowVersion,
			Map<String, Properties> servicesConfigs)
					throws FrameworkConnectorException, JobRejectedException;
	/**
	 * 
	 * Sends JobDescriptor operation to the framework.
	 * 
	 * @param workflowId Identifier of the workflow to run job against of.
	 * @param workflowVersion Version of the workflow.
	 * @return Accepted job identifier.
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	long sendJobDescriptor(String workflowId, String workflowVersion)
			throws FrameworkConnectorException, JobRejectedException;

	/**
	 * Sends JobDescriptor operation to the framework.
	 * 
	 * @param workflowId Identifier of the workflow to run job against of.
	 * @return Accepted job identifier.
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	long sendJobDescriptor(String workflowId)
			throws FrameworkConnectorException, JobRejectedException;
	
	/**
	 * Sends JobListRequest operation to the framework.
	 * 
	 * @return List of jobs with statuses in the framework.
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	List<JobInfo> sendJobsListRequest() throws FrameworkConnectorException;

	/**
	 * Sends a request to get details of the job.
	 * 
	 * @param jobId Job identifier.
	 * @return Information about the job or null if not found.
	 * 
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	ObjectData sendJobDetails(long jobId)
			throws FrameworkConnectorException;
	
	/**
	 * Sends a request to get list of available workflows in a framework.
	 * 
	 * @param enabledOnly Flag if only enabled workflows should be selected.
	 * @return List of selected workflows.
	 * 
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	List<WorkflowBasicInfo> sendListWorkflows(boolean enabledOnly)
			throws FrameworkConnectorException;
	
	/**
	 * Sends a request to get list of available workflows in a framework.
	 * 
	 * @return List of all workflows.
	 * 
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	List<WorkflowBasicInfo> sendListWorkflows()
				throws FrameworkConnectorException;

	/**
	 * Sends <code>WorkflowUploadRequest</code> operation to the framework.
	 *
	 * @param name Name of the workflow to be created/overridden.
	 * @param content Content of the workflow to be uploaded.
	 * @param override Flag if the workflow need to be override if exists.
	 * @return Current revision of the workflow.
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	String sendWorkflowUpload(String name, String content,
			boolean override) throws FrameworkConnectorException;

	/**
	 * Sends <code>WorkflowUploadRequest</code> operation to the framework.
	 *
	 * @param name Name of the workflow.
	 * @param content Content of the workflow to be uploaded.
	 * @return Current revision of the workflow.
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	String sendWorkflowUpload(String name, String content)
			throws FrameworkConnectorException;

	/**
	 * Sends <code>Ping</code> operation to the framework.
	 *
	 * @return <code>true</code> if framework responds.
	 */
	boolean sendPing();

	/**
	 * Sends <code>GetConfigRequest</code> to the framework.
	 * 
	 * @return Configuration of the framework.
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	Properties sendGetConfig() throws FrameworkConnectorException;

	/**
	 * Sets configuration of the framework.
	 * 
	 * @param properties Configuration to be set.
	 * @param override Flag if current keys should be override or not.
	 * @return <code>true</code> if success.
	 * 
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	boolean sendSetConfig(Properties properties, boolean override)
			throws FrameworkConnectorException;

	/**
	 * Sets configuration of the framework  but existing key will NOT be overriden.
	 * 
	 * @param properties Configuration to be set.
	 * @return <code>true</code> if success.
	 * 
	 * @throws FrameworkConnectorException Exception will thrown if there
	 *         is any problem with the framework communication.
	 */
	boolean sendSetConfig(Properties properties)
			throws FrameworkConnectorException;
	boolean sendJobCancelRequest(long jobId) throws FrameworkConnectorException;
}
