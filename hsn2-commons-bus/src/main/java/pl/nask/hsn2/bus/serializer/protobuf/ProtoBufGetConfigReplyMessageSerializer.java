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

import java.util.Enumeration;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.GetConfigReply;
import pl.nask.hsn2.bus.operations.builder.GetConfigReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>GetConfigReply</code> operation.
 * 
 *
 */
public class ProtoBufGetConfigReplyMessageSerializer implements MessageSerializer<GetConfigReply> {

	@Override
	public final Message serialize(GetConfigReply operation)
			throws MessageSerializerException {

		pl.nask.hsn2.protobuff.Config.GetConfigReply.Builder builder = pl.nask.hsn2.protobuff.Config.GetConfigReply
				.newBuilder();
		
		pl.nask.hsn2.protobuff.Config.Property.Builder propertyBuilder = pl.nask.hsn2.protobuff.Config.Property.newBuilder();
		
		for (Enumeration<?> e = operation.getProperties().propertyNames(); e.hasMoreElements(); ) {
			propertyBuilder.clear();
			String key = (String)e.nextElement();
			propertyBuilder.setName(key);
			propertyBuilder.setValue(operation.getProperties().getProperty(key));
			builder.addProperties(propertyBuilder.build());
		}

		return new Message("GetConfigReply", builder.build().toByteArray(), null);
	}

	@Override
	public final GetConfigReply deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Config.GetConfigReply pbGetConfigReply =
					pl.nask.hsn2.protobuff.Config.GetConfigReply.parseFrom(message.getBody());
			GetConfigReplyBuilder gcr = new GetConfigReplyBuilder();
			if (pbGetConfigReply.getPropertiesCount() > 0) {
				for (pl.nask.hsn2.protobuff.Config.Property p : pbGetConfigReply.getPropertiesList()) {
					gcr.addProperty(p.getName(), p.getValue());
				}
			}
			return gcr.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
