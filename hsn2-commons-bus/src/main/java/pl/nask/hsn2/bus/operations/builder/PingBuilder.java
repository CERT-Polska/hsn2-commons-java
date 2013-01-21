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

package pl.nask.hsn2.bus.operations.builder;

import pl.nask.hsn2.bus.operations.Ping;

/**
 * This is a builder for <code>Ping</code> operation.
 * 
 *
 */
public class PingBuilder implements OperationBuilder<Ping> {

	/**
	 * Internal variable.
	 */
	private Ping ping;

	/**
	 * Default constructor.
	 */
	public PingBuilder() {
		this.ping = new Ping();
	}

	/**
	 * Constructor.with response flag.
	 */
	public PingBuilder(boolean response) {
		this.ping = new Ping(response);
	}

	/**
	 * Returns current Ping instance.
	 * 
	 * @return Actual Ping instance.
	 */
	@Override
	public final Ping build() {
		return this.ping;
	}
}
