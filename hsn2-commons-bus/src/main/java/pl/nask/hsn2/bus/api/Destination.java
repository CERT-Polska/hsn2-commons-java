package pl.nask.hsn2.bus.api;

public class Destination {
	protected String service;

	public Destination(String service) {
		this.service = service;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
	
	@Override
	public String toString() {
		return "Destination{service=" + service + "}";
	}
}
