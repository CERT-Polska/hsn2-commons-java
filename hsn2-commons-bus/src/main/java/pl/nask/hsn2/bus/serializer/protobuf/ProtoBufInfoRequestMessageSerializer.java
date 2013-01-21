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
import pl.nask.hsn2.bus.operations.InfoRequest;
import pl.nask.hsn2.bus.operations.InfoType;
import pl.nask.hsn2.bus.operations.builder.InfoRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>JobRejected</code> operation.
 * 
 *
 */
public class ProtoBufInfoRequestMessageSerializer implements MessageSerializer<InfoRequest> {

	@Override
	public final Message serialize(InfoRequest operation)
			throws MessageSerializerException {

		pl.nask.hsn2.protobuff.Info.InfoRequest.Builder builder = pl.nask.hsn2.protobuff.Info.InfoRequest
				.newBuilder();
		builder.setId(operation.getId());
		builder.setType(pl.nask.hsn2.protobuff.Info.InfoType.valueOf(operation.getType().name()));

		return new Message("InfoRequest", builder.build().toByteArray(), null);
	}

	@Override
	public final InfoRequest deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Info.InfoRequest pbInfoRequest =
					pl.nask.hsn2.protobuff.Info.InfoRequest.parseFrom(message.getBody());
			InfoRequestBuilder irb = new InfoRequestBuilder(InfoType.valueOf(pbInfoRequest.getType().name()));
			irb.setId(pbInfoRequest.getId());
			return irb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
