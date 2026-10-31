package com.fxmind.web.scheduling;

import java.util.List;

/** Facade over Quartz used by the admin console. */
public interface SchedulerService {

    List<ScheduledJobView> listJobs();

    void runNow(String group, String name);

    void updateJob(String group, String name, String cron, boolean enabled);

    boolean isPaused();

    void pause();

    void resume();
}
