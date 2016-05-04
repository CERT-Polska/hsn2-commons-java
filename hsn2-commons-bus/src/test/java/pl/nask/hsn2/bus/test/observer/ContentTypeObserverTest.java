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

package pl.nask.hsn2.bus.test.observer;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.bus.api.Destination;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.observer.ContentTypeObserver;
import pl.nask.hsn2.bus.api.endpoint.observer.DeliveryObserver;

public class ContentTypeObserverTest {

	@Test
	public void checkDefaultContentTest() {
		Message message = new Message("SomeType", null, new Destination("some destination"));
		message.setContentType("application/hsn2+protobuf"); // default content type
		DeliveryObserver observer = new ContentTypeObserver();
		Assert.assertEquals(observer.observeBefore(message), true);
		
		observer.observeAfter(message); // nothing will do
		observer.observeOnError(message); // nothing will do
	}
	
	@Test
	public void checkNullContentType() {
		Message message = new Message("SomeType", null, new Destination("some destination"));
		message.setContentType(null); // null content type
		DeliveryObserver observer = new ContentTypeObserver();
		Assert.assertEquals(observer.observeBefore(message), false);
	}

	@Test
	public void checkUnmatchedDefaultContentType() {
		Message message = new Message("SomeType", null, new Destination("some destination"));
		message.setContentType(""); // null content type
		DeliveryObserver observer = new ContentTypeObserver();
		Assert.assertEquals(observer.observeBefore(message), false);
	}

	@Test
	public void checkCustomContentType() {
		Message message = new Message("SomeType", null, new Destination("some destination"));
		message.setContentType("custom-content-type"); // custom content type
		ContentTypeObserver observer = new ContentTypeObserver();
		observer.setAcceptableContent("custom-content-type");
		Assert.assertEquals(observer.observeBefore(message), true);

		message.setContentType("sdfsdf"); // unmatched custom content type
		Assert.assertEquals(observer.observeBefore(message), false);
	}
}
