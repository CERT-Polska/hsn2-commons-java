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
 * This is sub operation of <code>ObjectRequest</code>.
 *
 */
public class QueryStructure implements Operation {

	public enum QueryType {
		BY_ATTR_NAME, BY_ATTR_VALUE;
	}
	private QueryType type;
	private String attributeName;
	private Attribute value;
	private boolean negate = false;
	
	public QueryStructure(QueryType type) {
		this.type = type;
	}

	public final QueryType getType() {
		return type;
	}

	public final String getAttributeName() {
		return attributeName;
	}

	public final void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public final Attribute getValue() {
		return value;
	}

	public final void setValue(Attribute value) {
		this.value = value;
	}

	public final boolean isNegate() {
		return negate;
	}

	public final void setNegate(boolean negate) {
		this.negate = negate;
	}
}
