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

import pl.nask.hsn2.bus.operations.WorkflowRevisionInfo;

public class ProtoBufWorkflowRevisionInfoSerializer {

	private ProtoBufWorkflowRevisionInfoSerializer() {
		// this is utility class
	}
	
	public static pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo serializeWorkflowRevisionInfo(
			WorkflowRevisionInfo workflowRevisionInfo) {
		pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo.Builder builder = pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo
				.newBuilder();
		return serializeWorkflowRevisionInfo(builder, workflowRevisionInfo);
	}

	public static pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo serializeWorkflowRevisionInfo(
			pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo.Builder builder,
			WorkflowRevisionInfo workflowRevisionInfo) {
		return builder.clear().setRevision(workflowRevisionInfo.getRevision())
				.setMtime(workflowRevisionInfo.getMtime()).build();
	}
	
	public static WorkflowRevisionInfo deserializeWorkflowRevisionInfo(
			pl.nask.hsn2.protobuff.Workflows.WorkflowRevisionInfo pbWorkflowRevisionInfo) {
		return new WorkflowRevisionInfo(pbWorkflowRevisionInfo.getRevision(),
				pbWorkflowRevisionInfo.getMtime());
	}
}
