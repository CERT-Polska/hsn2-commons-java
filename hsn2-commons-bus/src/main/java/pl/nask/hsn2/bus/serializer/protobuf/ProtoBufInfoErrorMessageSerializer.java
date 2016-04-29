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
import pl.nask.hsn2.bus.operations.InfoError;
import pl.nask.hsn2.bus.operations.InfoType;
import pl.nask.hsn2.bus.operations.builder.InfoErrorBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>GetConfigReply</code> operation.
 * 
 *
 */
public class ProtoBufInfoErrorMessageSerializer implements MessageSerializer<InfoError> {

	@Override
	public final Message serialize(InfoError operation)
			throws MessageSerializerException {

		pl.nask.hsn2.protobuff.Info.InfoError.Builder builder = pl.nask.hsn2.protobuff.Info.InfoError
				.newBuilder();
		
		builder.setType(pl.nask.hsn2.protobuff.Info.InfoType.valueOf(operation.getType().name()));
		builder.setReason(operation.getReason());

		return new Message("InfoError", builder.build().toByteArray(), null);
	}

	@Override
	public final InfoError deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Info.InfoError pbInfoError =
					pl.nask.hsn2.protobuff.Info.InfoError.parseFrom(message.getBody());
			
			InfoErrorBuilder ier = new InfoErrorBuilder(
					InfoType.valueOf(pbInfoError.getType().name()),
					pbInfoError.getReason());

			return ier.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}
}
