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

import pl.nask.hsn2.bus.operations.WorkflowRevisionInfo;
import pl.nask.hsn2.bus.operations.WorkflowStatusReply;

/**
 * This is a builder for <code>WorkflowStatusReply</code> operation.
 * 
 *
 */
public class WorkflowStatusReplyBuilder implements OperationBuilder<WorkflowStatusReply> {

	/**
	 * Internal variable.
	 */
	private WorkflowStatusReply workflowStatusReply;
	
	/**
	 * Default constructor.
	 * 
	 * @param valid Flag if the workflow is valid.
	 * @param enabled Flag if the workflow is enabled.
	 * @param info Revision information for the workflow.
	 */
	public WorkflowStatusReplyBuilder(boolean valid, boolean enabled, WorkflowRevisionInfo info) {
		this.workflowStatusReply = new WorkflowStatusReply(valid, enabled, info);
	}
	
	/**
	 * Sets description for the workflow.
	 * 
	 * @param description Description for the workflow.
	 * @return This builder instance.
	 */
	public final WorkflowStatusReplyBuilder setDescription(String description) {
		this.workflowStatusReply.setDescription(description);
		return this;
	}
	
	/**
	 * Returns current WorkflowStatusReply instance.
	 * 
	 * @return Actual WorkflowStatusReply instance.
	 */
	@Override
	public final WorkflowStatusReply build() {
		return this.workflowStatusReply;
	}
}
