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

import pl.nask.hsn2.bus.operations.WorkflowBasicInfo;

/**
 * This is a builder for <code>WorkflowBasicInfo</code> operation.
 * 
 *
 */
public class WorkflowBasicInfoBuilder implements OperationBuilder<WorkflowBasicInfo> {

	/**
	 * Internal variable.
	 */
	private WorkflowBasicInfo workflowBasicInfo;
	
	/**
	 * Default constructor.
	 * 
	 * @param name The name of a workflow.
	 */
	public WorkflowBasicInfoBuilder(String name) {
		this.workflowBasicInfo = new WorkflowBasicInfo(name);
	}

	/**
	 * Sets enabled flag.
	 * 
	 * @param enabled Flag to set.
	 * @return This builder instance.
	 */
	public final WorkflowBasicInfoBuilder setEnabled(boolean enabled) {
		this.workflowBasicInfo.setEnabled(enabled);
		return this;
	}

	/**
	 * Returns current WorkflowBasicInfo instance.
	 * 
	 * @return Actual WorkflowBasicInfo instance.
	 */
	@Override
	public final WorkflowBasicInfo build() {
		return workflowBasicInfo;
	}

}
