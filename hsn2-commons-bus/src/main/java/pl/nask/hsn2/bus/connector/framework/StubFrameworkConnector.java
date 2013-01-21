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

package pl.nask.hsn2.bus.connector.framework;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import pl.nask.hsn2.bus.operations.JobInfo;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.WorkflowBasicInfo;

/**
 * This is stub for FrameworkConnector.
 * It's for tests purpose only.
 * 
 *
 */
public class StubFrameworkConnector implements FrameworkConnector {

	@Override
	public long sendJobDescriptor(String workflowId, String workflowVersion,
			Map<String, Properties> servicesConfigs)
			throws FrameworkConnectorException, JobRejectedException {
		return 0;
	}

	@Override
	public long sendJobDescriptor(String workflowId, String workflowVersion)
			throws FrameworkConnectorException, JobRejectedException {
		return 0;
	}

	@Override
	public long sendJobDescriptor(String workflowId)
			throws FrameworkConnectorException, JobRejectedException {
		return 0;
	}

	@Override
	public List<JobInfo> sendJobsListRequest()
			throws FrameworkConnectorException {
		return null;
	}

	@Override
	public ObjectData sendJobDetails(long jobId)
			throws FrameworkConnectorException {
		return null;
	}

	@Override
	public List<WorkflowBasicInfo> sendListWorkflows(boolean enabledOnly)
			throws FrameworkConnectorException {
		return null;
	}

	@Override
	public List<WorkflowBasicInfo> sendListWorkflows()
			throws FrameworkConnectorException {
		return null;
	}

	@Override
	public String sendWorkflowUpload(String name, String content,
			boolean override) throws FrameworkConnectorException {
		return null;
	}

	@Override
	public String sendWorkflowUpload(String name, String content)
			throws FrameworkConnectorException {
		return null;
	}

	@Override
	public boolean sendPing() {
		return false;
	}

	@Override
	public Properties sendGetConfig() throws FrameworkConnectorException {
		return null;
	}

	@Override
	public boolean sendSetConfig(Properties properties, boolean override)
			throws FrameworkConnectorException {
		return false;
	}

	@Override
	public boolean sendSetConfig(Properties properties)
			throws FrameworkConnectorException {
		return false;
	}
}
