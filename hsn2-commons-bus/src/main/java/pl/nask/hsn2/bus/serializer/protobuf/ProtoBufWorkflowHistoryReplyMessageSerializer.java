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
import pl.nask.hsn2.bus.operations.WorkflowHistoryReply;
import pl.nask.hsn2.bus.operations.WorkflowRevisionInfo;
import pl.nask.hsn2.bus.operations.builder.WorkflowHistoryReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>WorkflowHistoryReply</code> operation.
 * 
 *
 */
public class ProtoBufWorkflowHistoryReplyMessageSerializer implements MessageSerializer<WorkflowHistoryReply> {

	@Override
	public final Message serialize(WorkflowHistoryReply operation)
			throws MessageSerializerException {
		
		pl.nask.hsn2.protobuff.Workflows.WorkflowHistoryReply.Builder whrb = pl.nask.hsn2.protobuff.Workflows.WorkflowHistoryReply
				.newBuilder();

		pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo.Builder wriBuilder =
				pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo.newBuilder();
		for (WorkflowRevisionInfo info : operation.getHistory()) {
			whrb.addHistory(ProtoBufWorkflowRevisionInfoSerializer.serializeWorkflowRevisionInfo(wriBuilder, info));
		}
		
		return new Message("WorkflowHistoryReply", whrb.build().toByteArray(), null);
	}

	@Override
	public final WorkflowHistoryReply deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Workflows.WorkflowHistoryReply pbWorkflowHistoryReply =
					pl.nask.hsn2.protobuff.Workflows.WorkflowHistoryReply.parseFrom(message.getBody());

			WorkflowHistoryReplyBuilder wsrb = new WorkflowHistoryReplyBuilder();
			
			for (pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo pbInfo : pbWorkflowHistoryReply.getHistoryList()) {
				wsrb.addHistoryEntry(ProtoBufWorkflowRevisionInfoSerializer.deserializeWorkflowRevisionInfo(pbInfo));
			}
			
			return wsrb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}
}
