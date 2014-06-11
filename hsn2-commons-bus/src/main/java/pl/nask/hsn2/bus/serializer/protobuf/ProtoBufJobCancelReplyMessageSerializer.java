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

import com.google.protobuf.InvalidProtocolBufferException;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.JobCancelReply;
import pl.nask.hsn2.bus.operations.builder.JobCancelReplyBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class ProtoBufJobCancelReplyMessageSerializer implements MessageSerializer<JobCancelReply> {

	@Override
	public Message serialize(JobCancelReply operation) throws MessageSerializerException {
		pl.nask.hsn2.protobuff.Jobs.JobCancelReply.Builder builder = pl.nask.hsn2.protobuff.Jobs.JobCancelReply.newBuilder();
		builder.setCancelled(true);
		return new Message("JobCancelReply", builder.build().toByteArray(), null);
	}

	@Override
	public JobCancelReply deserialize(Message message) throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Jobs.JobCancelReply cancelReply = pl.nask.hsn2.protobuff.Jobs.JobCancelReply.parseFrom(message.getBody());
			JobCancelReplyBuilder builder = new JobCancelReplyBuilder(cancelReply.getCancelled());
			return builder.build();
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
		
	}
}
