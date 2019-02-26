package com.fxmind.manager.jobs;

import com.fxmind.manager.quartz.Job;
import com.fxmind.quartz.JobDescription;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
   This type of task Allows! concurent execution!!
 */
@PersistJobDataAfterExecution
public abstract class AdminTask extends Job {
	private static final Logger log = LoggerFactory.getLogger(SystemTask.class);

	private boolean _stopped = false;
	
	protected String statMessage;
	
	public static final int MILLISECONDS_IN_SECONDS = 1000;
	public static final int SECONDS_IN_MINUTE = 60;
	public static final int MINUTES_IN_HOUR = 60;
	public static final int HOURS_IN_DAY = 24;
	public static final int DAYS_IN_WEEK = 7;
	private static final int MILLISECONDS_IN_DAY = MILLISECONDS_IN_SECONDS * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY;
	public static final int MILLISECONDS_IN_HOUR = MILLISECONDS_IN_SECONDS * SECONDS_IN_MINUTE * MINUTES_IN_HOUR;
	private static final int REPLENISHMENT_TIMEOUT = MILLISECONDS_IN_SECONDS * SECONDS_IN_MINUTE * 10;

	public String name() {
		return getClass().getSimpleName();
	}

	public abstract void run();
	
	public String status() {
		setResultMessage(statMessage);
		return statMessage;
	}

    public synchronized void stop() {
        _stopped = true;
        try {
            interrupt();
        } catch (UnableToInterruptJobException e1) {
            e1.printStackTrace();
        }
    }

    protected void throwIfCancelled()  {
        if (isJobInterrupted()) {
            stop();
            throw new RuntimeException(statMessage);
        }
    }


	protected synchronized boolean stopped() {
		return _stopped;
	}
	
	Job getJob() {
		return this;
	}
	
	// methods from ERQSJob
	@Override
	protected void _execute() throws JobExecutionException {
		//if (getJob().disabled())
		//	return;
		run();
        status();
        //log.info("Task finished with Result: " + status());
		_stopped = true;
	}

	@Override
	public void willDelete(JobDescription aJobDescription) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void willSave(JobDescription aJobDescription) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateForSave(JobDescription aJobDescription) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateForDelete(JobDescription aJobDescription) {
		// TODO Auto-generated method stub
		
	}
	
}
