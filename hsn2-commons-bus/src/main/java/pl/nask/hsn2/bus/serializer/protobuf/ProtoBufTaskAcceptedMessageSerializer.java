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
import pl.nask.hsn2.bus.operations.TaskAccepted;
import pl.nask.hsn2.bus.operations.builder.TaskAcceptedBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>TaskAccepted</code> operation.
 * 
 *
 */
public class ProtoBufTaskAcceptedMessageSerializer implements MessageSerializer<TaskAccepted> {

	@Override
	public final Message serialize(TaskAccepted operation)
			throws MessageSerializerException {

		pl.nask.hsn2.protobuff.Process.TaskAccepted ta = pl.nask.hsn2.protobuff.Process.TaskAccepted.newBuilder()
				.setJob(operation.getJobId())
				.setTaskId(operation.getTaskId())
				.build();
		
		return new Message("TaskAccepted", ta.toByteArray(), null);
	}

	@Override
	public final TaskAccepted deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Process.TaskAccepted pbTaskAccepted =
					pl.nask.hsn2.protobuff.Process.TaskAccepted.parseFrom(message.getBody());
			return new TaskAcceptedBuilder(pbTaskAccepted.getJob(),
					pbTaskAccepted.getTaskId()).build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
