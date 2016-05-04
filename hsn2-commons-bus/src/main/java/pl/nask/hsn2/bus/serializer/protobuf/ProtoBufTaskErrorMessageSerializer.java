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
import pl.nask.hsn2.bus.operations.TaskError;
import pl.nask.hsn2.bus.operations.TaskErrorReasonType;
import pl.nask.hsn2.bus.operations.builder.TaskErrorBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>TaskError</code> operation.
 * 
 *
 */
public class ProtoBufTaskErrorMessageSerializer implements MessageSerializer<TaskError> {

	@Override
	public final Message serialize(TaskError operation)
			throws MessageSerializerException {
		
		pl.nask.hsn2.protobuff.Process.TaskError.Builder teb = pl.nask.hsn2.protobuff.Process.TaskError.newBuilder()
    	        .setJob(operation.getJobId())
    	        .setTaskId(operation.getTaskId());

		teb.setReason(pl.nask.hsn2.protobuff.Process.TaskError.ReasonType.valueOf(operation.getReason().name()));

		if (operation.getDescription() != null
				&& !"".equals(operation.getDescription())) {
			teb.setDescription(operation.getDescription());
		}
		
		return new Message("TaskError", teb.build().toByteArray(), null);
	}

	@Override
	public final TaskError deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Process.TaskError pbTaskError =
					pl.nask.hsn2.protobuff.Process.TaskError.parseFrom(message.getBody());
			TaskErrorBuilder teb = new TaskErrorBuilder(
					pbTaskError.getJob(),
					pbTaskError.getTaskId(),
					TaskErrorReasonType.valueOf(pbTaskError.getReason().name()));
			if (pbTaskError.getDescription() != null
					&& !"".equals(pbTaskError.getDescription())) {
				teb.setDescription(pbTaskError.getDescription());
			}
			return teb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
