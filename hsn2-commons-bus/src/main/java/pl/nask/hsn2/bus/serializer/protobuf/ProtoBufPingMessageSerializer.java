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
import pl.nask.hsn2.bus.operations.Ping;
import pl.nask.hsn2.bus.operations.builder.PingBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>Ping</code> operation.
 * 
 *
 */
public class ProtoBufPingMessageSerializer implements MessageSerializer<Ping> {

	@Override
	public final Message serialize(Ping operation) throws MessageSerializerException {
		pl.nask.hsn2.protobuff.Info.Ping.Builder builder = pl.nask.hsn2.protobuff.Info.Ping.newBuilder();
		if (operation.isResponse()) {
			return new Message("Ping", "pong".getBytes(), null);
		} else {
			return new Message("Ping", builder.build().toByteArray(), null);
		}
	}

	@Override
	public final Ping deserialize(Message message) throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Info.Ping.parseFrom(message.getBody());
			PingBuilder pb = new PingBuilder();
			return pb.build();
		} catch (InvalidProtocolBufferException e) {
			if ("pong".equals(new String(message.getBody()))) {
				return new Ping(true);
			}
			throw new MessageSerializerException(e);
		}
	}

}
