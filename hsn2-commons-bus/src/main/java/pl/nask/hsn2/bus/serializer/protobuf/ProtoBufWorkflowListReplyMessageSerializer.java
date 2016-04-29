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
import pl.nask.hsn2.bus.operations.WorkflowBasicInfo;
import pl.nask.hsn2.bus.operations.WorkflowListReply;
import pl.nask.hsn2.bus.operations.builder.WorkflowListReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This is protobuf serializer for <code>WorkflowListReply</code> operation.
 * 
 *
 */
public class ProtoBufWorkflowListReplyMessageSerializer implements MessageSerializer<WorkflowListReply> {

	@Override
	public final Message serialize(WorkflowListReply operation)
			throws MessageSerializerException {
		
		pl.nask.hsn2.protobuff.Workflows.WorkflowListReply.Builder wlrb
			= pl.nask.hsn2.protobuff.Workflows.WorkflowListReply
				.newBuilder();
		
		pl.nask.hsn2.protobuff.Workflows.WorkflowBasicInfo.Builder wbib = pl.nask.hsn2.protobuff.Workflows.WorkflowBasicInfo
				.newBuilder();
		for (WorkflowBasicInfo workflowInfo : operation.getWorkflows()) {
			wlrb.addWorkflows(ProtoBufWorkflowBasicInfoSerializer
					.serializeWorkflowBasicInfo(wbib, workflowInfo));
		}

		return new Message("WorkflowListReply", wlrb.build().toByteArray(), null);
	}

	@Override
	public final WorkflowListReply deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Workflows.WorkflowListReply pbWorkflowListReply = pl.nask.hsn2.protobuff.Workflows.WorkflowListReply
					.parseFrom(message.getBody());
			WorkflowListReplyBuilder wlrb = new WorkflowListReplyBuilder();

			if (pbWorkflowListReply.getWorkflowsCount() > 0) {
				for (pl.nask.hsn2.protobuff.Workflows.WorkflowBasicInfo workflowInfo : pbWorkflowListReply
						.getWorkflowsList()) {
					wlrb.addWorkflowBasicInfo(ProtoBufWorkflowBasicInfoSerializer
							.deserializeWorkflowBasicInfo(workflowInfo));
				}
			}
			return wlrb.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
