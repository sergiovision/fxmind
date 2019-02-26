package com.fxmind.quartz;

import java.sql.Timestamp;
import java.util.Map;

/**
 *
 * @author Sergei Zhuravlev
 *
 */
public interface JobDescription
{
	/**
	 * The name and the group are very important because the scheduler retrieve jobs based on the name and group.<p>
	 * It can't be null.
	 * 
	 * @return job name
	 */
	String name();
	
	/**
	 * If group() return null or an empty string, Scheduler.DEFAULT_GROUP is used instead.
	 * 
	 * @return group
	 */
	String group();
	
	/**
	 * The cron expression allows you to define a period where the job is triggered.<br>
	 * If the cron expression returns null, the job runs once immediately.
	 * 
	 * See the documentation: http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
	 * 
	 * @return cron expression
	 */
	String cronExpression();
	
	/**
	 * The description is optional. It's used only when displaying the dashboard so it can give you additional
	 * informations useful for you.
	 * 
	 * @return a job description
	 */
	String jobDescription();
	
	/**
	 * Object that will be instantiated by the scheduler to make its job. Of course, very important ;-)
	 * 
	 * @return a class path
	 */
	String classPath();
	
	/**
	 * A getter that returns the last execution date of the job.
	 * 
	 * @return last execution date
	 */
	Timestamp lastExecutionDate();
	void setLastExecutionDate(Timestamp lastExecutionDate);
	
	/**
	 * A getter that returns the first execution date of the job.
	 * 
	 * @return last execution date
	 */
    Timestamp firstExecutionDate();
	void setFirstExecutionDate(Timestamp firstExecutionDate);
	
	/**
	 * A setter to save the next execution date.<p>
	 * Notice that there is no getter because the framework doesn't need it to run. But it's a good idea to code it.
	 * 
	 * @param nextExecutionDate
	 */
	void setNextExecutionDate(Timestamp nextExecutionDate);
	
	/**
	 * jobParams is used to pass information when the job will run.<p>
	 * All key/value pair will be given to the job.
	 */
	Map<String, Object> jobParams();
}
