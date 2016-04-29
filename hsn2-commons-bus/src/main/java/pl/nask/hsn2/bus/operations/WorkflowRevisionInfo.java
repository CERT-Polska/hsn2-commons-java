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
 * This is an operation represents revision of a workflow.
 * 
 *
 */
public class WorkflowRevisionInfo implements Operation {

	/**
	 * Revision of a workflow.
	 */
	private String revision;
	
	/**
	 * Last modification time.
	 */
	private long mtime;
	
	/**
	 * Default constructor.
	 * 
	 * @param revision Revision of a workflow.
	 * @param mtime Last modified date.
	 */
	public WorkflowRevisionInfo(String revision, long mtime) {
		this.revision = revision;
		this.mtime = mtime;
	}

	/**
	 * Gets workflow revision.
	 * 
	 * @return Revision of a workflow.
	 */
	public final String getRevision() {
		return revision;
	}

	/**
	 * Gets workflow last modified date.
	 * 
	 * @return Last modified date.
	 */
	public final long getMtime() {
		return mtime;
	}
}
