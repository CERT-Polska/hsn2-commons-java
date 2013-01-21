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

import java.util.List;

import pl.nask.hsn2.bus.operations.WorkflowBasicInfo;
import pl.nask.hsn2.bus.operations.WorkflowListReply;

/**
 * This is a builder for <code>WorkflowListReply</code> operation.
 * 
 *
 */
public class WorkflowListReplyBuilder implements OperationBuilder<WorkflowListReply> {

	/**
	 * Internal variable.
	 */
	private WorkflowListReply workflowListReply;

	/**
	 * Default constructor creates reply with empty workflows set.
	 */
	public WorkflowListReplyBuilder() {
		this.workflowListReply = new WorkflowListReply();
	}

	/**
	 * Sets workflows to the reply.
	 * 
	 * @param workflows Workflows to be set.
	 * @return This buiolder instance.
	 */
	public final WorkflowListReplyBuilder setWorkflowsBasicInfos(List<WorkflowBasicInfo> workflows) {
		this.workflowListReply.setWorkflows(workflows);
		return this;
	}
	
	/**
	 * Adds <code>WorkflowsBasicInfo</code> to the reply.
	 * @param info Workflow basic infor to be added.
	 * @return This builder instance.
	 */
	public final WorkflowListReplyBuilder addWorkflowBasicInfo(WorkflowBasicInfo info) {
		this.workflowListReply.getWorkflows().add(info);
		return this;
	}

	/**
	 * Returns current WorkflowListReply instance.
	 * 
	 * @return Actual WorkflowListReply instance.
	 */
	@Override
	public final WorkflowListReply build() {
		return this.workflowListReply;
	}

}
