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

import pl.nask.hsn2.bus.operations.WorkflowUploadRequest;

/**
 * This is a builder for <code>WorkflowUploadRequest</code> operation.
 * 
 *
 */
public class WorkflowUploadRequestBuilder implements OperationBuilder<WorkflowUploadRequest> {

	/**
	 * Internal variable.
	 */
	private WorkflowUploadRequest workflowUploadRequest;

	/**
	 * Default constructor.
	 * 
	 * @param name The name of the workflow.
	 * @param content Content of the workflow.
	 * @param override Flag if the workflow content should be override or not.
	 */
	public WorkflowUploadRequestBuilder(String name, String content, boolean override) {
		this.workflowUploadRequest = new WorkflowUploadRequest(name, content, override);
	}
	
	/**
	 * Returns current WorkflowUploadRequest instance.
	 * 
	 * @return Actual WorkflowUploadRequest instance.
	 */
	@Override
	public final WorkflowUploadRequest build() {
		return this.workflowUploadRequest;
	}

}
