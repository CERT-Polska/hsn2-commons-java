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

package pl.nask.hsn2.bus.operations.builder;

import pl.nask.hsn2.bus.operations.WorkflowPolicyRequest;

/**
 * This is a builder for <code>WorkflowPolicyRequest</code> operation.
 * 
 *
 */
public class WorkflowPolicyRequestBuilder implements OperationBuilder<WorkflowPolicyRequest> {

	/**
	 * Internal variable.
	 */
	private WorkflowPolicyRequest workflowPolicyRequest;
	
	/**
	 * Default constructor.
	 * 
 	 * @param name The name of the workflow.
	 * @param enabled Flag if workflow should be enabled or not.
	 */
	public WorkflowPolicyRequestBuilder(String name, boolean enabled) {
		this.workflowPolicyRequest = new WorkflowPolicyRequest(name, enabled);
	}
	
	/**
	 * Returns current WorkflowPolicyRequest instance.
	 * 
	 * @return Actual WorkflowPolicyRequest instance.
	 */
	@Override
	public final WorkflowPolicyRequest build() {
		return this.workflowPolicyRequest;
	}

}
