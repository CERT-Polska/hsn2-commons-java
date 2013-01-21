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
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPoint;
import pl.nask.hsn2.bus.api.endpoint.ConsumeEndPointHandler;
import pl.nask.hsn2.bus.api.endpoint.EndPointFactory;
import pl.nask.hsn2.bus.api.endpoint.FireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.MulticastEndPoint;
import pl.nask.hsn2.bus.api.endpoint.RequestResponseEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledFireAndForgetEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledMulticastEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.PooledRequestResponseEndPoint;
import pl.nask.hsn2.bus.api.endpoint.pooled.ThreadLocalRequestResponseEndPoint;

public class InMemoryEndPointFactory implements EndPointFactory {

	private static IllegalStateException ERROR_EXCEPTION = new IllegalStateException("Someting goes wrong.");
	
	@Override
	public final ConsumeEndPoint createConsumeEndPoint(
			ConsumeEndPointHandler messagesHandler, String routingKey)
			throws BusException {
		return this.createConsumeEndPoint(messagesHandler, routingKey, true);
	}

	@Override
	public ConsumeEndPoint createConsumeEndPoint(
			ConsumeEndPointHandler messagesHandler, String routingKey,
			boolean autoack) throws BusException {
		return new InMemoryConsumeEndPoint(routingKey, messagesHandler);
	}

	@Override
	public RequestResponseEndPoint createRequestResponseEndPoint()
			throws BusException {
		return createRequestResponseEndPoint(1);
	}

	@Override
	public RequestResponseEndPoint createRequestResponseEndPoint(int maxThreads)
			throws BusException {
		
		final EndPointFactory thiz = this;
		
		if (maxThreads > 1) {
			return new PooledRequestResponseEndPoint(maxThreads,
					new PooledRequestResponseEndPoint.PooledRREPCreateCallback(){
						@Override
						public RequestResponseEndPoint create() {
							try {
								return thiz.createRequestResponseEndPoint(1);
							} catch (BusException e) {
								throw ERROR_EXCEPTION;
							}
						}
			});
		} else {
			return new InMemoryRequestResponseEndPoint();
		}
	}

	@Override
	public FireAndForgetEndPoint createFireAndForgetEndPoint()
			throws BusException {
		return this.createNotificationEndPoint(1);
	}

	@Override
	public FireAndForgetEndPoint createNotificationEndPoint(int maxThreads)
			throws BusException {

		final EndPointFactory thiz = this;
		
		if (maxThreads > 1) {
			return new PooledFireAndForgetEndPoint(maxThreads,
					new PooledFireAndForgetEndPoint.PooledNEPCreateCallback(){
						@Override
						public FireAndForgetEndPoint create() {
							try {
								return thiz.createNotificationEndPoint(1);
							} catch (BusException e) {
								throw ERROR_EXCEPTION;
							}
						}
			});
		} else {
			return new InMemoryFireAndForgetEndPoint();
		}
	}

	@Override
	public MulticastEndPoint createMulticastEndPoint() throws BusException {
		return this.createMulticastEndPoint(1);
	}

	@Override
	public MulticastEndPoint createMulticastEndPoint(int maxThreads)
			throws BusException {
		
		final EndPointFactory thiz = this;
		
		if (maxThreads > 1) {
			return new PooledMulticastEndPoint(maxThreads,
					new PooledMulticastEndPoint.PooledMulticastEPCreateCallback(){
						@Override
						public MulticastEndPoint create() {
							try {
								return thiz.createMulticastEndPoint(1);
							} catch (BusException e) {
								throw ERROR_EXCEPTION;
							}
						}
			});
		} else {
			return new InMemoryMulticastEndPoint();
		}
	}
	
	@Override
	public RequestResponseEndPoint createThreadSafeRequestResponseEndPoint()
			throws BusException {	
		return new ThreadLocalRequestResponseEndPoint(this);
	}

}
