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

package pl.nask.hsn2.bus.serializer.protobuf;

import java.util.ArrayList;
import java.util.List;

import pl.nask.hsn2.bus.operations.Attribute;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.builder.ObjectDataBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public abstract class ProtoBufObjectDataSerializer {

	private ProtoBufObjectDataSerializer() {
		// this is utility class
	}
	
	public static pl.nask.hsn2.protobuff.Object.ObjectData serializeObjectData(ObjectData operation)
			throws MessageSerializerException {

		pl.nask.hsn2.protobuff.Object.ObjectData.Builder odBuilder = pl.nask.hsn2.protobuff.Object.ObjectData.newBuilder();
		if (operation.getId() > 0) {
			odBuilder.setId(operation.getId());
		}
		pl.nask.hsn2.protobuff.Object.Attribute.Builder atBuilder = pl.nask.hsn2.protobuff.Object.Attribute.newBuilder();
		for (Attribute attribute : operation.getAttributes()) {
			odBuilder.addAttrs(ProtoBufAttributeSerializer.serializeAttribute(atBuilder, attribute));
		}
		return odBuilder.build();
	}
	
	public static List<pl.nask.hsn2.protobuff.Object.ObjectData> serializeObjectData(List<ObjectData> operations)
			throws MessageSerializerException {
		List<pl.nask.hsn2.protobuff.Object.ObjectData> result = new ArrayList<pl.nask.hsn2.protobuff.Object.ObjectData>(operations.size());
		for (ObjectData op: operations) {
			result.add(serializeObjectData(op));
		}
		return result;
	}

	public static ObjectData deserializeObjectData(pl.nask.hsn2.protobuff.Object.ObjectData pbObjectData) {
		ObjectDataBuilder odBuilder = new ObjectDataBuilder();
		odBuilder.setId(pbObjectData.getId());
		if (pbObjectData.getAttrsCount() > 0) {
			for (pl.nask.hsn2.protobuff.Object.Attribute pbAttribute : pbObjectData.getAttrsList()) {
				odBuilder.addAttribute(ProtoBufAttributeSerializer.deserializeAttribute(pbAttribute));
			}
		}
		return odBuilder.build();
	}
}
