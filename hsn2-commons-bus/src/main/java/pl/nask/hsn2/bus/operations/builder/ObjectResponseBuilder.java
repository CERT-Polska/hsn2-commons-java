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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.bus.operations.ObjectData;
import pl.nask.hsn2.bus.operations.ObjectResponse;

/**
 * This is a builder for <code>ObjectResponse</code> operation.
 * 
 *
 */
public class ObjectResponseBuilder implements OperationBuilder<ObjectResponse> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectResponseBuilder.class);

	/**
	 * Internal variable.
	 */
	private ObjectResponse objectResponse;
	
	public ObjectResponseBuilder(ObjectResponse.ResponseType type) {
		this.objectResponse = new ObjectResponse(type);
	}

	public final ObjectResponseBuilder setData(Set<ObjectData> data) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_GET) {
			if (data == null) {
				this.objectResponse.getData().clear();
			} else {
				this.objectResponse.setData(data);
			}
		} else {
			LOGGER.warn("Data can be set only for SUCCESS_GET type.");
		}
		return this;
	}
	
	public final ObjectResponseBuilder addAllData(Set<ObjectData> data) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_GET) {
			if (data != null) {
				this.objectResponse.getData().addAll(data);
			}
		} else {
			LOGGER.warn("Data can be set only for SUCCESS_GET type.");
		}
		return this;
	}

	public final ObjectResponseBuilder addData(ObjectData data) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_GET
				|| this.objectResponse.getType() == ObjectResponse.ResponseType.PARTIAL_GET) {
			if (data != null) {
				this.objectResponse.getData().add(data);
			}
		} else {
			LOGGER.warn("Data can be set only for SUCCESS_GET/PARTIAL_GET type.");
		}
		return this;
	}

	public final ObjectResponseBuilder setObjects(Set<Long> objects) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_QUERY
				|| this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_PUT) {
			if (objects == null) {
				this.objectResponse.getObjects().clear();
			} else {
				this.objectResponse.setObjects(objects);
			}
		} else {
			LOGGER.warn("Objects can be set only for SUCCESS_QUERY or SUCCESS_PUT type.");
		}
		return this;
	}
	
	public final ObjectResponseBuilder addAllObjects(Collection<Long> objects) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_QUERY
				|| this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_PUT) {
			if (objects != null) {
				this.objectResponse.getObjects().addAll(objects);
			}
		} else {
			LOGGER.warn("Objects can be added only for SUCCESS_QUERY or SUCCESS_PUT type.");
		}
		return this;
	}

	public final ObjectResponseBuilder addObject(long objectId) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_QUERY
				|| this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_PUT) {
			this.objectResponse.getObjects().add(objectId);
		} else {
			LOGGER.warn("Objects can be added only for SUCCESS_QUERY or SUCCESS_PUT type.");
		}
		return this;
	}

	public final ObjectResponseBuilder setMissing(Set<Long> missing) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.PARTIAL_GET
				|| this.objectResponse.getType() == ObjectResponse.ResponseType.PARTIAL_UPDATE) {
			if (missing == null) {
				this.objectResponse.getMissing().clear();
			} else {
				this.objectResponse.setMissing(missing);
			}
		} else {
			LOGGER.warn("Missing can be set only for PARTIAL_GET or PARTIAL_UPDATE type.");
		}
		return this;
	}

	public final ObjectResponseBuilder addAllMissing(Collection<Long> missing) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.PARTIAL_GET
				|| this.objectResponse.getType() == ObjectResponse.ResponseType.PARTIAL_UPDATE) {
			if (missing != null) {
				this.objectResponse.getMissing().addAll(missing);
			}
		} else {
			LOGGER.warn("Missing can be added only for PARTIAL_GET or PARTIAL_UPDATE type.");
		}
		return this;
	}

	public final ObjectResponseBuilder addMissing(long missingId) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.PARTIAL_GET
				|| this.objectResponse.getType() == ObjectResponse.ResponseType.PARTIAL_UPDATE) {
			this.objectResponse.getMissing().add(missingId);
		} else {
			LOGGER.warn("Missing can be added only for PARTIAL_GET or PARTIAL_UPDATE type.");
		}
		return this;
	}

	public final ObjectResponseBuilder setConflicts(Set<Long> conflicts) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_UPDATE
				|| this.objectResponse.getType() == ObjectResponse.ResponseType.PARTIAL_UPDATE) {
			if (conflicts == null) {
				this.objectResponse.getConflicts().clear();
			} else {
				this.objectResponse.setConflicts(conflicts);
			}
		} else {
			LOGGER.warn("Conflicts can be set only for SUCCESS_UPDATE or PARTIAL_UPDATE type.");
		}
		return this;
	}

	public final ObjectResponseBuilder addAllConflicts(Collection<Long> conflicts) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_UPDATE
				|| this.objectResponse.getType() == ObjectResponse.ResponseType.PARTIAL_UPDATE) {
			if (conflicts != null) {
				this.objectResponse.getConflicts().addAll(conflicts);
			}
		} else {
			LOGGER.warn("Conflicts can be added only for SUCCESS_UPDATE or PARTIAL_UPDATE type.");
		}
		return this;
	}

	public final ObjectResponseBuilder addConflict(long conflictId) {
		if (this.objectResponse.getType() == ObjectResponse.ResponseType.SUCCESS_UPDATE
				|| this.objectResponse.getType() == ObjectResponse.ResponseType.PARTIAL_UPDATE) {
			this.objectResponse.getConflicts().add(conflictId);
		} else {
			LOGGER.warn("Conflicts can be added only for SUCCESS_UPDATE or PARTIAL_UPDATE type.");
		}
		return this;
	}

	public final ObjectResponseBuilder setError(String error) {
		this.objectResponse.setError(error);
		return this;
	}

	@Override
	public final ObjectResponse build() {
		return this.objectResponse;
	}
}
