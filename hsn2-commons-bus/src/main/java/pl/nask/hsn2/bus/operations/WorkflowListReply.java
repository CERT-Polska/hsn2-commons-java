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

package pl.nask.hsn2.bus.operations;

import java.util.LinkedList;
import java.util.List;

/**
 * This is a response for <code>WorkflowListRequest</code> operation.
 * 
 *
 */
public class WorkflowListReply implements Operation {

	/**
	 * List of information of found workflows.
	 */
	private List<WorkflowBasicInfo> workflows;
	
	/**
	 * Default constructor created empty set of information.
	 */
	public WorkflowListReply() {
		this(new LinkedList<WorkflowBasicInfo>());
	}

	/**
	 * Constructor with provided set of information.
	 * 
	 * @param workflows Set of workflows information.
	 */
	public WorkflowListReply(List<WorkflowBasicInfo> workflows) {
		setWorkflows(workflows);
	}

	/**
	 * Gets a set of workflow information.
	 * 
	 * @return Set of worklow information.
	 */
	public final List<WorkflowBasicInfo> getWorkflows() {
		return workflows;
	}

	/**
	 * Sets set of workflow information.
	 * 
	 * @param workflows Set of workflow information.
	 */
	public final void setWorkflows(List<WorkflowBasicInfo> workflows) {
		if (workflows == null) {
			if (this.workflows == null) {
				this.workflows = new LinkedList<WorkflowBasicInfo>();
			} else {
				this.workflows.clear();
			}
		} else {
			this.workflows = workflows;
		}
	}
}
