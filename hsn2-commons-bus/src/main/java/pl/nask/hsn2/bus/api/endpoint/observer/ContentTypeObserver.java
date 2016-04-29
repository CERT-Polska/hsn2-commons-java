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

package pl.nask.hsn2.bus.api.endpoint.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.api.Message;

/**
 * This is simple <code>DeliveryObserver</code> implementation
 * for checking if a content has acceptable value. E.g. it can
 * be used for validation if message serialized with valid serializer.
 * 
 *
 */
public class ContentTypeObserver implements DeliveryObserver {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContentTypeObserver.class);
			
	private static final String DEFAULT_CONTENT_TYPE = "application/hsn2+protobuf";

	private String acceptableContent = DEFAULT_CONTENT_TYPE;

	/**
	 * Sets acceptable content.
	 * 
	 * @param acceptableContent Acceptable content.
	 */
	public final void setAcceptableContent(String acceptableContent) {
		this.acceptableContent = acceptableContent;
	}
	
	@Override
	public final boolean observeBefore(Message message) {
		if (acceptableContent.equals(message.getContentType())) {
			return true;
		} else {
			LOGGER.error("Unacceptable content type: {}", message.getContentType());
			return false;
		}
	}

	@Override
	public final void observeAfter(Message message) {
		// nothing to do
	}

	@Override
	public final void observeOnError(Message message) {
		// nothing to do
	}
}
