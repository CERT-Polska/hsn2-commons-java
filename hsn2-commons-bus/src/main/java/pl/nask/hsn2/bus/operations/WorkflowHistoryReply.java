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

package pl.nask.hsn2.bus.operations;

import java.util.LinkedList;
import java.util.List;

/**
 * This is successfully response operation for <code>WorkflowHistoryRequest</code>.
 * 
 *
 */
public class WorkflowHistoryReply implements Operation{

	/**
	 * This is history list of the workflow.
	 */
	private List<WorkflowRevisionInfo> history;
	
	/**
	 * Default constructor created empty history list.
	 */
	public WorkflowHistoryReply() {
		this(null);
	}
	
	/**
	 * Constructor with history list provided.
	 * 
	 * @param history History list.
	 */
	public WorkflowHistoryReply(List<WorkflowRevisionInfo> history) {
		this.history = history;
		if (this.history == null) {
			this.history = new LinkedList<WorkflowRevisionInfo>();
		}
	}

	/**
	 * Gets history list.
	 * 
	 * @return History list.
	 */
	public final List<WorkflowRevisionInfo> getHistory() {
		return history;
	}
}
