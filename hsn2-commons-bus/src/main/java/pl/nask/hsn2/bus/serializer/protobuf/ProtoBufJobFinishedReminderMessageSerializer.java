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
import pl.nask.hsn2.bus.operations.JobFinishedReminder;
import pl.nask.hsn2.bus.serializer.MessageSerializer;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

import com.google.protobuf.InvalidProtocolBufferException;

public class ProtoBufJobFinishedReminderMessageSerializer implements MessageSerializer<JobFinishedReminder> {

	@Override
	public final Message serialize(JobFinishedReminder operation)
			throws MessageSerializerException {
		pl.nask.hsn2.protobuff.Jobs.JobFinishedReminder.Builder builder = pl.nask.hsn2.protobuff.Jobs.JobFinishedReminder.newBuilder();
		builder.setJob(operation.getJobId());
		builder.setOffendingTask(operation.getOffendingTask());
		if (operation.getStatus() != null) {
			builder.setStatus(pl.nask.hsn2.protobuff.Jobs.JobStatus.valueOf(operation.getStatus().name()));
		}
		return new Message("JobFinishedReminder", builder.build().toByteArray(), null);
	}

	@Override
	public final JobFinishedReminder deserialize(Message message) throws MessageSerializerException {
		try {
			pl.nask.hsn2.protobuff.Jobs.JobFinishedReminder pbJobFinishedReminder = pl.nask.hsn2.protobuff.Jobs.JobFinishedReminder
					.parseFrom(message.getBody());
			JobFinishedReminder jobFinishedReminder = new JobFinishedReminder(pbJobFinishedReminder.getJob());
			// TODO protobuf definition should be fixed, and then remove int cast below.
			// Offending task is defined as uint64 (long in java), but it should be fixed32 (int in java).
			jobFinishedReminder.setOffendingTask((int) pbJobFinishedReminder.getOffendingTask());
			return jobFinishedReminder;
		} catch (InvalidProtocolBufferException e) {
			throw new MessageSerializerException(e);
		}
	}
}
