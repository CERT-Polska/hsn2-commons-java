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

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.GetConfigRequest;
import pl.nask.hsn2.bus.operations.builder.GetConfigRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>GetConfigRequest</code> operation.
 * 
 *
 */
public class ProtoBufGetConfigRequestMessageSerializer implements MessageSerializer<GetConfigRequest> {

	@Override
	public final Message serialize(GetConfigRequest operation)
			throws MessageSerializerException {

		pl.nask.hsn2.protobuff.Config.GetConfigRequest.Builder builder = pl.nask.hsn2.protobuff.Config.GetConfigRequest
				.newBuilder();

		return new Message("GetConfigRequest", builder.build().toByteArray(), null);
	}

	@Override
	public final GetConfigRequest deserialize(Message message)
			throws MessageSerializerException {
		try {

// this is for future use if GetConfig will have any additional parameters.
//			pl.nask.hsn2.protobuff.Config.GetConfigRequest pbGetConfigRequest =
// currently this is used only for check if messages is correctly serialized
			pl.nask.hsn2.protobuff.Config.GetConfigRequest.parseFrom(message.getBody());

			GetConfigRequestBuilder gcr = new GetConfigRequestBuilder();
			return gcr.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
