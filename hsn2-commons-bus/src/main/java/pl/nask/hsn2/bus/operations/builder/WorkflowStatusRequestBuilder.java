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

import pl.nask.hsn2.bus.operations.WorkflowStatusRequest;

/**
 * This is a builder for <code>WorkflowStatusRequest</code> operation.
 * 
 *
 */
public class WorkflowStatusRequestBuilder implements OperationBuilder<WorkflowStatusRequest> {

	/**
	 * Internal variable.
	 */
	private WorkflowStatusRequest workflowStatusRequest;
	
	/**
	 * Default constructor.
	 * 
	 * @param name The name of the workflow.
	 */
	public WorkflowStatusRequestBuilder(String name) {
		this.workflowStatusRequest = new WorkflowStatusRequest(name);
	}
	
	/**
	 * Constructor with revision argument.
	 * 
	 * @param name The name of the workflow.
	 * @param revision Revision.
	 */
	public WorkflowStatusRequestBuilder(String name, String revision) {
		this.workflowStatusRequest = new WorkflowStatusRequest(name, revision);
	}

	/**
	 * Sets expected revision.
	 * 
	 * @param revision Expected revision.
	 * @return This builder instance.
	 */
	public final WorkflowStatusRequestBuilder setRevision(String revision) {
		this.workflowStatusRequest.setRevision(revision);
		return this;
	}

	/**
	 * Returns current Attribute instance.
	 * 
	 * @return Actual Attribute instance.
	 */
	@Override
	public final WorkflowStatusRequest build() {
		return this.workflowStatusRequest;
	}
}
