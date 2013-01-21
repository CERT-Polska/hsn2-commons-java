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

package pl.nask.hsn2.bus.api;

/**
 * The class <code>BusException</code> and its subclasses are a form of
 * <code>Throwable</code> that indicates conditions that a reasonable
 * application might want to catch if any problem with
 * connection to the Bus occurs.
*/
public class BusException extends Exception {

	private static final long serialVersionUID = 2476319465668493285L;

	public BusException() {
	}

	public BusException(String arg0) {
		super(arg0);
	}

	public BusException(Throwable arg0) {
		super(arg0);
	}

	public BusException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
