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
 * This is static class for access to Bus.
 * 
 *
 */
public final class BusManager {

	/**
	 * Hidden constructor.
	 */
	private BusManager() {
		// this is utility class
	}
	
	/**
	 * Bus instance.
	 */
	private static Bus bus;

	/**
	 * get current instance of the Bus.
	 * 
	 * @return Bus instance.
	 */
	public static Bus getBus() {
		if (bus == null)
			throw new IllegalStateException("Bus is not initialised!");
		return bus;
	}

	/**
	 * Sets current implementation of the Bus.
	 * 
	 * @param bus Bus implementation.
	 */
	public static void setBus(final Bus bus) {
		BusManager.bus = bus;
	}
	
}
