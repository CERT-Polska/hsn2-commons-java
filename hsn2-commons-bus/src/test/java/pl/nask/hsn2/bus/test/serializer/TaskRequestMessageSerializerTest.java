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

package pl.nask.hsn2.bus.test.serializer;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.TaskRequest;
import pl.nask.hsn2.bus.operations.builder.TaskRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class TaskRequestMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void taskRequestSerializeNoParamsTest() throws MessageSerializerException {
		TaskRequest operation = new TaskRequestBuilder(5, 6, 9).build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "TaskRequest");
		
		TaskRequest deserialized = (TaskRequest) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getJob(), 5);
		Assert.assertEquals(deserialized.getTaskId(), 6);
		Assert.assertEquals(deserialized.getObject(), 9);
		Assert.assertNotNull(deserialized.getParameters());
		Assert.assertEquals(deserialized.getParameters().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void taskRequestSerializeWithParamsTest() throws MessageSerializerException {
		TaskRequest operation = new TaskRequestBuilder(5, 6, 9)
			.addParameter("p1", "ttt")
			.addParameter("p2", "ssss")
			.addParameter("p2", "uuu")
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "TaskRequest");
		
		TaskRequest deserialized = (TaskRequest) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getJob(), 5);
		Assert.assertEquals(deserialized.getTaskId(), 6);
		Assert.assertEquals(deserialized.getObject(), 9);
		Assert.assertNotNull(deserialized.getParameters());
		Assert.assertEquals(deserialized.getParameters().size(), 2);
		Assert.assertEquals(deserialized.getParameters().get("p1"), "ttt");
		Assert.assertEquals(deserialized.getParameters().get("p2"), "uuu");
	}

}
