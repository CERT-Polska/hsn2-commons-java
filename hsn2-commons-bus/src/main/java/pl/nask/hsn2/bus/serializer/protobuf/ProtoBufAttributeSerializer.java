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

import pl.nask.hsn2.bus.operations.Attribute;
import pl.nask.hsn2.bus.operations.AttributeType;
import pl.nask.hsn2.bus.operations.builder.ReferenceBuilder;

public class ProtoBufAttributeSerializer {

	private ProtoBufAttributeSerializer() {
		// this is utility class
	}
	
	public static Attribute deserializeAttribute(pl.nask.hsn2.protobuff.Object.Attribute pbAttribute) {
		Attribute attribute = new Attribute(pbAttribute.getName());
		switch(pbAttribute.getType()) {
			case EMPTY:
				attribute.setValue(AttributeType.EMPTY, null);
				break;
			case BOOL:
				attribute.setValue(AttributeType.BOOL, pbAttribute.getDataBool());
				break;
			case INT:
				attribute.setValue(AttributeType.INT, pbAttribute.getDataInt());
				break;
			case TIME:
				attribute.setValue(AttributeType.TIME, pbAttribute.getDataTime());
				break;
			case FLOAT:
				attribute.setValue(AttributeType.FLOAT, pbAttribute.getDataFloat());
				break;
			case STRING:
				attribute.setValue(AttributeType.STRING, pbAttribute.getDataString());
				break;
			case OBJECT:
				attribute.setValue(AttributeType.OBJECT, pbAttribute.getDataObject());
				break;
			case BYTES:
			ReferenceBuilder rfBuilder = new ReferenceBuilder(
					pbAttribute.getDataBytes().getKey(),
					pbAttribute.getDataBytes().getStore());
				attribute.setValue(AttributeType.BYTES, rfBuilder.build());
				break;
			default:
				// nothing to do
				break;
		}
		return attribute;
	}
	
	public static pl.nask.hsn2.protobuff.Object.Attribute serializeAttribute(pl.nask.hsn2.protobuff.Object.Attribute.Builder atBuilder, Attribute attribute) {
		atBuilder.clear();
		atBuilder.setName(attribute.getName());
		switch (attribute.getType()) {
			case EMPTY:
					atBuilder.setType(pl.nask.hsn2.protobuff.Object.Attribute.Type.EMPTY);
					break;
			case BOOL:
					atBuilder.setType(pl.nask.hsn2.protobuff.Object.Attribute.Type.BOOL);
					atBuilder.setDataBool(attribute.getBool());
					break;
			case INT:
					atBuilder.setType(pl.nask.hsn2.protobuff.Object.Attribute.Type.INT);
					atBuilder.setDataInt(attribute.getInteger());
					break;
			case TIME:
					atBuilder.setType(pl.nask.hsn2.protobuff.Object.Attribute.Type.TIME);
					atBuilder.setDataTime(attribute.getTime());
					break;
			case FLOAT:
					atBuilder.setType(pl.nask.hsn2.protobuff.Object.Attribute.Type.FLOAT);
					atBuilder.setDataFloat(attribute.getFloat());
					break;
			case STRING:
					atBuilder.setType(pl.nask.hsn2.protobuff.Object.Attribute.Type.STRING);
					atBuilder.setDataString(attribute.getString());
					break;
			case OBJECT:
					atBuilder.setType(pl.nask.hsn2.protobuff.Object.Attribute.Type.OBJECT);
					atBuilder.setDataObject(attribute.getObejectRef());
					break;
			case BYTES:
					pl.nask.hsn2.protobuff.Object.Reference.Builder rfBuilder = pl.nask.hsn2.protobuff.Object.Reference.newBuilder();
					atBuilder.setType(pl.nask.hsn2.protobuff.Object.Attribute.Type.BYTES);
					rfBuilder.clear();
					rfBuilder.setKey(attribute.getBytes().getKey());
					rfBuilder.setStore(attribute.getBytes().getStore());
					atBuilder.setDataBytes(rfBuilder.build());
					break;
			default:
				// nothing to do
				break;
		}
		return atBuilder.build();
	}

}
