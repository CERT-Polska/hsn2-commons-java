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

package pl.nask.hsn2.bus.test.operations.builder;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.operations.JobDescriptor;
import pl.nask.hsn2.bus.operations.builder.JobDescriptorBuilder;

public class JobDescriptorBuilderTest {

	@Test
	public void jdTest() {
		JobDescriptor operation = new JobDescriptorBuilder("workflow-1").build();
		Assert.assertNotNull(operation);
		Assert.assertEquals(operation.getWorkflowId(), "workflow-1");
		Assert.assertNotNull(operation.getServicesConfigs());
		Assert.assertEquals(operation.getServicesConfigs().size(), 0);
		Assert.assertNull(operation.getWorkflowVersion());
	}
	
	@Test
	public void jdWithVersion() {
		JobDescriptor operation = new JobDescriptorBuilder("workflow-1")
				.setWorkflowVersion("1.0").build();
		Assert.assertNotNull(operation.getWorkflowVersion());
		Assert.assertEquals(operation.getWorkflowVersion(), "1.0");
	}
	
	@Test
	public void jdWithServicesConfig() {
		JobDescriptor operation = new JobDescriptorBuilder("workflow-1")
				.setWorkflowVersion("1.0")
				.addServiceConfig("s1", "enabled", "true")
				.addServiceConfig("s1", "opened", "false")
				.build();
		Assert.assertNotNull(operation.getServicesConfigs());
		Assert.assertEquals(operation.getServicesConfigs().size(), 1);
		Assert.assertEquals(operation.getServicesConfigs().get("s1").size(), 2);
		Assert.assertEquals(operation.getServicesConfigs().get("s1").containsKey("enabled"), true);
		Assert.assertEquals(operation.getServicesConfigs().get("s1").get("enabled"), "true");
		Assert.assertEquals(operation.getServicesConfigs().get("s1").containsKey("opened"), true);
		Assert.assertEquals(operation.getServicesConfigs().get("s1").get("opened"), "false");
	}
}
