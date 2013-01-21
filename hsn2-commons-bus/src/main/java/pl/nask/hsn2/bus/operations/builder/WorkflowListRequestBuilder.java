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

import pl.nask.hsn2.bus.operations.WorkflowListRequest;

/**
 * This is a builder for <code>WorkflowListRequest</code> operation.
 * 
 *
 */
public class WorkflowListRequestBuilder implements OperationBuilder<WorkflowListRequest> {

	/**
	 * Internal variable.
	 */
	private WorkflowListRequest workflowListRequest;
	
	/**
	 * Default constructor, created request with enbaled only flag set on <code>false</code>.
	 */
	public WorkflowListRequestBuilder() {
		this.workflowListRequest = new WorkflowListRequest();
	}

	/**
	 * Constructor with enabled flag provided.
	 * 
	 * @param enabledOnly Enabled workflows flag.
	 */
	public WorkflowListRequestBuilder(boolean enabledOnly) {
		this.workflowListRequest = new WorkflowListRequest(enabledOnly);
	}

	/**
	 * Returns current WorkflowListRequest instance.
	 * 
	 * @return Actual WorkflowListRequest instance.
	 */
	@Override
	public final WorkflowListRequest build() {
		return this.workflowListRequest;
	}

}
