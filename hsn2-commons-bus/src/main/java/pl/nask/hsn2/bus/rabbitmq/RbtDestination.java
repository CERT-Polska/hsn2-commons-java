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
