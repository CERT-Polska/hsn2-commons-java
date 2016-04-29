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
 * This is standard request to get various information
 * depends on the type of the request.
 * 
 *
 */
public class InfoRequest implements Operation {

	/**
	 * Type of the request (context).
	 */
	private InfoType type;
	
	/**
	 * Idditional information, e.g. identifier of the job. 
	 */
	private long id;
	
	/**
	 * Default constructor with id set on 0
	 * and type provided.
	 * 
	 * @param type Type of the request.
	 */
	public InfoRequest(InfoType type) {
		this(0, type);
	}
	
	/**
	 * Constructor with identifier and type provided.
	 * @param id Identifier
	 * @param type Type of the request.
	 */
	public InfoRequest(long id, InfoType type) {
		this.id = id;
		this.type = type;
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
	 * Sets type of the request.
	 * 
	 * @param type Type of the request.
	 */
	public final void setType(InfoType type) {
		this.type = type;
	}

	/**
	 * Gets additional identifier.
	 * 
	 * @return Identifier.
	 */
	public final long getId() {
		return id;
	}

	/**
	 * Sets additional identifier.
	 * 
	 * @param id Identifier.
	 */
	public final void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return new StringBuffer(128).append("InfoRequest={")
				.append("id=").append(id).append(",")
				.append("type=").append(type).append("}")
				.toString();
	}
}
