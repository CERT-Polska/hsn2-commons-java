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
import pl.nask.hsn2.bus.operations.WorkflowUploadRequest;
import pl.nask.hsn2.bus.operations.builder.WorkflowUploadRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class WorkflowUploadRequestMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void workflowUploadRequestSerializeTest() throws MessageSerializerException {
		WorkflowUploadRequest operation = new WorkflowUploadRequestBuilder("someName", "someContent", true).build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "WorkflowUploadRequest");
		
		WorkflowUploadRequest deserialized = (WorkflowUploadRequest) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getName(), "someName");
		Assert.assertEquals(deserialized.getContent(), "someContent");
		Assert.assertEquals(deserialized.isOverride(), true);
	}
}
