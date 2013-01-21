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
 * This is operation represents object reference.
 * 
 *
 */
public class Reference implements Operation {

	/**
	 * Identifier of the object.
	 */
	private long key;
	
	/**
	 * Identifier of the store
	 * (can be 0 if there is only one store).
	 */
	private int store;

	/**
	 * Default constructor creates reference
	 * with provided key and store sets on 0.
	 * 
	 * @param key Object reference.
	 */
	public Reference(long key) {
		this(key, 0);
	}
	
	/**
	 * Constructor created reference with provided
	 * key and target store.
	 * 
	 * @param key Reference of the object.
	 * @param store Store where is the object.
	 */
	public Reference(long key, int store) {
		this.key = key;
		this.store = store;
	}

	/**
	 * Gets object reference.
	 * 
	 * @return Object reference.
	 */
	public final long getKey() {
		return key;
	}

	/**
	 * Sets object reference.
	 * 
	 * @param key Object reference.
	 */
	public final void setKey(long key) {
		this.key = key;
	}

	/**
	 * Gets store where is the object.
	 * 
	 * @return Identifier of the store or 0 if default.
	 */
	public final int getStore() {
		return store;
	}

	/**
	 * Sets store identifier where is the object.
	 * 
	 * @param store Identifier of the store.
	 */
	public final void setStore(int store) {
		this.store = store;
	}

	@Override
	public String toString() {
		return "Reference [key=" + key + ", store=" + store + "]";
	}
	
	
}
