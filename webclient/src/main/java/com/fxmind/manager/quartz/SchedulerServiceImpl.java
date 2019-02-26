package com.fxmind.manager.quartz;

import com.fxmind.entity.Jobs;
import com.fxmind.global.FXMindMQLServer;
import com.fxmind.manager.jobs.GlobalServerJob;
import com.fxmind.quartz.JobDescription;
import com.fxmind.service.AdminService;
import com.fxmind.service.SchedulerService;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 *  @author Sergei Zhuravlev
 */
@Service
@Scope(value = "singleton")
public class SchedulerServiceImpl implements SchedulerService, Serializable
{
    protected static final Logger log = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	public static final String INSTANCE_KEY = "COInstanceKey";
	private volatile Scheduler quartzSheduler;
    private static final long serialVersionUID = 100L;

    @Autowired
    AdminService service;

    @Autowired
    JobListener listener;

    public static SchedulerServiceImpl instance;

    /**
     * This method initializes the scheduler service.<p>
     */
    //@PostConstruct
    public void startScheduler() {
        if (instance == null) {
            getScheduler();
        }
    }

    public List<JobDescription> getActiveJobs() {
        List<JobDescription> listJobDesc = new LinkedList<JobDescription>();
        for(Jobs job : service.getActiveJobs()) {
            listJobDesc.add((JobDescription)job);
        }
        return listJobDesc;
    }

	/**
	 * Return the quartz scheduler which schedules the job described by JobDescription objects.<p>
	 * As the quartz scheduler is uses a ram job stores, everything is gone when the scheduler stops.<br>
	 * The persistence must be handled by your own EOs which must implement JobDescription interface.<p>
	 * You have several options to set quartz properties:
	 *
	 * @return the scheduler
	 */
	public Scheduler getScheduler()
	{
		if (instance == null)
		{
			try 
			{
                StdSchedulerFactory sf = new StdSchedulerFactory();
                // Initialize Quartz from quartz.properties file
                Properties props = service.getProps();
                //admin = service;
                instance = this;

                sf.initialize(props);

                quartzSheduler = sf.getScheduler();

                quartzSheduler.start();
                addJobListener(listener);
                instantiateJobSupervisor();
                instantiateThriftServer();

            } catch (SchedulerException e)
			{
				log.error("method: getScheduler: exception", e);
			}
		}
		return quartzSheduler;
	}

	/**
	 * Return a list of all jobs handled by the scheduler, even those that are not managed by the framework, aka
	 * jobs that have been added manually.
	 * 
	 * @return immutable list of all job detail or an empty list
	 */
    public List<JobDetail> getScheduledJobs()
    {
        List<JobDetail> jobDetailList = new ArrayList<JobDetail>();
    	try
    	{
    		List<String> groups = getScheduler().getJobGroupNames();

    		for (int i = 0; i < groups.size(); i++)
    		{
    			String name = groups.get(i);
    			GroupMatcher<JobKey> matcher = GroupMatcher.groupEquals(name);
    			Set<JobKey> keys = getScheduler().getJobKeys(matcher);

    			for (JobKey jk : keys) 
    			{
    				JobDetail jd = getScheduler().getJobDetail(jk);

    				jobDetailList.add(jd);
    			}
    		}
    		return jobDetailList;
    	} catch (SchedulerException e) 
    	{
    		log.error("method: getAllJobs: execution error.", e);
    	}
    	return jobDetailList;
    }

    /**
     * Return a list of running jobs
     *
     * @return immutable list of all job detail or null
     */
    public List<JobExecutionContext> getRunningJobs()
    {
        if (getScheduler() != null) {
            try {
                return getScheduler().getCurrentlyExecutingJobs();
            } catch (SchedulerException e)
            {
                log.error(e.toString());
            }
        }
        return null;
    }

    @Override
    public boolean hasRunningJobs()
	{
		List<JobExecutionContext> executingJobs = null;
		try 
		{
			executingJobs = getScheduler().getCurrentlyExecutingJobs();
		} catch (SchedulerException e) 
		{
			log.error("method: hasRunningJobs: execution error", e);
		}
		return executingJobs != null && executingJobs.size() > 0;
	}

	public Trigger.TriggerState getTriggerState(final JobKey aJobKey)
	{
		Trigger aTrigger = getTriggerOfJob(aJobKey);
		if (aTrigger == null)
			return Trigger.TriggerState.NONE;
		try 
		{
			return getScheduler().getTriggerState(aTrigger.getKey());
		} catch (SchedulerException e) 
		{
			log.error("method: getTriggerState: error for JobKey: " + aJobKey, e);
		}
		return Trigger.TriggerState.NONE;
	}
	
	public Trigger getTriggerOfJob(final JobKey aJobKey)
	{
    	try 
    	{
			if (getScheduler().getTriggersOfJob(aJobKey).size() > 0)
	    		return getScheduler().getTriggersOfJob(aJobKey).get(0);
		} catch (SchedulerException e) 
		{
			log.error("method: getTriggerOfJob: error for JobKey: " + aJobKey, e);
		}
		return null;
	}
	
	public void triggerNow(final JobDetail aJob)
	{
        try {
	    	getScheduler().triggerJob(aJob.getKey(), aJob.getJobDataMap());
        } catch (SchedulerException e )  {
            log.error(e.toString());
        }
    }

    public void stopJob(final JobExecutionContext aJobContext)
    {
        if ( aJobContext == null )
            return;
        try
        {
            JobKey key = aJobContext.getJobDetail().getKey();
            getScheduler().interrupt(key);
        } catch (Exception e)
        {
            log.error(e.toString());
        }
    }

    public void RunJobNow(String group, String name, JobDataMap map) {
        try {
            //JobDescription jobDesc = getJobDescription(group, name);
            //if (jobDesc == null)
            //    return;
            JobDetail jd = getScheduler().getJobDetail(new JobKey(name, group));
            if (jd != null)
                getScheduler().triggerJob(jd.getKey(), map);
        }  catch (SchedulerException se) {
           log.error(se.toString());
        }
    }

    protected void instantiateJobSupervisor()
	{
		Class<? extends Job> supervisorClass = JobSupervisor.class;

        //SimpleClassLoadHelper loader = new SimpleClassLoadHelper();

		JobDataMap map = new JobDataMap();
		map.put(INSTANCE_KEY, this);
		JobDetail job = newJob(supervisorClass).withIdentity("JobSupervisor", Scheduler.DEFAULT_GROUP).usingJobData(map).build();
		
		Trigger trigger = newTrigger()
		.withIdentity("JobSupervisorTrigger")
		.startAt(futureDate(5, IntervalUnit.SECOND))
		.withPriority(Trigger.DEFAULT_PRIORITY)
		//.withSchedule(simpleSchedule()
        //      .withIntervalInMinutes(supervisorSleepDuration())
        //      .withRepeatCount(1))
				.build();
		// Attache data to the job
		try 
		{
			getScheduler().scheduleJob(job, trigger);
		} catch (SchedulerException e) 
		{
			log.error("method: instantiateJobSupervisor: unable to launch supervisor.", e);
		}
	}


    protected void instantiateThriftServer()
    {
        Class<? extends Job> supervisorClass = GlobalServerJob.class;
        JobDataMap map = new JobDataMap();
        map.put(INSTANCE_KEY, this);
        JobDetail job = newJob(supervisorClass).withIdentity("GlobalServerJob", "SYSTEM").usingJobData(map).build();

        Trigger trigger = newTrigger()
                .withIdentity("GlobalServerJobTrigger")
                .startAt(futureDate(5, IntervalUnit.SECOND))
                .withPriority(Trigger.DEFAULT_PRIORITY*2) // start with higher priority
                .build();
        // Attache data to the job
        try
        {
            getScheduler().scheduleJob(job, trigger);
        } catch (SchedulerException e)
        {
            log.error("method: instantiateThriftServer: unable to launch Thrift Server.", e);
        }
    }

    /**
	 * Use this method if you need to add other listeners jobs handled by COScheduler, aka job with group
	 * beginning with JobSupervisor.GROUP_NAME_PREFIX
	 * 
	 * @param newJobListener
	 */
	protected void addJobListener(final org.quartz.JobListener newJobListener)
	{
		try 
		{
			GroupMatcher<JobKey> matcher = GroupMatcher.anyGroup();//.groupStartsWith(JobSupervisor.GROUP_NAME_PREFIX);
			getScheduler().getListenerManager().addJobListener(newJobListener, matcher);
		} catch (SchedulerException e) 
		{
			log.error("method: addJobListener: unable to add a job listener", e);
		}
	}
	
	protected int supervisorSleepDuration()
	{
        String propInt = service.getProperty("quartzscheduler.COJobSupervisor.sleepduration");
        return Integer.parseInt(propInt);
    }
	
	public synchronized void deleteAllJobs()
	{
		List<JobDetail> allJobs = getScheduledJobs();
		if (allJobs.size() > 0)
		{
			List<JobKey> jobKeys = new ArrayList<JobKey>(allJobs.size());
			for (JobDetail jobDetail : allJobs)
			{
                //JobKey jk = new JobKey(jobDetail.getGroup(), jobDetail.getName());
				jobKeys.add(jobDetail.getKey());
			}
			try 
			{
				getScheduler().deleteJobs(jobKeys);
			} catch (SchedulerException e) 
			{
	   			log.error("method: deleteAllJobs", e);
			}
		}
	}

    @Override
	public synchronized void stopScheduler()
	{
		try 
		{
            if (FXMindMQLServer.server != null) {
                log.info("000000000000000000000000! Stopping thrift server !0000000000000000000000000000 ");
                FXMindMQLServer.server.stop();
                FXMindMQLServer.server = null;
            }

            if (instance != null) {
                log.info("000000000000000000000000! Going to stop Quartz !0000000000000000000000000000 ");
                // true means it will wait till all jobs finished
                quartzSheduler.shutdown(true);
                log.info("000000000000000000000000! Quartz stopped !0000000000000000000000000000 ");
                quartzSheduler = null;
                instance = null;
            }
		} catch (SchedulerException e)
		{
			log.error("method: stopScheduler: exception: " + e.getMessage(), e);
		}
	}

    public Trigger getFirstTrigger(JobKey jk)
    {
        try {
            if (getScheduler().getTriggersOfJob(jk).size() > 0)
                return getScheduler().getTriggersOfJob(jk).get(0);
            else
                return null;
        } catch (SchedulerException e) {
            log.error(e.toString());
            return null;
        }
    }

    @Override
    public JobDescription getJobDescription(String group, String name) {

        try {

            JobDescription aJobDescription = null;
            JobDetail jd = getScheduler().getJobDetail(new JobKey(name, group));
            if (jd == null)
                return null;

            aJobDescription = (JobDescription) jd.getJobDataMap().get(com.fxmind.manager.quartz.Job.NOT_PERSISTENT_OBJECT_KEY);

            if (aJobDescription != null) {
                return aJobDescription;
            }  else {

               aJobDescription = service.loadJob(group, name);
            }
            return aJobDescription;
        }   catch (SchedulerException e) {
            log.error(e.toString());
            return null;
        }
    }

    public String getJobLastResult(JobDetail aJob) {
        String result = "";
        if ( aJob != null ) {
            result = (String)aJob.getJobDataMap().get("STATMESSAGE");
        }
        return result;
    }


}