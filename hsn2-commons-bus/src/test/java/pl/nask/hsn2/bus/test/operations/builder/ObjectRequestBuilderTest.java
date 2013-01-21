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

package pl.nask.hsn2.bus.test.operations.builder;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.ObjectRequest;
import pl.nask.hsn2.bus.operations.QueryStructure;
import pl.nask.hsn2.bus.operations.builder.ObjectRequestBuilder;

public class ObjectRequestBuilderTest {

	@Test
	public void simpleGETTest() {
		ObjectRequest operation = new ObjectRequestBuilder(ObjectRequest.RequestType.GET, 1L)
			.setOverride(true).setTaskId(1)
			.addObject(2L).addObject(3L).addObject(4L)
			.build();
		Assert.assertNotNull(operation);
		Assert.assertEquals(operation.getRequestType(), ObjectRequest.RequestType.GET);
		Assert.assertEquals(operation.getJobId(), 1L);
		Assert.assertEquals(operation.isOverride(), false);
		Assert.assertEquals(operation.getTaskId(), 0);
		Assert.assertEquals(operation.getObjects().size(), 3);
		Assert.assertTrue(operation.getObjects().contains(2L));
		Assert.assertTrue(operation.getObjects().contains(3L));
		Assert.assertTrue(operation.getObjects().contains(4L));
	}
	
	@Test
	public void simplePUTTest() {
		ObjectData od = new ObjectData();
		od.setId(3L);
		ObjectRequest operation = new ObjectRequestBuilder(ObjectRequest.RequestType.PUT, 5L)
		.setTaskId(7).addData(od).setOverride(true)
		.build();
		Assert.assertNotNull(operation);
		Assert.assertEquals(operation.getRequestType(), ObjectRequest.RequestType.PUT);
		Assert.assertEquals(operation.getJobId(), 5L);
		Assert.assertEquals(operation.getTaskId(), 7);
		Assert.assertEquals(operation.isOverride(), false); // for UPDATE only
		Assert.assertEquals(operation.getData().size(), 1);
		Assert.assertTrue(operation.getData().contains(od));
	}

	@Test
	public void simpleUPDATETest() {
		ObjectData od = new ObjectData();
		od.setId(3L);
		ObjectRequest operation = new ObjectRequestBuilder(ObjectRequest.RequestType.UPDATE, 4L)
		.setTaskId(7).setOverride(true).addData(od)
		.build();
		Assert.assertNotNull(operation);
		Assert.assertEquals(operation.getRequestType(), ObjectRequest.RequestType.UPDATE);
		Assert.assertEquals(operation.getJobId(), 4L);
		Assert.assertEquals(operation.getTaskId(), 0); // for PUT only
		Assert.assertEquals(operation.isOverride(), true);
		Assert.assertEquals(operation.getData().size(), 1);
		Assert.assertTrue(operation.getData().contains(od));
	}

	@Test
	public void simpleQUERYTest() {
		ObjectData od = new ObjectData();
		od.setId(3L);
		ObjectRequest operation = new ObjectRequestBuilder(ObjectRequest.RequestType.QUERY, 3L)
		.setTaskId(7).setOverride(true).addData(od)
		.addByAttributeNameQuery("someAttrname")
		.build();
		Assert.assertNotNull(operation);
		Assert.assertEquals(operation.getRequestType(), ObjectRequest.RequestType.QUERY);
		Assert.assertEquals(operation.getJobId(), 3L);
		Assert.assertEquals(operation.getTaskId(), 0); // for PUT only
		Assert.assertEquals(operation.isOverride(), false); // for UPDATE only
		Assert.assertEquals(operation.getData().size(), 0); // for PUT & UPDATE only
		Assert.assertEquals(operation.getQuery().size(), 1);
		Object obj = operation.getQuery().toArray()[0];
		Assert.assertTrue(obj instanceof QueryStructure);
		QueryStructure qs = (QueryStructure) obj;
		Assert.assertEquals(qs.getType(), QueryStructure.QueryType.BY_ATTR_NAME);
		Assert.assertEquals(qs.getAttributeName(), "someAttrname");
	}
}
