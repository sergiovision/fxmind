package com.fxmind.manager.quartz;

import com.fxmind.entity.Jobs;
import com.fxmind.quartz.JobDescription;
import com.fxmind.service.AdminService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The job listener is called automatically before a job is executed and after it has been executed.<p>
 * When a job is candidate to be executed, the job listener posts a notification JOB_WILL_RUN through the NSNotificationCenter.
 * If you want to be notified, subscribe to the JOB_WILL_RUN notification name and read the notification userInfo to know which job
 * will be executed.<p>
 * When a job has been executed, the job listener posts a notification JOB_RAN through the NSNotificationCenter.
 * Again, if you want to be notified, subscribe to the JOB_WILL_RUN notification name and read the notification userInfo to know which job.
 * If the job fails, we can also get the exception from the userInfo with the key EXCEPTION_KEY.<p>
 * Depending on the nature of the job description, you have to check the following keys when you access to the userInfo:
 * <ul>
 * <li> Job.ENTERPRISE_OBJECT_KEY if the isEnterpriseObject() method of the job description returns true
 * <li> Job.NOT_PERSISTENT_OBJECT_KEY if the job description is not an enterprise object
 * </ul>
 * 
 * When the job has been executed, the listener logs information and can send an email. The content of the log and 
 * the email are identical.
 * 
 * @see #jobToBeExecuted
 * @see #jobWasExecuted
 * @see #logResult
 */
@Component
public class JobListener extends AbstractListener implements org.quartz.JobListener, Serializable
{
	public static String JOB_WILL_RUN = "jobWillRun";
	public static String JOB_RAN = "jobRan";
	public static String EXCEPTION_KEY = "exceptionKey";
	public static final String DEFAULT_MAIL_SUBJECT_TEMPLATE = "Job info: {0} is done.";
	public static final String DEFAULT_MAIL_ERROR_MESSAGE_TEMPLATE = "Error message: {0}. It took {1}";
	public static final String DEFAULT_MAIL_SHORT_MESSAGE_TEMPLATE = "It took {0}.";
	public static final String DEFAULT_MAIL_MESSAGE_WITH_MORE_INFOS_TEMPLATE = "More informations: {0}. It took {1}.";

    @Autowired
    private AdminService admin;

	public JobListener()
	{
	}

	/**
	 * This method is due to JobListener interface.
	 * Get the name of the JobListener.
	 */
	public String getName() 
	{
		return this.getClass().getName();
	}
	
	/**
	 * This method is due to JobListener interface.<p>
	 * Called by the Scheduler when a JobDetail  was about to be executed (an associated Trigger has occured),
	 * but a TriggerListener vetoed it's execution.<br>
	 * The method is empty.
	 */
	public void jobExecutionVetoed(final JobExecutionContext jobexecutioncontext) 
	{

	}

	/**
	 * This method is due to JobListener interface.<p>
	 * Called by the Scheduler when a JobDetail  is about to be executed (an associated Trigger has occurred).<p>
	 * Posts the notification JOB_WILL_RUN and a userInfo with a global ID if the key is Job.ENTERPRISE_OBJECT_KEY
	 * or directly the JobDescription object with the key Job.NOT_PERSISTENT_OBJECT_KEY
	 */
	public void jobToBeExecuted(final JobExecutionContext jobexecutioncontext) 
	{
		//EOGlobalID id = null;
		JobDescription aJobDescription = null;
		try 
		{
			Map<String, Object> userInfo = null;

			Integer id = (Integer)jobexecutioncontext.getMergedJobDataMap().get(Job.ENTERPRISE_OBJECT_KEY);

			if (id != null) {
				userInfo = new HashMap<String, Object>();
			    userInfo.put(Job.ENTERPRISE_OBJECT_KEY, id);
            } else {
				aJobDescription = (JobDescription) jobexecutioncontext.getMergedJobDataMap().get(Job.NOT_PERSISTENT_OBJECT_KEY);
				if (aJobDescription != null)  {
					userInfo = new HashMap<String, Object>();
                    userInfo.put(Job.NOT_PERSISTENT_OBJECT_KEY, aJobDescription);
                }
			}
//			if (userInfo != null && userInfo.size() > 0)
//				NSNotificationCenter.defaultCenter().postNotification(JOB_WILL_RUN, null, userInfo);

			if(log.isInfoEnabled())
			{
				log.info("*** Job:" + jobexecutioncontext.getJobDetail().getKey().getGroup() + "." + jobexecutioncontext.getJobDetail().getKey().getName() + " start at: " + jobexecutioncontext.getFireTime());
				// + " previousFireTime: " + jobexecutioncontext.getPreviousFireTime() + " nextFireTime: " + jobexecutioncontext.getNextFireTime());
			}
		} catch (Exception e) 
		{
			log.error("method: jobToBeExecuted: an error occured: " +  " /jobDescription: " + aJobDescription, e);
		}	
	}

	/**
	 * This method is due to JobListener interface.
	 * Called by the Scheduler after a JobDetail  has been executed <p>
	 * It retrieve the JobDescription object from the datamap and updates the object.<br>
	 * It also send an email if <code>er.quartzscheduler.JobListener.sendingmail=true</code><p>
	 */
	public void jobWasExecuted(final JobExecutionContext jobexecutioncontext, final JobExecutionException jobexecutionexception) 
	{
        Map<String, Object> userInfo = new HashMap<String, Object>();
		String errorMsg = null;

		if (jobexecutionexception != null)
		{
			errorMsg = jobexecutionexception.getMessage();
			userInfo.put(EXCEPTION_KEY, jobexecutionexception);
			log.error("method: jobWasExecuted: jobexecutionexception: ", jobexecutionexception);
		}

		// Even if there is an exception, we continue to put the jobDescription object in the userInfo
		if (jobexecutioncontext.getMergedJobDataMap() != null)
		{
			JobDescription aJobDescription = null;
			aJobDescription = (JobDescription) jobexecutioncontext.getMergedJobDataMap().get(Job.NOT_PERSISTENT_OBJECT_KEY);

			if (aJobDescription != null)
			{
				userInfo.put(Job.NOT_PERSISTENT_OBJECT_KEY, aJobDescription);
				updateJobDescription(jobexecutioncontext, aJobDescription);
			}

			if (aJobDescription == null)
			{
				Integer id = (Integer) jobexecutioncontext.getMergedJobDataMap().get(Job.ENTERPRISE_OBJECT_KEY);

				// We save in database if there is no exception.
				if (id != null && jobexecutionexception == null)
				{
					userInfo.put(Job.ENTERPRISE_OBJECT_KEY, id);
					try
					{
						if (aJobDescription != null)
						{
							updateJobDescription(jobexecutioncontext, aJobDescription);
						}
					}
                    catch (Exception e)
					{
						errorMsg = e.getMessage();
						userInfo.put(EXCEPTION_KEY, e);
						log.error("method: jobWasExecuted: exception when saving job description: ", e);
					}
				}
			}

			logResult(jobexecutioncontext, errorMsg);
			// We read the value each time because this value can be changed dynamically in development.
		}
//		if (userInfo != null && userInfo.size() > 0)
//			NSNotificationCenter.defaultCenter().postNotification(JOB_RAN, null, userInfo.immutableClone());
	}


	/**
	 * Update the first, last and next execution date attributes of jobDescription
	 * 
	 * @param jobexecutioncontext
	 * @param jobDescription
	 */
	protected void updateJobDescription(final JobExecutionContext jobexecutioncontext, final JobDescription jobDescription)
	{
		if (jobDescription.firstExecutionDate() == null && jobexecutioncontext.getFireTime() != null)
			jobDescription.setFirstExecutionDate(dateToTimestamp(jobexecutioncontext.getFireTime()));

		jobDescription.setLastExecutionDate(dateToTimestamp(jobexecutioncontext.getFireTime()));
		// The next fire time can be null, mainly if it's a simple trigger when launched manually for example.
		if (jobexecutioncontext.getNextFireTime() != null)
			jobDescription.setNextExecutionDate(dateToTimestamp(jobexecutioncontext.getNextFireTime()));
		
		// additional code for saving statMessage in DB
        com.fxmind.entity.Jobs eoJob = (Jobs)jobDescription;
		if ( (eoJob != null) && (jobexecutioncontext != null ) ) {
			Object result = jobexecutioncontext.getResult();
			if ( result != null) {
                //eoJob.setResultMessage(result.toString());
				eoJob.setStatMessage(result.toString());
                admin.saveJob(eoJob);
            }
		}
		
	}
	
	/**
	 * If log info is enabled, logResult logs informations about the job execution like the job duration. It can 
	 * also logs specific information if the job called the method setResult(message) before ending its duty.<p>
	 * But if something wrong happened, the log displays the message <code>errorMsg</code>.
	 * 
	 * @param jobexecutioncontext
	 * @param errorMsg
	 */
	protected void logResult(final JobExecutionContext jobexecutioncontext, final String errorMsg)
	{
		if(log.isInfoEnabled())
		{
			String jobFullName = jobexecutioncontext.getJobDetail().getKey().getGroup() + "." + jobexecutioncontext.getJobDetail().getKey().getName();
			String msg = (String) jobexecutioncontext.getResult();
			String duration = formattedDuration(jobexecutioncontext.getJobRunTime()); 
			if (errorMsg != null)
				log.info("*** Job: '" + jobFullName + "' Error : "+ errorMsg);
            if ((msg != null) && (msg.length() != 0))
                log.info("*** Job:" + jobFullName + " took: " + duration + ", result:" + msg);
		}
	}


	/**
	 * Return a string used by the logger and the mail sending method.<p>
	 * If the duration is less than 180s, the duration is expressed in seconds otherwise there is a conversion in mn.
	 * 
	 * @param duration
	 * @return the formatted duration
	 */
	protected String formattedDuration(final long duration) {
		long durationInMinute = 0;
		long durationInSecond = (duration)/1000; //in seconds

		if (durationInSecond > 180)
		{
			durationInMinute = durationInSecond / 60;
			durationInSecond = durationInSecond % 60;
		}
		return durationInMinute == 0 ? durationInSecond + "s" : (durationInMinute + "mn " + durationInSecond+"s");
	}

	/**
	 * Utility method.
	 * 
	 * @param date
	 * @return the date in Timestamp format
	 */
	protected Timestamp dateToTimestamp(final Date date)
	{
		if (date != null )
			return new Timestamp(date.getTime());
		return null;
	}

}
