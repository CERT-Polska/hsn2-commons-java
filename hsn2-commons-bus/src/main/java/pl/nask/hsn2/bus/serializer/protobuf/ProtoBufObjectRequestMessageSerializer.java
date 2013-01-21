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

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.ObjectRequest;
import pl.nask.hsn2.bus.operations.QueryStructure;
import pl.nask.hsn2.bus.operations.builder.ObjectRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>ObjectRequest</code> operation.
 * 
 *
 */
public class ProtoBufObjectRequestMessageSerializer implements
		MessageSerializer<ObjectRequest> {

	@Override
	public final Message serialize(ObjectRequest operation)
			throws MessageSerializerException {
		
		
		pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.Builder orb = pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.newBuilder()
    	        .setType(pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.RequestType.valueOf(operation.getRequestType().name()))
				.setJob(operation.getJobId());
		
		switch (operation.getRequestType()) {
			case GET:
				for (Long objId : operation.getObjects()) {
					orb.addObjects(objId);
				}
				break;
			case PUT:
				if (operation.getTaskId() > 0) {
					orb.setTaskId(operation.getTaskId());
				}
    			for (ObjectData od : operation.getData()) {
    				orb.addData(ProtoBufObjectDataSerializer.serializeObjectData(od));
    			}
    			break;
			case UPDATE:
    	        orb.setOverwrite(operation.isOverride());
    			for (ObjectData od : operation.getData()) {
    				orb.addData(ProtoBufObjectDataSerializer.serializeObjectData(od));
    			}
    			break;
			case QUERY:
				for (QueryStructure query : operation.getQuery()) {
					orb.addQuery(ProtoBufQueryStructureSerializer.serializeQueryStructure(query));
				}
				break;
			default:
				// nothing to do
				break;
		}
		
		return new Message("ObjectRequest", orb.build().toByteArray(), null);
	}

	@Override
	public final ObjectRequest deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest pbObjectRequest =
					pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.parseFrom(message.getBody());

			ObjectRequestBuilder orb = new ObjectRequestBuilder(
					ObjectRequest.RequestType.valueOf(pbObjectRequest.getType()
							.name()), pbObjectRequest.getJob());
			switch(pbObjectRequest.getType()) {
				case GET:
					orb.addAllObjects(pbObjectRequest.getObjectsList());
					break;
				case PUT:
					orb.setTaskId(pbObjectRequest.getTaskId());
					for (pl.nask.hsn2.protobuff.Object.ObjectData od : pbObjectRequest.getDataList()) {
						orb.addData(ProtoBufObjectDataSerializer.deserializeObjectData(od));
					}
					break;
				case UPDATE:
					orb.setOverride(pbObjectRequest.getOverwrite());
					for (pl.nask.hsn2.protobuff.Object.ObjectData od : pbObjectRequest.getDataList()) {
						orb.addData(ProtoBufObjectDataSerializer.deserializeObjectData(od));
					}
					break;
				case QUERY:
					for (pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure qs : pbObjectRequest.getQueryList()) {
						switch(qs.getType().getNumber()) {
							case pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure.QueryType.BY_ATTR_NAME_VALUE:
								orb.addByAttributeNameQuery(qs.getAttrName(), qs.getNegate());
								break;
							case pl.nask.hsn2.protobuff.ObjectStore.ObjectRequest.QueryStructure.QueryType.BY_ATTR_VALUE_VALUE:
								orb.addByAttributeValueQuery(ProtoBufAttributeSerializer.deserializeAttribute(qs.getAttrValue()), qs.getNegate());
								break;
							default:
								break;
						}
					}
					break;
				default:
					// nothing to do
					break;
			}
			return orb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
