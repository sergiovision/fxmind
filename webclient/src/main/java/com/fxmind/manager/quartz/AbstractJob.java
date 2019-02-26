package com.fxmind.manager.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

/**
 *  @author Sergei Zhuravlev
*/
public class AbstractJob implements Job
{
	private JobExecutionContext jobContext;

	public void execute(final JobExecutionContext jobexecutioncontext) throws JobExecutionException
	{
		jobContext = jobexecutioncontext;
	}

	protected Scheduler getScheduler() 
	{
		return getJobContext().getScheduler();
	}

	/**
	 * Returns the jobContext associated with the job. It can't be null otherwise an IllegalStateException is raised
	 * 
	 * @return the job context
	 * @throws IllegalStateException if the job context is null
	 */
	public JobExecutionContext getJobContext() {
		if (jobContext == null)
			throw new IllegalStateException("method: getJobContext: the job context is not yet initialized.");
		return jobContext;
	}

	/**
	 * Helper method to set a log message displayed through the web UI when the job is running. <p>
	 * It's used also when the job ends up to display and send a log message by email.
	 * 
	 * @param message
	 */
	public void setResultMessage(final String message) 
	{
		getJobContext().setResult(message);
	}
}