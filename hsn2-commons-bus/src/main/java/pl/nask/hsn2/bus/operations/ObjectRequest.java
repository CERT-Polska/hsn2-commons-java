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

package pl.nask.hsn2.bus.operations;

import java.util.HashSet;
import java.util.Set;

/**
 * This is operation to be proceed on Object Store.
 * 
 *
 */
public class ObjectRequest implements Operation {

	public enum RequestType {
		GET, PUT, UPDATE, QUERY, PUT_RAW;
	}

	private RequestType requestType;
	private long jobId;
	private Set<Long> objects;
	private Set<ObjectData> data;
	private int taskId;
	private boolean override;
	private Set<QueryStructure> query;

	public ObjectRequest(RequestType requestType, long jobId) {
		this.requestType = requestType;
		this.jobId = jobId;
		this.objects = new HashSet<Long>();
		this.data = new HashSet<ObjectData>();
		this.query = new HashSet<QueryStructure>();
	}
	
	public final RequestType getRequestType() {
		return requestType;
	}

	public final long getJobId() {
		return jobId;
	}

	public final Set<Long> getObjects() {
		return objects;
	}

	public final void setObjects(Set<Long> objects) {
		this.objects = objects;
	}

	public final Set<ObjectData> getData() {
		return data;
	}

	public final void setData(Set<ObjectData> data) {
		this.data = data;
	}

	public final int getTaskId() {
		return taskId;
	}

	public final void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public final boolean isOverride() {
		return override;
	}

	public final void setOverride(boolean override) {
		this.override = override;
	}

	public final Set<QueryStructure> getQuery() {
		return query;
	}

	public final void setQuery(Set<QueryStructure> query) {
		this.query = query;
	}

	@Override
	public String toString() {
		return new StringBuffer().append("ObjectRequest={")
				.append("reqType=").append(requestType).append(", ")
				.append("jobId=").append(jobId).append(", ")
				.append("taskId=").append(taskId).append(", ")
				.append("override=").append(override).append(", ")
				.append("objects=").append(objects).append(", ")
				.append("data=").append(data).append(", ")
				.append("query=").append(query).append("}")
				.toString();
	}
}
