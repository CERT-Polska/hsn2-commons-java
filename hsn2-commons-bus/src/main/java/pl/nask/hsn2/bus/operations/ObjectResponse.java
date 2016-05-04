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

package pl.nask.hsn2.bus.operations;

import java.util.HashSet;
import java.util.Set;

/**
 * This is response operation got from ObjectStore on <code>ObjectRequest</code> operation.
 * 
 *
 */
public class ObjectResponse implements Operation {

	public enum ResponseType {
		SUCCESS_GET, SUCCESS_PUT, SUCCESS_UPDATE, SUCCESS_QUERY,
		FAILURE, PARTIAL_GET, PARTIAL_UPDATE;
	}

	private ResponseType type;
	private Set<ObjectData> data;
	private Set<Long> objects;
	private Set<Long> missing;
	private Set<Long> conflicts;
	private String error;
	
	public ObjectResponse(ResponseType type) {
		this.type = type;
		this.data = new HashSet<ObjectData>();
		this.objects = new HashSet<Long>();
		this.missing = new HashSet<Long>();
		this.conflicts = new HashSet<Long>();
	}
	
	public final Set<ObjectData> getData() {
		return data;
	}

	public final ObjectData getData(int index) {
		if (data == null || data.size() <= index) {
			throw new IndexOutOfBoundsException();
		}
		return (ObjectData) data.toArray()[index];
	}
	
	public final void setData(Set<ObjectData> data) {
		this.data = data;
	}

	public final Set<Long> getObjects() {
		return objects;
	}

	public final void setObjects(Set<Long> objects) {
		this.objects = objects;
	}

	public final Set<Long> getMissing() {
		return missing;
	}

	public final void setMissing(Set<Long> missing) {
		this.missing = missing;
	}

	public final Set<Long> getConflicts() {
		return conflicts;
	}

	public final void setConflicts(Set<Long> conflicts) {
		this.conflicts = conflicts;
	}

	public final String getError() {
		return error;
	}

	public final void setError(String error) {
		this.error = error;
	}

	public final ResponseType getType() {
		return type;
	}
	
	@Override
	public final String toString() {
		return new StringBuffer(512).append("ObjectResponse={")
			.append("type=").append(type).append(",")
			.append("data=[").append(data).append("],")
			.append("objects=[").append(objects).append("],")
			.append("missing=[").append(missing).append("],")
			.append("conflicts=[").append(conflicts).append("],")
			.append("error=").append(error).append("}").toString();
	}
}
