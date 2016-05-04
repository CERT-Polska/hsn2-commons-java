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

/**
 * This is a builder for <code>WorkflowRevisionInfo</code> operation.
 * 
 *
 */
public class WorkflowRevisionInfoBuilder implements OperationBuilder<WorkflowRevisionInfo> {

	/**
	 * Internal variable.
	 */
	private WorkflowRevisionInfo workflowRevisionInfo;
	
	/**
	 * Default constructor.
	 * 
	 * @param revision Revision of a workflow.
	 * @param mtime Last modified date.
	 */
	public WorkflowRevisionInfoBuilder(String revision, long mtime) {
		this.workflowRevisionInfo = new WorkflowRevisionInfo(revision, mtime);
	}

	/**
	 * Returns current WorkflowRevisionInfo instance.
	 * 
	 * @return Actual WorkflowRevisionInfo instance.
	 */
	@Override
	public final WorkflowRevisionInfo build() {
		return workflowRevisionInfo;
	}

}
