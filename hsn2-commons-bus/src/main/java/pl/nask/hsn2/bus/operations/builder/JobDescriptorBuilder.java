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

package pl.nask.hsn2.bus.operations.builder;

import java.util.Map;
import java.util.Properties;

import pl.nask.hsn2.bus.operations.JobDescriptor;

/**
 * This is a builder for JobDescriptor operation.
 * 
 *
 */
public class JobDescriptorBuilder implements OperationBuilder<JobDescriptor> {

	/**
	 * Internal variable
	 */
	private JobDescriptor jobDescriptor;
	
	/**
	 * Default constructor.
	 * 
	 * @param workflowId Identifier of the workflow.
	 */
	public JobDescriptorBuilder(String workflowId) {
		this.jobDescriptor = new JobDescriptor(workflowId);
	}

	/**
	 * Sets workflow version.
	 * 
	 * @param workflowVersion Version of the workflow.
	 * @return This builder instance.
	 */
	public final JobDescriptorBuilder setWorkflowVersion(String workflowVersion) {
		if (workflowVersion != null
				&& !"".equals(workflowVersion)) {
			this.jobDescriptor.setWorkflowVersion(workflowVersion);
		}
		return this;
	}

	/**
	 * Sets all services configs at once.
	 * 
	 * @param servicesConfigs All services configurations.
	 * @return This builder instance.
	 */
	public final JobDescriptorBuilder setServicesConfigs(Map<String, Properties> servicesConfigs) {
		if (servicesConfigs != null
			&& servicesConfigs.size() > 0) {
				this.jobDescriptor.setServicesConfigs(servicesConfigs);
		} else {
			this.jobDescriptor.getServicesConfigs().clear();
		}
		return this;
	}

	/**
	 * Sets configuration for selected service.
	 * 
	 * @param serviceName Service name.
	 * @param properties Configuration for the service.
	 * @return This builder instance.
	 */
	public final JobDescriptorBuilder setServiceConfig(String serviceName, Properties properties) {
		this.jobDescriptor.getServicesConfigs().put(serviceName, properties);
		return this;
	}

	/**
	 * Adds one property to selected service.
	 * 
	 * @param serviceName Service name.
	 * @param key Parameter name.
	 * @param value Parameter value.
	 * @return This builder instance.
	 */
	public final JobDescriptorBuilder addServiceConfig(String serviceName, String key, String value) {
		Properties serviceConfig = this.jobDescriptor.getServicesConfigs().get(serviceName);
		if (serviceConfig == null) {
			this.jobDescriptor.getServicesConfigs().put(serviceName, new Properties());
			serviceConfig = this.jobDescriptor.getServicesConfigs().get(serviceName);
		}
		serviceConfig.put(key, value);
		return this;
	}

	/**
	 * Returns current Job Descriptor instance.
	 * 
	 * @return Actual JobDescriptor instance.
	 */
	@Override
	public final JobDescriptor build() {
		return this.jobDescriptor;
	}
}
