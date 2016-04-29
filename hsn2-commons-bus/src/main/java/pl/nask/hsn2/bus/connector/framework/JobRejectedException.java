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

package pl.nask.hsn2.bus.connector.framework;

/**
 * This exception can be thrown in <code>JobDescriptor</code> operation
 * request if job rejected by the framework. 
 * 
 *
 */
public class JobRejectedException extends FrameworkConnectorException {

	private static final long serialVersionUID = -576850818102699121L;

	private String reason;
	
	/**
	 * Default constructor.
	 * 
	 * @param reason Reason why a job has been rejected.
	 */
	public JobRejectedException(String reason) {
		super();
		this.reason = reason;
	}

	/**
	 * Gets a reason why a job has been rejected.
	 * @return The reason why job has been rejected.
	 */
	public final String getReason() {
		return reason;
	}
}
