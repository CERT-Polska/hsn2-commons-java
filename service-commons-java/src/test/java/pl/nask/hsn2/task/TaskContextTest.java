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

package pl.nask.hsn2.task;

import java.util.Collections;
import java.util.List;

import mockit.Expectations;
import mockit.Mocked;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.ServiceConnector;
import pl.nask.hsn2.TaskContext;
import pl.nask.hsn2.bus.operations.AttributeType;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.ObjectResponse.ResponseType;
import pl.nask.hsn2.bus.operations.builder.ObjectResponseBuilder;

public class TaskContextTest {
	@Mocked
	ServiceConnector connector;
	
	/**
	 * Tests for bug 8102 - js-sta cannot get contexts from webclient output
	 */
	@Test
	public void testGetFileAsStream() throws Exception {
		
		TaskContext ctx = new TaskContext(200, 1, 1, connector);
		new Expectations() {
			{
				connector.getDataStoreDataAsStream(200, 100);times=1;
			}
		};
		ctx.getFileAsInputStream(100);		
	}
	
	@Test
	public void testSubcontextsAreSaved() throws Exception {
		TaskContext ctx = new TaskContext(1, 10, 100, connector);
		final ObjectResponseBuilder builder = new ObjectResponseBuilder(ResponseType.SUCCESS_PUT);
		builder.addObject(101L);
		
		new Expectations() {
			{
				connector.saveObjects(1, withInstanceOf(List.class));
				times=1;
				result=Collections.singleton(builder.build());
				
				forEachInvocation = new Object() {
					public void validate(long jobId, List<ObjectData> list) {
						Assert.assertEquals(list.size(), 1, "Number of subcontexts");
						Assert.assertEquals(list.get(0).findAttribute("parent", AttributeType.OBJECT).getObejectRef(), 100, "Parent object ID");
					}
				};
			}
		};
		// create one child
		ctx.openSubContext();
		ctx.closeSubContext();
		
		// flush to check, if the child is saved
		ctx.flush();
	}
}
