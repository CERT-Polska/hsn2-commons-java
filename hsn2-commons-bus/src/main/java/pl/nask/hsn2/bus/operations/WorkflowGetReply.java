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

/**
 * This is a response operation for <code>WorkflowGetRequest</code>.
 * 
 *
 */
public class WorkflowGetReply implements Operation {

	/**
	 * Full content of the workflow.
	 */
	private String conetnt;
	
	/**
	 * Revision of the workflow in a framework.
	 */
	private String revision;
	
	/**
	 * Default constructor.
	 * 
	 * @param content Content of the workflow.
	 * @param revision Revision of the workflow.
	 */
	public WorkflowGetReply(String content, String revision) {
		this.conetnt = content;
		this.revision = revision;
	}

	/**
	 * Gets content of the workflow.
	 * @return Content of the workflow.
	 */
	public final String getConetnt() {
		return conetnt;
	}

	/**
	 * Gets revision of the workflow.
	 * 
	 * @return Revison of the workflow.
	 */
	public final String getRevision() {
		return revision;
	}
}
