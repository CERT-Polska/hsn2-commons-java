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

package pl.nask.hsn2.bus.operations.builder;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.operations.Attribute;
import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.ObjectRequest;
import pl.nask.hsn2.bus.operations.QueryStructure;

/**
 * This is a builder for <code>ObjectRequest</code> operation.
 * 
 *
 */
public class ObjectRequestBuilder implements OperationBuilder<ObjectRequest> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectRequestBuilder.class);

	/**
	 * Internal variable.
	 */
	private ObjectRequest objectRequest;
	
	public ObjectRequestBuilder(ObjectRequest.RequestType type, long jobId) {
		this.objectRequest = new ObjectRequest(type, jobId);
	}

	public final ObjectRequestBuilder addObject(long id) {
		if (objectRequest.getRequestType() != ObjectRequest.RequestType.GET) {
			LOGGER.warn("Objects are allowed only for GET requests. Ignoring...");
		} else {
			objectRequest.getObjects().add(id);
		}
		return this;
	}

	public final ObjectRequestBuilder addAllObjects(Collection<Long> objects) {
		if (objects != null && objects.size() > 0) {
			if (objectRequest.getRequestType() != ObjectRequest.RequestType.GET) {
				LOGGER.warn("Objects are allowed only for GET requests. Ignoring...");
			} else {
				objectRequest.getObjects().addAll(objects);
			}
		}
		return this;
	}

	public final ObjectRequestBuilder addData(ObjectData data) {
		if (objectRequest.getRequestType() != ObjectRequest.RequestType.PUT
				&& objectRequest.getRequestType() != ObjectRequest.RequestType.UPDATE) {
			LOGGER.warn("ObjectData is allowed only for PUT & UPDATE requests. Ignoring...");
		} else {
			objectRequest.getData().add(data);
		}
		return this;
	}

	public final ObjectRequestBuilder addAllData(Collection<? extends ObjectData> data) {
		if (data != null && data.size() > 0) {
			if (objectRequest.getRequestType() != ObjectRequest.RequestType.PUT
					&& objectRequest.getRequestType() != ObjectRequest.RequestType.UPDATE) {
				LOGGER.warn("ObjectData is allowed only for PUT & UPDATE requests. Ignoring...");
			} else {
				objectRequest.getData().addAll(data);
			}
		}
		return this;
	}

	public final ObjectRequestBuilder setTaskId(int taskId) {
		if (objectRequest.getRequestType() != ObjectRequest.RequestType.PUT) {
			LOGGER.warn("TaskId is allowed only for PUT request. Ignoring...");
		} else {
			objectRequest.setTaskId(taskId);
		}
		return this;
	}

	public final ObjectRequestBuilder setOverride(boolean override) {
		if (objectRequest.getRequestType() != ObjectRequest.RequestType.UPDATE) {
			LOGGER.warn("Override flag is allowed only for UPDATE request. Ignoring...");
		} else {
			objectRequest.setOverride(override);
		}
		return this;
	}

	public final ObjectRequestBuilder addByAttributeNameQuery(String name) {
		QueryStructure structure = new QueryStructure(
				QueryStructure.QueryType.BY_ATTR_NAME);
		structure.setAttributeName(name);
		objectRequest.getQuery().add(structure);
		return this;
	}

	public final ObjectRequestBuilder addByAttributeNameQuery(String name, boolean negate) {
		QueryStructure structure = new QueryStructure(
				QueryStructure.QueryType.BY_ATTR_NAME);
		structure.setAttributeName(name);
		structure.setNegate(negate);
		objectRequest.getQuery().add(structure);
		return this;
	}

	public final ObjectRequestBuilder addByAttributeValueQuery(Attribute value) {
		QueryStructure structure = new QueryStructure(
				QueryStructure.QueryType.BY_ATTR_VALUE);
		structure.setValue(value);
		objectRequest.getQuery().add(structure);
		return this;
	}

	public final ObjectRequestBuilder addByAttributeValueQuery(Attribute value, boolean negate) {
		QueryStructure structure = new QueryStructure(
				QueryStructure.QueryType.BY_ATTR_VALUE);
		structure.setValue(value);
		structure.setNegate(negate);
		objectRequest.getQuery().add(structure);
		return this;
	}

	/**
	 * Returns current ObjectRequest instance.
	 */
	@Override
	public final ObjectRequest build() {
		return objectRequest;
	}

}
