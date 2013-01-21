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

/**
 * This is request operation to get a content of the workflow.
 * 
 *
 */
public class WorkflowGetRequest implements Operation {

	/**
	 * The name of the workflow.
	 */
	private String name;

	/**
	 * Optional revision of the workflow.
	 */
	private String revision;

	/**
	 * Default constructor.
	 * 
	 * @param name The name of the workflow.
	 */
	public WorkflowGetRequest(String name) {
		this(name, null);
	}
	
	/**
	 * Constructor with revision provided.
	 * 
	 * @param name The name of the workflow.
	 * @param revision Revision of the workflow.
	 */
	public WorkflowGetRequest(String name, String revision) {
		this.name = name;
		this.revision = revision;
	}

	/**
	 * Gets revision of the workflow.
	 * 
	 * @return Revision of the workflow.
	 */
	public final String getRevision() {
		return revision;
	}

	/**
	 * Sets revision of the workflow.
	 * 
	 * @param revision Revision of the workflow.
	 */
	public final void setRevision(String revision) {
		this.revision = revision;
	}

	/**
	 * Gets name of the workflow.
	 * 
	 * @return The name of the workflow.
	 */
	public final String getName() {
		return name;
	}
}
