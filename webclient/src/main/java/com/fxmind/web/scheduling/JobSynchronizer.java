package com.fxmind.web.scheduling;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import com.fxmind.domain.JobDefinition;
import com.fxmind.service.JobDefinitionService;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.quartz.CronTrigger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Reconciles Quartz with persistent {@link JobDefinition}s: schedules new ones, re-schedules changed
 * cron expressions and removes disabled or deleted ones. Only jobs it created (marked as managed) are
 * ever touched.
 */
@Component
public class JobSynchronizer {

    static final String MANAGED_KEY = "managed";

    private static final Logger log = LoggerFactory.getLogger(JobSynchronizer.class);

    private final Scheduler scheduler;
    private final JobDefinitionService jobDefinitions;

    public JobSynchronizer(Scheduler scheduler, JobDefinitionService jobDefinitions) {
        this.scheduler = scheduler;
        this.jobDefinitions = jobDefinitions;
    }

    /** Result counters, rendered as the supervisor's status. */
    public record SyncResult(int added, int rescheduled, int removed, int failed) {}

    public synchronized SyncResult synchronize() throws SchedulerException {
        int added = 0;
        int rescheduled = 0;
        int failed = 0;
        Set<JobKey> wanted = new HashSet<>();
        for (var definition : jobDefinitions.findEnabled()) {
            var jobKey = JobKey.jobKey(definition.getName(), definition.getGroup());
            wanted.add(jobKey);
            try {
                if (!scheduler.checkExists(jobKey)) {
                    schedule(definition, jobKey);
                    added++;
                } else if (cronChanged(definition, jobKey)) {
                    scheduler.rescheduleJob(triggerKey(jobKey), trigger(definition, jobKey));
                    rescheduled++;
                }
            } catch (RuntimeException | SchedulerException e) {
                // One broken definition (e.g. a cron edited via SQL) must not block the others. The job
                // is unscheduled rather than left firing on its stale cron, so the console shows a
                // stable INVALID status instead of alternating with successful runs.
                failed++;
                var status = "INVALID SCHEDULE - " + e.getMessage();
                log.warn("Cannot schedule job {}: {}", jobKey, e.getMessage());
                unscheduleQuietly(jobKey);
                jobDefinitions.recordExecution(
                        definition.getGroup(), definition.getName(), status, definition.getPreviousFireTime(), null);
            }
        }
        int removed = 0;
        for (var jobKey : scheduler.getJobKeys(GroupMatcher.anyJobGroup())) {
            if (!wanted.contains(jobKey) && isManaged(jobKey)) {
                scheduler.deleteJob(jobKey);
                removed++;
            }
        }
        var result = new SyncResult(added, rescheduled, removed, failed);
        if (added + rescheduled + removed + failed > 0) {
            log.info("Scheduler synchronised: {}", result);
        }
        return result;
    }

    private void unscheduleQuietly(JobKey jobKey) {
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.warn("Could not unschedule invalid job {}", jobKey, e);
        }
    }

    static TriggerKey triggerKey(JobKey jobKey) {
        return TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup());
    }

    private void schedule(JobDefinition definition, JobKey jobKey) throws SchedulerException {
        var detail = newJob(ProviderJob.class)
                .withIdentity(jobKey)
                .withDescription(definition.getDescription())
                .usingJobData(ProviderJob.HANDLER_KEY, definition.getHandler())
                .usingJobData(MANAGED_KEY, true)
                .storeDurably(false)
                .build();
        scheduler.scheduleJob(detail, trigger(definition, jobKey));
    }

    private boolean cronChanged(JobDefinition definition, JobKey jobKey) throws SchedulerException {
        return !(scheduler.getTrigger(triggerKey(jobKey)) instanceof CronTrigger cron)
                || !Objects.equals(cron.getCronExpression(), definition.getCron());
    }

    private boolean isManaged(JobKey jobKey) throws SchedulerException {
        var detail = scheduler.getJobDetail(jobKey);
        return detail != null && detail.getJobDataMap().getBooleanValue(MANAGED_KEY);
    }

    private static CronTrigger trigger(JobDefinition definition, JobKey jobKey) {
        return newTrigger()
                .withIdentity(triggerKey(jobKey))
                .forJob(jobKey)
                .withSchedule(cronSchedule(definition.getCron()).withMisfireHandlingInstructionDoNothing())
                .build();
    }
}
