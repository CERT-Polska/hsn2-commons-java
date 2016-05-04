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

package pl.nask.hsn2.bus.serializer.protobuf;

import pl.nask.hsn2.bus.operations.ObjectResponse;
import pl.nask.hsn2.bus.operations.builder.ObjectResponseBuilder;

public abstract class ProtoBufObjectResponseSerializer {

	private ProtoBufObjectResponseSerializer() {
		// utility class
	}

	public static ObjectResponse deserializeObjectResponse(pl.nask.hsn2.protobuff.ObjectStore.ObjectResponse pbObjectResponse) {

		ObjectResponseBuilder orb = new ObjectResponseBuilder(
				ObjectResponse.ResponseType.valueOf(pbObjectResponse.getType()
						.name()));
		switch(pbObjectResponse.getType()) {
		case SUCCESS_GET:
			for (pl.nask.hsn2.protobuff.Object.ObjectData od : pbObjectResponse.getDataList()) {
				orb.addData(ProtoBufObjectDataSerializer.deserializeObjectData(od));
			}
			break;
		case SUCCESS_PUT:
		case SUCCESS_QUERY:
			orb.addAllObjects(pbObjectResponse.getObjectsList());
			break;
		case SUCCESS_UPDATE:
			orb.addAllConflicts(pbObjectResponse.getConflictsList());
			break;
		case FAILURE:
			orb.setError(pbObjectResponse.getError());
			break;
		case PARTIAL_GET:
			for (pl.nask.hsn2.protobuff.Object.ObjectData od : pbObjectResponse.getDataList()) {
				orb.addData(ProtoBufObjectDataSerializer.deserializeObjectData(od));
			}
			orb.addAllMissing(pbObjectResponse.getMissingList());
			break;
		case PARTIAL_UPDATE:
			orb.addAllConflicts(pbObjectResponse.getConflictsList());
			orb.addAllMissing(pbObjectResponse.getMissingList());
			break;
		default:
			// nothing to do
				break;
		}

		ObjectResponse response = orb.build();						
		return response;
	}
}
