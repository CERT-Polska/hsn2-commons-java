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

package pl.nask.hsn2.bus.operations;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This operation starts job in framework against of selected workflow.
 * 
 *
 */
public class JobDescriptor implements Operation {

	private String workflowId = null;
	private String workflowVersion = null;
	private Map<String, Properties> servicesConfigs = null;
	
	/**
	 * Full parameters constructor.
	 * 
	 * @param workflowId Identifier of the workflow to run job againt.
	 * @param workflowVersion Version of the workflow.
	 * @param servicesConfigs Special user parameters for services in the workflow.
	 */
	public JobDescriptor(String workflowId, String workflowVersion,
			Map<String, Properties> servicesConfigs) {
		this.workflowId = workflowId;
		this.workflowVersion = workflowVersion;
		this.servicesConfigs = servicesConfigs;
		assert servicesConfigs != null;
	}

	/**
	 * Constructor with no version parameter.
	 * 
	 * @param workflowId Identifier of the workflow to run job againt.
	 * @param servicesConfigs Special user parameters for services in the workflow.
	 */
	public JobDescriptor(String workflowId, Map<String, Properties> servicesConfigs) {
		this(workflowId, null, servicesConfigs);
	}

	/**
	 * Simplest constructor.
	 * 
	 * @param workflowId Identifier of the workflow to run job againt.
	 */
	public JobDescriptor(String workflowId) {
		this(workflowId, null, new HashMap<String, Properties>());
	}

	/**
	 * Gets workflow identifier.
	 * 
	 * @return Workflow indentifier.
	 */
	public final String getWorkflowId() {
		return workflowId;
	}

	/**
	 * Sets workflow identifier.
	 * 
	 * @param workflowId Workflow identifier.
	 */
	public final void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}

	/**
	 * Gets workflow version.
	 * 
	 * @return Workflow version.
	 */
	public final String getWorkflowVersion() {
		return workflowVersion;
	}

	/**
	 * Sets workflow version.
	 * 
	 * @param workflowVersion Workflow version.
	 */
	public final void setWorkflowVersion(String workflowVersion) {
		this.workflowVersion = workflowVersion;
	}

	/**
	 * Gets user configuration for services.
	 * 
	 * @return User configuration for services.
	 */
	public final Map<String, Properties> getServicesConfigs() {
		return servicesConfigs;
	}

	/**
	 * Sets user configuration for services.
	 * 
	 * @param servicesConfigs User configuration for services.
	 */
	public final void setServicesConfigs(Map<String, Properties> servicesConfigs) {
		this.servicesConfigs = servicesConfigs;
	}

	@Override
	public String toString() {
		return new StringBuffer(128).append("JobDescriptor={")
				.append("workflowId=").append(workflowId).append(",")
				.append("workflowVersion=").append(workflowVersion).append(",")
				.append("servicesConfigs=[").append(servicesConfigs).append("]}")
				.toString();
	}
}
