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

package pl.nask.hsn2.bus.connector.objectstore;

/**
 * This is special handler for transformation of OBJECT type
 * objects returned from Object Store.
 * 
 *  In simplest implementation it can return just object
 *  reference. In more advanced real object can be fetched
 *  on demand.
 * 
 *
 */
public interface ObjectStoreGetCallback {

	/**
	 * Handles get operation, can be overridden by some real fetch.
	 * 
	 * @param id Id of the object to get.
	 * @return Transformed object, e.g. fetched.
	 */
	Object handleGet(long id);

}
