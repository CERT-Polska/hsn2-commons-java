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
import pl.nask.hsn2.bus.operations.JobDescriptor;
import pl.nask.hsn2.bus.operations.builder.JobDescriptorBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class JobDescriptorMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void jobDescriptorSerializeTest() throws MessageSerializerException {
		JobDescriptor operation = new JobDescriptorBuilder("workflow-1").build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "JobDescriptor");
		
		JobDescriptor deserialized = (JobDescriptor) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getWorkflowId(), "workflow-1");
		Assert.assertNull(deserialized.getWorkflowVersion());
		Assert.assertNotNull(deserialized.getServicesConfigs());
		Assert.assertEquals(deserialized.getServicesConfigs().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void jobDescriptorSerialize2Test() throws MessageSerializerException {
		JobDescriptor operation = new JobDescriptorBuilder("workflow-1")
			.setWorkflowVersion("1.2")
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "JobDescriptor");
		
		JobDescriptor deserialized = (JobDescriptor) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertEquals(deserialized.getWorkflowId(), "workflow-1");
		Assert.assertEquals(deserialized.getWorkflowVersion(), "1.2");
		Assert.assertNotNull(deserialized.getServicesConfigs());
		Assert.assertEquals(deserialized.getServicesConfigs().size(), 0);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void jobDescriptorSerialize3Test() throws MessageSerializerException {
		JobDescriptor operation = new JobDescriptorBuilder("workflow-1")
			.setWorkflowVersion("1.5")
			.addServiceConfig("S1", "k1", "v1")
			.addServiceConfig("S1", "k2", "v2")
			.addServiceConfig("S1", "k2", "v2")
			.addServiceConfig("S3", "k3", "v3")
			.build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "JobDescriptor");
		
		JobDescriptor deserialized = (JobDescriptor) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
		Assert.assertNotNull(deserialized.getServicesConfigs());
		Assert.assertEquals(deserialized.getServicesConfigs().size(), 2);
		Assert.assertEquals(deserialized.getServicesConfigs().get("S1").size(), 2);
		Assert.assertEquals(deserialized.getServicesConfigs().get("S3").size(), 1);
		Assert.assertEquals(deserialized.getServicesConfigs().get("S1").get("k1"), "v1");
		Assert.assertEquals(deserialized.getServicesConfigs().get("S1").get("k2"), "v2");
		Assert.assertEquals(deserialized.getServicesConfigs().get("S3").get("k3"), "v3");
	}
}
