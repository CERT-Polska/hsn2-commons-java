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
import pl.nask.hsn2.bus.operations.JobStarted;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

public class ProtoBufJobStartedMessageSerializer implements MessageSerializer<JobStarted> {

	@Override
	public final Message serialize(JobStarted operation)
			throws MessageSerializerException {
		pl.nask.hsn2.protobuff.Jobs.JobStarted.Builder builder = pl.nask.hsn2.protobuff.Jobs.JobStarted.newBuilder();
		builder.setJob(operation.getJobId());
		return new Message("JobStarted", builder.build().toByteArray(), null);
	}

	@Override
	public final JobStarted deserialize(Message message)
			throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Jobs.JobStarted pbJobStarted = pl.nask.hsn2.protobuff.Jobs.JobStarted.parseFrom(message.getBody());
			return new JobStarted(pbJobStarted.getJob());
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}

}
