package pl.nask.hsn2.bus.operations;

public class JobCancelRequest implements Operation {
	
	private long id;
	
	/**
	 * Constructor with identifier.
	 * @param id Identifier
	 */
	public JobCancelRequest(long id) {
		this.id = id;
	}
	
	/**
	 * Gets additional identifier.
	 * 
	 * @return Identifier.
	 */
	public final long getId() {
		return id;
	}

	/**
	 * Sets additional identifier.
	 * 
	 * @param id Identifier.
	 */
	public final void setId(long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "CancelJobRequest={id="+ id +"}";
	}
}
