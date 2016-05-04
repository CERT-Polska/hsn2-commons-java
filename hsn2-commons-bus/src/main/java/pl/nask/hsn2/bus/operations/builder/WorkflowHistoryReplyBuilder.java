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

import java.util.List;

import pl.nask.hsn2.bus.operations.WorkflowHistoryReply;
import pl.nask.hsn2.bus.operations.WorkflowRevisionInfo;

/**
 * This is a builder for <code>WorkflowHistoryReply</code> operation.
 * 
 *
 */
public class WorkflowHistoryReplyBuilder implements OperationBuilder<WorkflowHistoryReply> {
	
	/**
	 * Internal variable.
	 */
	private WorkflowHistoryReply workflowHistoryReply;

	/**
	 * Default constructor creates WorkflowHistoryReply
	 * with empty history list.
	 * 
	 */
	public WorkflowHistoryReplyBuilder() {
		this.workflowHistoryReply = new WorkflowHistoryReply();
	}
	
	/**
	 * Constructor created WorkflowHistoryReply with
	 * provided history list.
	 * 
	 * @param history History list.
	 */
	public WorkflowHistoryReplyBuilder(List<WorkflowRevisionInfo> history) {
		this.workflowHistoryReply = new WorkflowHistoryReply(history);
	}
	
	/**
	 * Adds entry to the workflow history list.
	 * 
	 * @param entry The entry to be added.
	 * @return This builder instance.
	 */
	public final WorkflowHistoryReplyBuilder addHistoryEntry(WorkflowRevisionInfo entry) {
		this.workflowHistoryReply.getHistory().add(entry);
		return this;
	}
	
	/**
	 * Adds entry to the workflow history list.
	 * 
	 * @param revision The entry revision.
	 * @param mtime The entry modified time.
	 * @return This builder instance.
	 */
	public final WorkflowHistoryReplyBuilder addHistoryEntry(String revision, long mtime) {
		this.workflowHistoryReply.getHistory().add(new WorkflowRevisionInfo(revision, mtime));
		return this;
	}
	
	/**
	 * Returns current WorkflowHistoryReply instance.
	 * 
	 * @return Actual WorkflowHistoryReply instance.
	 */
	@Override
	public final WorkflowHistoryReply build() {
		return this.workflowHistoryReply;
	}

}
