package pl.nask.hsn2.bus.operations;

public class JobCancelReply implements Operation {

	private boolean cancelled;
	private String reason;

	public JobCancelReply(boolean cancelled) {
		this(cancelled, null);
	}
	
	public JobCancelReply(boolean cancelled, String reason) {
		this.cancelled = cancelled;
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	@Override
	public String toString() {
		return "JobCancelReply={cancelled="+ cancelled +", reason="+ reason +"}";
	}
}
