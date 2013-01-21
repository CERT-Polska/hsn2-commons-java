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

package pl.nask.hsn2.bus.test.serializer;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.operations.GetConfigRequest;
import pl.nask.hsn2.bus.operations.builder.GetConfigRequestBuilder;
import pl.nask.hsn2.bus.serializer.MessageSerializerException;

public class GetConfigRequestMessageSerializerTest extends AbstractMessageSerializerTest {

	@Test
	@SuppressWarnings("unchecked")
	public void getConfigRequestSerializeTest() throws MessageSerializerException {
		GetConfigRequest operation = new GetConfigRequestBuilder().build();
		
		Message message = serializer.serialize(operation);
		Assert.assertNotNull(message);
		Assert.assertEquals(message.getType(), "GetConfigRequest");
		
		GetConfigRequest deserialized = (GetConfigRequest) serializer.deserialize(message);
		Assert.assertNotNull(deserialized);
	}

}
