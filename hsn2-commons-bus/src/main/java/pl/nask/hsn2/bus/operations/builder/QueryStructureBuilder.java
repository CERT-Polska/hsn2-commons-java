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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.operations.Attribute;
import pl.nask.hsn2.bus.operations.QueryStructure;

/**
 * This is a builder for <code>QueryStructure</code> operation.
 * 
 *
 */
public class QueryStructureBuilder implements OperationBuilder<QueryStructure> {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryStructureBuilder.class);

	private QueryStructure queryStructure;

	public QueryStructureBuilder(QueryStructure.QueryType type) {
		this.queryStructure = new QueryStructure(type);
	}
	
	public final QueryStructureBuilder setAttributeName(String name) {
		if (this.queryStructure.getType() != QueryStructure.QueryType.BY_ATTR_NAME) {
			LOGGER.warn("Name allowed only for BY_ATTR_NAME query type. Ignoring...");
		} else {
			this.queryStructure.setAttributeName(name);
		}
		return this;
	}

	public final QueryStructureBuilder setAttributeValue(Attribute value) {
		if (this.queryStructure.getType() != QueryStructure.QueryType.BY_ATTR_VALUE) {
			LOGGER.warn("Value allowed only for BY_ATTR_VALUE query type. Ignoring...");
		} else {
			this.queryStructure.setValue(value);
		}
		return this;
	}

	public final QueryStructureBuilder setNegate(boolean negate) {
		this.queryStructure.setNegate(negate);
		return this;
	}

	@Override
	public final QueryStructure build() {
		return this.queryStructure;
	}
}
