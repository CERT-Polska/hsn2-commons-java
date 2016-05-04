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
import pl.nask.hsn2.bus.operations.InfoData;
import pl.nask.hsn2.bus.operations.InfoType;
import pl.nask.hsn2.bus.operations.builder.InfoDataBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>GetConfigReply</code> operation.
 * 
 *
 */
public class ProtoBufInfoDataMessageSerializer implements MessageSerializer<InfoData> {

	@Override
	public final Message serialize(InfoData operation)
			throws MessageSerializerException {

		pl.nask.hsn2.protobuff.Info.InfoData.Builder builder = pl.nask.hsn2.protobuff.Info.InfoData
				.newBuilder();
		
		builder.setType(pl.nask.hsn2.protobuff.Info.InfoType.valueOf(operation.getType().name()));
		builder.setData(ProtoBufObjectDataSerializer.serializeObjectData(operation.getData()));

		return new Message("InfoData", builder.build().toByteArray(), null);
	}

	@Override
	public final InfoData deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Info.InfoData pbInfoData =
					pl.nask.hsn2.protobuff.Info.InfoData.parseFrom(message.getBody());
			
			InfoDataBuilder gcr = new InfoDataBuilder(
					InfoType.valueOf(pbInfoData.getType().name()),
					ProtoBufObjectDataSerializer.deserializeObjectData(pbInfoData.getData()));

			return gcr.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}
}
