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

import pl.nask.hsn2.bus.operations.WorkflowGetRequest;

/**
 * This is a builder for <code>WorkflowGetRequest</code> operation.
 * 
 *
 */
public class WorkflowGetRequestBuilder implements OperationBuilder<WorkflowGetRequest> {

	/**
	 * Internal variable.
	 */
	private WorkflowGetRequest workflowGetRequest;

	/**
	 * Default constructor for the builder.
	 * 
	 * @param name The name of the workflow.
	 */
	public WorkflowGetRequestBuilder(String name) {
		this(name, null);
	}
	
	/**
	 * Constructor with revision provided.
	 * 
	 * @param name The name of the workflow.
	 * @param revision Revision of the workflow.
	 */
	public WorkflowGetRequestBuilder(String name, String revision) {
		this.workflowGetRequest = new WorkflowGetRequest(name, revision);
	}
	
	/**
	 * Sets revision of the workflow.
	 * 
	 * @param revision Revision of the workflow.
	 * @return Instance of this builder. 
	 */
	public final WorkflowGetRequestBuilder setRevision(String revision) {
		this.workflowGetRequest.setRevision(revision);
		return this;
	}
	
	/**
	 * Returns current WorkflowGetRequest instance.
	 * 
	 * @return Actual WorkflowGetRequest instance.
	 */
	@Override
	public final WorkflowGetRequest build() {
		return this.workflowGetRequest;
	}
}
