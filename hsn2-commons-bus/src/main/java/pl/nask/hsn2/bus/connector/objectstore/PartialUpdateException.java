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

package pl.nask.hsn2.bus.connector.objectstore;

import java.util.HashSet;
import java.util.Set;

public class PartialUpdateException extends ObjectStoreConnectorException {

	private static final long serialVersionUID = -819647347425619043L;

	private Set<Long> conflicts;
	
	public PartialUpdateException(String error, Set<Long> conflicts) {
		super(error);
		this.conflicts = conflicts;
	}
	
	public final Set<Long> getConflicts() {
		if (this.conflicts == null) {
			return new HashSet<Long>();
		}
		return this.conflicts;
	}
}
