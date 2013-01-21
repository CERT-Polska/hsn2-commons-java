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

import pl.nask.hsn2.bus.operations.JobInfo;
import pl.nask.hsn2.bus.operations.JobStatus;
import pl.nask.hsn2.bus.operations.builder.JobInfoBuilder;

public class ProtoBufJobInfoSerializer {

	private ProtoBufJobInfoSerializer() {
		// this is utility class
	}
	
	public static pl.nask.hsn2.protobuff.Jobs.JobInfo serializeJobInfo(JobInfo jobInfo) {
		pl.nask.hsn2.protobuff.Jobs.JobInfo.Builder builder = pl.nask.hsn2.protobuff.Jobs.JobInfo.newBuilder();
		return serializeJobInfo(builder, jobInfo);
	}

	public static pl.nask.hsn2.protobuff.Jobs.JobInfo serializeJobInfo(
			pl.nask.hsn2.protobuff.Jobs.JobInfo.Builder builder, JobInfo jobInfo) {
		builder.clear();
		builder.setId(jobInfo.getId());
		builder.setStatus(pl.nask.hsn2.protobuff.Jobs.JobStatus.valueOf(jobInfo.getStatus().name()));
		return builder.build();
	}
	
	public static JobInfo deserializeJobInfo(pl.nask.hsn2.protobuff.Jobs.JobInfo pbJobInfo) {
		JobInfoBuilder builder = new JobInfoBuilder(
				pbJobInfo.getId(),
				JobStatus.valueOf(pbJobInfo.getStatus().name())
		);
		return builder.build();
	}
}
