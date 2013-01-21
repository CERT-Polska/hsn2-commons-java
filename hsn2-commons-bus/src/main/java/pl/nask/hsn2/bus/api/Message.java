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

package pl.nask.hsn2.bus.api;

import java.util.Arrays;

public class Message {
	private String type;
	private String correlationId;
	private byte[] body;
	private Destination replyTo = new Destination("");
	private Destination destination;
	private int retries = 0;
	private String contentType;

	public Message(String type, byte[] body, Destination destination) {
		this.type = type;
		setBody(body);
		this.destination = destination;
	}

	public Message(String type, byte[] body, String correlationId, Destination replyTo) {
		this.type = type;
		setBody(body);
		this.correlationId = correlationId;
		this.replyTo = replyTo;
	}

	public final String getType() {
		return type;
	}

	public final void setType(String type) {
		this.type = type;
	}

	public final String getCorrelationId() {
		return correlationId;
	}

	public final void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public final byte[] getBody() {
		return body;
	}

	public final void setBody(byte[] body) {
		this.body = body != null ? body.clone() : null;
	}

	public final Destination getReplyTo() {
		return replyTo;
	}

	public final void setReplyTo(Destination replyTo) {
		if (replyTo == null) {
			this.replyTo = new Destination("");
		} else {
			this.replyTo = replyTo;
		}
	}

	public final Destination getDestination() {
		return destination;
	}

	public final void setDestination(Destination destination) {
		this.destination = destination;
	}

	public final void setRetries(int retries) {
		this.retries = retries;
	}

	public final int getRetries() {
		return this.retries;
	}

	public final void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public final String getContentType() {
		return this.contentType;
	}

	@Override
	public final String toString() {
		return new StringBuilder("type: ").append(type)
			.append(", destination: ").append(destination)
			.append(", correlationId: ").append(correlationId)
			.append(", replyTo: ").append(replyTo)
			.append(", retries: ").append(retries)
			.append(", contentType: ").append(contentType)
			.append(", body: ").append(Arrays.toString(body)).toString();
	}
}
