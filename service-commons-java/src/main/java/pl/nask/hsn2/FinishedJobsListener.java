package pl.nask.hsn2;

import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.protobuff.Jobs.JobFinished;
import pl.nask.hsn2.protobuff.Jobs.JobFinishedReminder;

public class FinishedJobsListener extends FinishedJobsListenerAbstract {
	private static final Logger LOG = LoggerFactory.getLogger(FinishedJobsListener.class);
	private ConcurrentSkipListSet<Long> knownFinishedJobs = new ConcurrentSkipListSet<>();
	
	@Override
	protected void process(JobFinished data) {
		long jobId = data.getJob();
		knownFinishedJobs.add(jobId);
		LOG.info("Finished job added to list. (id={})", jobId);
	}
	
	@Override
	protected void process(JobFinishedReminder data) {
		long jobId = data.getJob();
		knownFinishedJobs.add(jobId);
		LOG.info("Finished job added to list (reminded). (id={})", jobId);
	}

	public boolean isJobFinished(long jobId) {
		return knownFinishedJobs.contains(jobId);
	}
}
