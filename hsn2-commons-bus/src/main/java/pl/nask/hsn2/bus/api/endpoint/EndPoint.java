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

package pl.nask.hsn2.bus.api.endpoint;

import pl.nask.hsn2.bus.api.BusException;

/**
 * This is main interface for all types of endpoints.
 * 
 *
 */
public interface EndPoint {

	/**
	 * Is endpoint closed?
	 * 
	 * @return True if closed, false otherwise
	 */
	boolean isClosed();
	
	/**
	 * Open endpoint if closed.
	 * Constructor by default opens it,
	 * so this method should be used
	 *  when <code>close</code> was called.
	 * 
	 * @throws BusException Exception will thrown if error.
	 */
	void open() throws BusException;
	
	/**
	 * Close endpoint. If endpoint already closed nothing will do.
	 * 
	 * @throws BusException Exception will thrown if error.
	 */
	void close() throws BusException;

}
