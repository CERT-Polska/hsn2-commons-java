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

import pl.nask.hsn2.bus.operations.WorkflowBasicInfo;
import pl.nask.hsn2.bus.operations.builder.WorkflowBasicInfoBuilder;

public class ProtoBufWorkflowBasicInfoSerializer {

	private ProtoBufWorkflowBasicInfoSerializer() {
		// this is utility class
	}
	
	public static WorkflowBasicInfo deserializeWorkflowBasicInfo(pl.nask.hsn2.protobuff.Workflows.WorkflowBasicInfo pbBasicInfo) {
		WorkflowBasicInfoBuilder wbib = new WorkflowBasicInfoBuilder(
				pbBasicInfo.getName());
		wbib.setEnabled(pbBasicInfo.getEnabled());
		return wbib.build();
	}
	
	public static pl.nask.hsn2.protobuff.Workflows.WorkflowBasicInfo serializeWorkflowBasicInfo(
			WorkflowBasicInfo basicInfo) {
		
		return serializeWorkflowBasicInfo(
				pl.nask.hsn2.protobuff.Workflows.WorkflowBasicInfo.newBuilder(),
				basicInfo);
	}
	
	public static pl.nask.hsn2.protobuff.Workflows.WorkflowBasicInfo serializeWorkflowBasicInfo(
			pl.nask.hsn2.protobuff.Workflows.WorkflowBasicInfo.Builder builder,
			WorkflowBasicInfo basicInfo) {
		builder.clear();
		builder.setEnabled(basicInfo.isEnabled());
		builder.setName(basicInfo.getName());
		return builder.build();
	}
}
