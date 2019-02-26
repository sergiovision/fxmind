package com.fxmind.manager.quartz;

import com.fxmind.entity.Jobs;
import com.fxmind.quartz.JobDescription;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * The supervisor has in charge to add, remove or update the list of job handled by the quartz scheduler.<p>
 * Every job handled by the supervisor has a group starting by GROUP_NAME_PREFIX. The goal is to let developers to add any
 * job directly, aka not linked to a job description. For that reason, by convention, the jobs not handled by the
 * supervisor must have a group not starting with GROUP_NAME_PREFIX
 *
 * @author Sergei Zhuravlev
 *
 */
@DisallowConcurrentExecution
public class JobSupervisor extends AbstractJob
{
	public static final int DEFAULT_SLEEP_DURATION = 10;
    protected static final Logger log = LoggerFactory.getLogger(JobSupervisor.class);

	@Override
	public void execute(final JobExecutionContext jobexecutioncontext) throws JobExecutionException 
	{
		super.execute(jobexecutioncontext);
		
		try
		{
			List<? extends JobDescription> jobs2Check = SchedulerServiceImpl.instance.getActiveJobs();
			setResultMessage("# of jobs to check: " + jobs2Check.size());
			removeObsoleteJobs(jobs2Check);
			if (jobs2Check.size() != 0)
				addOrModifyJobs(jobs2Check);
		} catch (Exception e)
		{
			log.error("method: execute: fetching jobs.", e);
		}
		finally
		{
		}
	}

	/**
	 * Return a a set of jobs handled currently by Quartz. Actually, it's a set of JobKey rather than Job.
	 * 
	 * @return set of JobKeys, never return null but an empty set instead.
	 */
	protected Set<JobKey> getScheduledJobKeys()
	{
		Set<JobKey> scheduledJobKeys = null;
		try 
		{
			GroupMatcher<JobKey> matcher = GroupMatcher.anyGroup(); //.groupStartsWith(GROUP_NAME_PREFIX);
			scheduledJobKeys = getScheduler().getJobKeys(matcher);
		} catch (SchedulerException e) 
		{
			log.error("method: getScheduledJobKeys: unable to get the list.", e);
		}
		return scheduledJobKeys == null ? new java.util.HashSet<JobKey>(0) : scheduledJobKeys;
	}

	/**
	 * From jobs2Check (a fresh list of JobDescription objects), removeJobs checks if jobs must be removed.<p>
	 * 
	 * @param jobs2Check list of JobDescription objects
	 */
	protected void removeObsoleteJobs(final List<? extends JobDescription> jobs2Check)
	{
		List<JobKey> jobKeys2remove = new ArrayList<JobKey>();
		List<JobKey> scheduledJobKeysSet = new ArrayList<JobKey>(getScheduledJobKeys());

		// If the list of existing jobs is empty, nothing to remove
		if (scheduledJobKeysSet.size() != 0)
		{
			// If there is no new job, we must remove all existing jobs
			if (jobs2Check.size() == 0)
				jobKeys2remove = scheduledJobKeysSet;
			else
			{
				Set<JobKey> jobKeys2Check = new HashSet<JobKey>(jobs2Check.size());
				for (JobDescription aJob2Check : jobs2Check)
				{
					JobKey aJobKey = getJobKeyForJobDescription(aJob2Check);
					jobKeys2Check.add(aJobKey);
				}
				//jobKeys2remove = scheduledJobKeysSet.setBySubtractingSet(jobKeys2Check);
			}
			
			if (log.isDebugEnabled())
				log.debug("method: removeJobs: jobKeys2remove.size: " + jobKeys2remove.size());
			if (jobKeys2remove.size() != 0)
			{
				setResultMessage("# of jobs to remove: " + jobKeys2remove.size());
				try 
				{
					getScheduler().deleteJobs(jobKeys2remove);
				} catch (SchedulerException e) 
				{
					log.error("method: removeJobs: unable to remove the jobs.", e);
				}
			}
		}
	}

	/**
	 * From jobs2Check (a fresh list of JobDescription objects), addOrModifyJobs checks if jobs must be added or modified.<p>
	 * 
	 * @param jobs2Check list of JobDescription objects
	 */
	protected void addOrModifyJobs(final List<? extends JobDescription> jobs2Check)
	{
		setResultMessage("# of jobs to add or modify: " + jobs2Check.size());
		for (JobDescription aJob2Check : jobs2Check)
		{
			JobKey aJobKey = getJobKeyForJobDescription(aJob2Check);
			try 
			{
				JobDetail aJobDetail = getScheduler().getJobDetail(aJobKey);
				if (log.isDebugEnabled())
					log.debug("method: jobs2AddOrModify: aJobKey: " + aJobKey + " /aJobDetail in scheduler: " + aJobDetail);
				
				if (aJobDetail == null)
					addJob2Scheduler(aJob2Check);
				else
					modifyJob(aJob2Check, aJobDetail);
				
			} catch (SchedulerException e) 
			{
				log.error("method: addOrModifyJobs: error when retrieving a jobDetail with this jobKey: " + aJobKey, e);
			}
		}
	}

	public JobKey getJobKeyForJobDescription(final JobDescription aJobDescription)
	{
		return new JobKey(aJobDescription.name(), buildGroup(aJobDescription.group()));
	}
	
	/**
	 * Add a job to the scheduler described the job description job2Add.<p>
	 * 
	 * @param job2Add job to add
	 */
	protected void addJob2Scheduler(final JobDescription job2Add)
	{
		if (!isJobDescriptionValid(job2Add))
			throw new IllegalArgumentException("method: addJob2Scheduler: some fields of job2Add are null or empty: job2Check: " + job2Add);

		else
		{
			JobDetail job = buildJobDetail(job2Add);
			if (log.isDebugEnabled())
				log.debug("method: addJob2Scheduler: job: " + job);
			if (job != null)
			{
				Trigger trigger;
				try 
				{
					trigger = buildTriggerForJob(job2Add, job);
					getScheduler().scheduleJob(job, trigger);
				}
				catch (SchedulerException se) 
				{
					log.error("method: addJob2Scheduler: unable to schedule the job: " + job2Add.group() + "." + job2Add.name(), se);
				}
			}
		}
	}

	protected void modifyJob(final JobDescription job2Check, final JobDetail job)
	{
		if (log.isDebugEnabled())
			log.debug("method: modifyJob: ENTER: job2Check: " + job2Check + " /job: " +job);
		if (!isJobDescriptionValid(job2Check))
			throw new IllegalArgumentException("method: applyModification2Scheduler: some fields of job2Check are null or empty: job2Check: " + job2Check);
		// We compare the job description with the scheduled job
		// We don't compare to the name and group because the job would have been removed and added just before.
		Scheduler scheduler = getScheduler();
		String jobClass = job.getJobClass().getName();
		String jobDescription = job.getDescription();
		String jobCronExpression;
		String strStatMessage = (String)job2Check.jobParams().get("STATMESSAGE");
		if ( strStatMessage == null )
			strStatMessage = new String();
		String currentStatMessage = (String) job.getJobDataMap().get("STATMESSAGE");

		boolean isJobModified =  (!job2Check.jobDescription().equals(jobDescription) || !job2Check.classPath().equals(jobClass)
								|| !strStatMessage.equals(currentStatMessage));
		try 
		{
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(job.getKey());
			if (triggers.size() != 0 && triggers.get(0) instanceof CronTrigger)
			{
				CronTrigger aTrigger = (CronTrigger) triggers.get(0);
				jobCronExpression = aTrigger.getCronExpression();
				
				if (!job2Check.cronExpression().equals(jobCronExpression) && !isJobModified)
				{
					//We just need to reschedule the job
					Trigger newTrigger = buildTriggerForJob(job2Check, job);
					TriggerKey aTriggerKey = new TriggerKey(buildTriggerName(job2Check.name()), buildGroup(job2Check.group()));
					scheduler.rescheduleJob(aTriggerKey, newTrigger);
					if (log.isDebugEnabled())
						log.debug("method: modifyJob: job2Check: " + job2Check + " has been rescheduled.");
				}
				if (isJobModified)
				{
					if (log.isDebugEnabled())
						log.debug("method: modifyJob: job2Check: " + job2Check + " has been removed then added.");
					// We remove the job and we create a new one
					getScheduler().deleteJob(job.getKey());
					addJob2Scheduler(job2Check);
				}
			}
		} catch (SchedulerException e) 
		{
			log.error("method: modifyJob: unable to get triggers of job: " + job2Check.group() + "." + job2Check.name(), e);
		}
		if (log.isDebugEnabled())
			log.debug("method: modifyJob: DONE: job2Check: " + job2Check + " /job: " +job + " /isJobModified: " + isJobModified);
	}

	/**
	 * Return a job detail built from a JobDescription object
	 * 
	 * @param jobDescription
	 * @return a JobDetail object
	 */
	protected JobDetail buildJobDetail(final JobDescription jobDescription)
	{
		JobDataMap map = new JobDataMap();
		map.put(SchedulerServiceImpl.INSTANCE_KEY, SchedulerServiceImpl.instance);
        Jobs jobsDao = (Jobs)jobDescription;
        map.put(Job.ENTERPRISE_OBJECT_KEY, jobsDao.getId());
        map.put(Job.NOT_PERSISTENT_OBJECT_KEY, jobDescription);
        String strStatMessage = (String)jobDescription.jobParams().get("STATMESSAGE");
        map.put("STATMESSAGE", strStatMessage);

		String name = jobDescription.name();
		String group = jobDescription.group();
		String classPath = jobDescription.classPath();
		String description = jobDescription.jobDescription();
		JobDetail job = null;
		Class<? extends org.quartz.Job> jobClass = getClass(classPath);
		if (jobClass != null)
		{
			job = newJob(jobClass)
			.withIdentity(name, buildGroup(group))
			.withDescription(description)
			.usingJobData(map)
			.build();
		}
		if (jobDescription.jobParams() != null)
			job.getJobDataMap().putAll(jobDescription.jobParams());
		return job;
	}

	/**
	 * Return a trigger built from a JobDescription object and a JobDetail object
	 * 
	 * @param jobDescription (we suppose that jobDescription is a subclass of ERXGenericRecord or a non persistent object)
	 * @param job
	 * @return a Trigger object
	 */
	protected Trigger buildTriggerForJob(final JobDescription jobDescription, final JobDetail job)
	{
		String name = jobDescription.name();
		String group = jobDescription.group();
		String cronExpression = jobDescription.cronExpression();
		
		return buildTrigger(name, group, cronExpression, null, job);
	}

	protected Trigger buildTrigger(final String name, final String group, final String cronExpression, final JobDataMap map, final JobDetail job)
	{
		Trigger trigger = null;		
		ScheduleBuilder<? extends Trigger> scheduleBuilder = null;
		if (cronExpression != null)
		{
			try 
			{
				scheduleBuilder = cronSchedule(cronExpression);
			} catch (RuntimeException e) 
			{
				log.error("method: buildTrigger: cronExpression: " + cronExpression + " for name: " + name + " /group: " + group, e);
			}
		}
		else
			scheduleBuilder = simpleSchedule();
		
		trigger = newTrigger()
		.withIdentity(buildTriggerName(name), buildGroup(group))
		.withPriority(Trigger.DEFAULT_PRIORITY)
		.forJob(job)
		.usingJobData(map == null ? new JobDataMap() : map)
		.withSchedule(scheduleBuilder)
		.build();
		return trigger;
	}
	
	protected String buildTriggerName(final String name) 
	{
		return name ;//+ TRIGGER_SUFFIX;
	}

	protected String buildGroup(final String group) 
	{
		if (group == null || group.length()==0 )
			return Scheduler.DEFAULT_GROUP;
		return group;
	}

	protected boolean isJobDescriptionValid(final JobDescription aJobDescription)
	{
		return (aJobDescription.classPath() != null && aJobDescription.classPath().length() != 0
				&& aJobDescription.name() != null  && aJobDescription.name().length() != 0
				);
	}

	protected Class<? extends org.quartz.Job> getClass(final String path) {
		Class<? extends org.quartz.Job> jobClass = null;
		try
		{
			jobClass = (Class<? extends org.quartz.Job>) Class.forName(path, false, this.getClass().getClassLoader());
		}
		catch (ClassNotFoundException ce)
		{
			log.error("method: getClass: path: " + path + " /exception: " + ce.getMessage(), ce);
		}
		catch (ExceptionInInitializerError ie)
		{
			log.error("method: getClass: path: " + path + " /exception: " + ie.getMessage(), ie);
		}
		catch (LinkageError le)
		{
			log.error("method: getClass: path: " + path + " /exception: " + le.getMessage(), le);
		}
		return jobClass;
	}
}
