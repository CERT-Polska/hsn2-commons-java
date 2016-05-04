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
 * This is InfoData operation.
 * 
 *
 */
public class InfoData implements Operation {

	/**
	 * Type of the request.
	 */
	private InfoType type;
	
	/**
	 * Returned data.
	 */
	private ObjectData data;

	/**
	 * Default constructor.
	 * 
	 * @param type Type of the request.
	 * @param data Returned data.
	 */
	public InfoData(InfoType type, ObjectData data) {
		this.type = type;
		this.data = data;
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
	 * Gets returned data.
	 * @return Returned data.
	 */
	public final ObjectData getData() {
		return data;
	}
}
