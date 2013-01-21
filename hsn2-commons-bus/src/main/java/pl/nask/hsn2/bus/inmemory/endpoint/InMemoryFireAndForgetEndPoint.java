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

package pl.nask.hsn2.bus.inmemory.endpoint;

import pl.nask.hsn2.bus.api.BusException;
import pl.nask.hsn2.bus.api.Message;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.inmemory.InMemoryBroker;

public class InMemoryFireAndForgetEndPoint implements FireAndForgetEndPoint {

	@Override
	public final boolean isClosed() {
		// this endpoint is always open
		return false;
	}

	@Override
	public final void open() throws BusException {
		// this endpoint is always open
	}

	@Override
	public final void close() throws BusException {
		// this endpoint is always open
	}

	@Override
	public void sendNotify(final Message message) throws BusException {
		try {
			InMemoryBroker.publish(message.getDestination().getService(), message);
		} catch (IllegalStateException ex) {
			throw new BusException("Cannot sent notify.", ex);
		}
	}

	@Override
	public void spread(final Message message, final String[] servicesNames)
			throws BusException {
		try {
			for (String destination : servicesNames) {
				InMemoryBroker.publish(destination, message);
			}
		} catch (IllegalStateException ex) {
			throw new BusException("Cannot spread message.", ex);
		}
	}

}
