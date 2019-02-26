package com.fxmind.service;
import com.fxmind.quartz.JobDescription;

/**
 * Created by sergeizhuravlev on 4/5/14.
 */
public interface SchedulerService {

    boolean hasRunningJobs();

    void startScheduler();

    void stopScheduler();

    JobDescription getJobDescription(String group, String name);

}
