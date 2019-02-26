package com.fxmind.manager.quartz;

import com.fxmind.quartz.JobDescription;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public abstract class Job extends AbstractJob implements InterruptableJob
{
    protected static final Logger log = LoggerFactory.getLogger(Job.class);

    public static final String ENTERPRISE_OBJECT_KEY = "eoJobKey";
	public static final String NOT_PERSISTENT_OBJECT_KEY = "jobKey";

	private JobDescription jobDescription;
	private boolean jobInterrupted = false;

	/**
	 * Implementation of Job interface.
	 * Called by the Scheduler when a Trigger associated with the job is fired.<br>
	 * getJobContext() returns the jobContext after execute() is called.<p>
	 * To be sure that any exception will be catched, _execute() call in surround by a try/catch block. 
	 * 
	 * @param jobexecutioncontext passed by the scheduler
	 * @see <a href="http://quartz-scheduler.org/documentation/best-practices">http://quartz-scheduler.org/documentation/best-practices</a>
	 */
	@Override
	public final void execute(final JobExecutionContext jobexecutioncontext) throws JobExecutionException
	{
		super.execute(jobexecutioncontext);

        try
		{
			_execute();
		} catch (Exception e)
		{
			log.error("method: execute: " + e.getMessage(), e);
		}	
	}

	/**
	 * _execute() is called by execute(). Put your code here and everything will be set up for you.
	 * 
	 * @throws JobExecutionException 
	 * 
	 */
	protected abstract void _execute() throws JobExecutionException;
	
	/**
	 * It's a good place to put code that will be executed before job description deletion.<p>
	 * Nothing is done automatically, you have to call this method manually if you want to give a chance to the job to
	 * use its own logic.
	 * 
	 * @param aJobDescription
	 */
	public abstract void willDelete(JobDescription aJobDescription);

	/**
	 * It's a good place to put code that will be executed before job description save.<p>
	 * Nothing is done automatically, you have to call this method manually if you want to give a chance to the job to
	 * use its own logic.
	 * 
	 * @param aJobDescription
	 */
	public abstract void willSave(JobDescription aJobDescription);

	/**
	 * It's a good place to put code that will check if the job description can be saved or not.<p>
	 * Nothing is done automatically, you have to call this method manually if you want to give a chance to the job to
	 * use its own logic.
	 * 
	 * @param aJobDescription
	 */
	public abstract void validateForSave(JobDescription aJobDescription);

	/**
	 * It's a good place to put code that will check if the job description can be deleted or not.<p>
	 * Nothing is done automatically, you have to call this method manually if you want to give a chance to the job to
	 * use its own logic.
	 * 
	 * @param aJobDescription
	 */
	public abstract void validateForDelete(JobDescription aJobDescription);

	/**
	 * Send back the JobDescription Object attach to the job.
	 *
	 * @return the NOScheduler Object.
	 * @throws IllegalStateException if there is no EOGlobalID in the JobDataMap or if ec is null and JobDescription object isn't already defaulted
	 */
	public JobDescription getJobDescription()
	{
		JobDescription aJobDescription;
		
		if (jobDescription == null)
		{
			JobExecutionContext context = getJobContext();
			
			if (context.getMergedJobDataMap() != null)
			{
			    jobDescription = (JobDescription) context.getMergedJobDataMap().get(NOT_PERSISTENT_OBJECT_KEY);

				if (jobDescription == null)
					throw new IllegalStateException("method: getJobDescription: unknown jobDescription.");
			}
			else
			{
				throw new IllegalStateException("method: getJobDescription: no job detail or job data map. The jobDescription is still null.");
			}
			aJobDescription = jobDescription;
		}
		else
		{
			aJobDescription = jobDescription;
		}
		return aJobDescription;
	}

	public Timestamp getLastExecutionDate()
	{
		return getJobDescription().lastExecutionDate();
	}
	
    /**
     * Called by the <code>{@link Scheduler}</code> when a user interrupts the <code>Job</code>.
     * return void (nothing) if job interrupt is successful.
     *
     * @throws JobExecutionException
     *           if there is an exception while interrupting the job.
     */
    public void interrupt() throws UnableToInterruptJobException 
    {
        log.info("method: interrupt has been called for the job: " + getJobDescription());
        jobInterrupted = true;
    }
    
    protected boolean isJobInterrupted()
    {
    	return jobInterrupted;
    }
}
