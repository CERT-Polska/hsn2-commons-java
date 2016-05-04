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

package pl.nask.hsn2.bus.rabbitmq;

import pl.nask.hsn2.bus.api.Destination;

public class RbtDestination extends Destination {
	private String exchange;

	public RbtDestination(String destinationService) {
		super(destinationService);
	}

	public RbtDestination(String destinationExchange, String destinationService) {
		super(destinationService);
		exchange = destinationExchange;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Destination{exchange=").append(exchange).append(",service=").append(service).append("}");
		return sb.toString();
	}
}
