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

package pl.nask.hsn2.bus.serializer.protobuf;

import pl.nask.hsn2.bus.operations.QueryStructure;
import pl.nask.hsn2.bus.operations.builder.QueryStructureBuilder;

public class ProtoBufQueryStructureSerializer {

	private ProtoBufQueryStructureSerializer() {
		// this is utility class
	}

	public static pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure serializeQueryStructure(QueryStructure query) {
		pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure.Builder builder = pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure
				.newBuilder();
		builder.setType(pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure.QueryType.valueOf(query.getType().name()));
		builder.setNegate(query.isNegate());
		switch (query.getType()) {
			case BY_ATTR_NAME:
				builder.setAttrName(query.getAttributeName());
				break;
			case BY_ATTR_VALUE:
				pl.nask.hsn2.protobuff.Object.Attribute.Builder atBuilder = pl.nask.hsn2.protobuff.Object.Attribute.newBuilder();
				builder.setAttrValue(ProtoBufAttributeSerializer.serializeAttribute(atBuilder, query.getValue()));
				break;
			default:
				// nothing to do
				break;
		}
		return builder.build();
	}

	public static QueryStructure deserializeQueryStructure(pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure query) {
		QueryStructureBuilder qsb = new QueryStructureBuilder(
				QueryStructure.QueryType.valueOf(query.getType().name()));
		qsb.setNegate(query.getNegate());
		switch(query.getType()) {
			case BY_ATTR_NAME:
				qsb.setAttributeName(query.getAttrName());
				break;
			case BY_ATTR_VALUE:
				qsb.setAttributeValue(ProtoBufAttributeSerializer.deserializeAttribute(query.getAttrValue()));
				break;
			default:
				// nothing to do
				break;
		}
		return qsb.build();
	}
}
