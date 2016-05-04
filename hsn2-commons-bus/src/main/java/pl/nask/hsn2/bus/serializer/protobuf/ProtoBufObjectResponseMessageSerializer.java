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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.ObjectResponse;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>ObjectResponse</code> operation.
 * 
 *
 */
public class ProtoBufObjectResponseMessageSerializer implements MessageSerializer<ObjectResponse> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProtoBufObjectResponseMessageSerializer.class);
	
	@Override
	public final Message serialize(ObjectResponse operation)
			throws MessageSerializerException {
		
		
		pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse.Builder orb = pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse.newBuilder()
    	        .setType(pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse.ResponseType.valueOf(operation.getType().name()));
		
		switch (operation.getType()) {
			case SUCCESS_GET:
    			for (ObjectData od : operation.getData()) {
    				orb.addData(ProtoBufObjectDataSerializer.serializeObjectData(od));
    			}
				break;
			case SUCCESS_PUT:
			case SUCCESS_QUERY:
				orb.addAllObjects(operation.getObjects());
    			break;
			case SUCCESS_UPDATE:
				orb.addAllConflicts(operation.getConflicts());
    			break;
			case FAILURE:
				orb.setError(operation.getError());
				break;
			case PARTIAL_GET:
    			for (ObjectData od : operation.getData()) {
    				orb.addData(ProtoBufObjectDataSerializer.serializeObjectData(od));
    			}
    			orb.addAllMissing(operation.getMissing());
				break;
			case PARTIAL_UPDATE:
				orb.addAllConflicts(operation.getConflicts());
    			orb.addAllMissing(operation.getMissing());
    			break;
    		default:
    			// nothing to do
    			break;
		}
		
		return new Message("ObjectResponse", orb.build().toByteArray(), null);
	}
	
	@Override
	public final ObjectResponse deserialize(Message message)
			throws MessageSerializerException {

		try {

			pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse pbObjectResponse =
					pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse.parseFrom(message.getBody());

			ObjectResponse response = ProtoBufObjectResponseSerializer.deserializeObjectResponse(pbObjectResponse);
			
			LOGGER.debug("Deserialized operation: {}", response);
			
			return response;

		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}
}
