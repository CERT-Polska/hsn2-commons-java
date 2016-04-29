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
 * This is <code>InfoError</code> operation.
 * 
 *
 */
public class InfoError implements Operation {

	/**
	 * Type of info request.
	 */
	private InfoType type;
	
	/**
	 * Reason of the error.
	 */
	private String reason;
	
	/**
	 * Default constructor.
	 * 
	 * @param type Type of the request.
	 * 
	 * @param reason Reason of the error.
	 */
	public InfoError(InfoType type, String reason) {
		this.type = type;
		this.reason = reason;
	}

	/**
	 * Gets type of the request.
	 * 
	 * @return Type of the request.
	 */
	public final InfoType getType() {
		return type;
	}

	/**
	 * Gets reason of the error.
	 * 
	 * @return Reason of the error.
	 */
	public final String getReason() {
		return reason;
	}
}
