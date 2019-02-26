package com.fxmind.manager.quartz;

import com.fxmind.quartz.JobDescription;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract listener provides reusable methods like editingContext and getJobDescription to any listener like JobListener.<p>
 * This class can be used if you need to create your own job listener or trigger listener.
 *
 * @author Sergei Zhuravlev
 */
public abstract class AbstractListener
{
	protected static final Logger log = LoggerFactory.getLogger(AbstractListener.class);
	/**
	 * Send back the JobDescription object attached to the job.
	 *
	 * @param context the JobExecutionContext
	 * @return the JobDescription object.
	 */
	protected JobDescription getJobDescription(final JobExecutionContext context)
	{
		JobDescription aJobDescription = null;
		if (context.getMergedJobDataMap() != null)
		{
				aJobDescription = (JobDescription) context.getMergedJobDataMap().get(Job.NOT_PERSISTENT_OBJECT_KEY);
		}
		return aJobDescription;
	}


}