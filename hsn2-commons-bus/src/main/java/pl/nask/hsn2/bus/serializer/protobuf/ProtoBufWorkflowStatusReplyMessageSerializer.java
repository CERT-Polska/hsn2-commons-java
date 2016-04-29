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
import pl.nask.hsn2.bus.operations.WorkflowRevisionInfo;
import pl.nask.hsn2.bus.operations.WorkflowStatusReply;
import pl.nask.hsn2.bus.operations.builder.WorkflowStatusReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>WorkflowStatusRequest</code> operation.
 * 
 *
 */
public class ProtoBufWorkflowStatusReplyMessageSerializer implements MessageSerializer<WorkflowStatusReply> {

	@Override
	public final Message serialize(WorkflowStatusReply operation)
			throws MessageSerializerException {
		
		pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo info =
		pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo
			.newBuilder().setRevision(operation.getInfo().getRevision())
			.setMtime(operation.getInfo().getMtime()).build();
		
		pl.nask.hsn2.protobuff.Workflows.WorkflowStatusReply.Builder wsrb = pl.nask.hsn2.protobuff.Workflows.WorkflowStatusReply
				.newBuilder()
				.setValid(operation.isValid())
				.setEnabled(operation.isEnabled())
				.setInfo(info);
		if (operation.getDescription() != null) {
			wsrb.setDescription(operation.getDescription());
		}

		return new Message("WorkflowStatusReply", wsrb.build().toByteArray(), null);
	}

	@Override
	public final WorkflowStatusReply deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Workflows.WorkflowStatusReply pbWorkflowStatusReply =
					pl.nask.hsn2.protobuff.Workflows.WorkflowStatusReply.parseFrom(message.getBody());

			pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo info = pbWorkflowStatusReply.getInfo();
	
			WorkflowStatusReplyBuilder wsrb = new WorkflowStatusReplyBuilder(
					pbWorkflowStatusReply.getValid(),
					pbWorkflowStatusReply.getEnabled(),
					new WorkflowRevisionInfo(info.getRevision(), info.getMtime()));

			if (pbWorkflowStatusReply.hasDescription()) {
				wsrb.setDescription(pbWorkflowStatusReply.getDescription());
			}
			
			return wsrb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}
}
