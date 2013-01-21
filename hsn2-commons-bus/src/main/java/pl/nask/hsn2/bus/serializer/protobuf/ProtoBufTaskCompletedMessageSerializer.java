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
import pl.nask.hsn2.bus.operations.TaskCompleted;
import pl.nask.hsn2.bus.operations.builder.TaskCompletedBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>TaskCompleted</code> operation.
 * 
 *
 */
public class ProtoBufTaskCompletedMessageSerializer implements MessageSerializer<TaskCompleted> {

	@Override
	public final Message serialize(TaskCompleted operation)
			throws MessageSerializerException {
		
		pl.nask.hsn2.protobuff.Process.TaskCompleted.Builder tcb = pl.nask.hsn2.protobuff.Process.TaskCompleted.newBuilder()
    	        .setJob(operation.getJobId())
    	        .setTaskId(operation.getTaskId());

		for (String warning : operation.getWarnings()) {
			tcb.addWarnings(warning);
		}
		
		for (Long objId : operation.getObjects()) {
			tcb.addObjects(objId);
		}

		return new Message("TaskCompleted", tcb.build().toByteArray(), null);
	}

	@Override
	public final TaskCompleted deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Process.TaskCompleted pbTaskCompleted =
					pl.nask.hsn2.protobuff.Process.TaskCompleted.parseFrom(message.getBody());
			TaskCompletedBuilder tcb = new TaskCompletedBuilder(
					pbTaskCompleted.getJob(),
					pbTaskCompleted.getTaskId());
			
			if (pbTaskCompleted.getWarningsCount() > 0) {
				for (String warning : pbTaskCompleted.getWarningsList()) {
					tcb.addWarning(warning);
				}
			}
			
			if (pbTaskCompleted.getObjectsCount() > 0) {
				for (Long objId : pbTaskCompleted.getObjectsList()) {
					tcb.addNewObjectId(objId);
				}
			}
			
			return tcb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
