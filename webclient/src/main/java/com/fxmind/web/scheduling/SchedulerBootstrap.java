package com.fxmind.web.scheduling;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.TriggerBuilder.newTrigger;

import com.fxmind.service.SettingsService;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Registers the {@link JobSupervisor} once the application is ready to serve. */
@Component
@ConditionalOnBooleanProperty(name = "fxmind.scheduler.enabled", matchIfMissing = true)
public class SchedulerBootstrap {

    static final JobKey SUPERVISOR = JobKey.jobKey("JobSupervisor", "SYSTEM");
    static final String SYNC_INTERVAL_SETTING = "scheduler.sync-interval-seconds";
    private static final int DEFAULT_SYNC_INTERVAL_SECONDS = 60;

    private static final Logger log = LoggerFactory.getLogger(SchedulerBootstrap.class);

    private final Scheduler scheduler;
    private final SettingsService settings;

    public SchedulerBootstrap(Scheduler scheduler, SettingsService settings) {
        this.scheduler = scheduler;
        this.settings = settings;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerSupervisor() throws SchedulerException {
        if (scheduler.checkExists(SUPERVISOR)) {
            return;
        }
        int interval =
                settings.value(SYNC_INTERVAL_SETTING).map(Integer::parseInt).orElse(DEFAULT_SYNC_INTERVAL_SECONDS);
        scheduler.scheduleJob(
                newJob(JobSupervisor.class)
                        .withIdentity(SUPERVISOR)
                        .withDescription("Synchronises Quartz with the jobs table")
                        .build(),
                newTrigger()
                        .withIdentity(SUPERVISOR.getName(), SUPERVISOR.getGroup())
                        .startNow()
                        .withSchedule(repeatSecondlyForever(interval))
                        .build());
        log.info("Job supervisor registered (every {}s)", interval);
    }
}
