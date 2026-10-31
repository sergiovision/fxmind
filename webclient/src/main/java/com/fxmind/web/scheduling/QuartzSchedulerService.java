package com.fxmind.web.scheduling;

import com.fxmind.service.JobDefinitionService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.quartz.CronExpression;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

/** {@link SchedulerService} implementation that combines Quartz runtime state with job definitions. */
@Service
public class QuartzSchedulerService implements SchedulerService {

    private final Scheduler scheduler;
    private final JobDefinitionService jobDefinitions;
    private final JobSynchronizer synchronizer;

    public QuartzSchedulerService(
            Scheduler scheduler, JobDefinitionService jobDefinitions, JobSynchronizer synchronizer) {
        this.scheduler = scheduler;
        this.jobDefinitions = jobDefinitions;
        this.synchronizer = synchronizer;
    }

    @Override
    public List<ScheduledJobView> listJobs() {
        var running = runningJobs();
        return jobDefinitions.findAll().stream()
                .map(definition -> {
                    var key = JobKey.jobKey(definition.getName(), definition.getGroup());
                    return new ScheduledJobView(
                            definition.getGroup(),
                            definition.getName(),
                            definition.getHandler(),
                            definition.getDescription(),
                            definition.getCron(),
                            definition.isEnabled(),
                            triggerState(key),
                            running.contains(key),
                            definition.getLastStatus(),
                            definition.getPreviousFireTime(),
                            definition.getNextFireTime());
                })
                .toList();
    }

    @Override
    public void runNow(String group, String name) {
        quartz(() -> {
            var key = JobKey.jobKey(name, group);
            if (!scheduler.checkExists(key)) {
                throw new IllegalArgumentException("Job " + group + "." + name + " is not scheduled (disabled?)");
            }
            scheduler.triggerJob(key);
            return null;
        });
    }

    @Override
    public void updateJob(String group, String name, String cron, boolean enabled) {
        if (!CronExpression.isValidExpression(cron)) {
            throw new IllegalArgumentException("Invalid Quartz cron expression: " + cron);
        }
        jobDefinitions.update(group, name, cron, enabled);
        quartz(synchronizer::synchronize);
    }

    @Override
    public boolean isPaused() {
        return quartz(scheduler::isInStandbyMode);
    }

    @Override
    public void pause() {
        quartz(() -> {
            scheduler.standby();
            return null;
        });
    }

    @Override
    public void resume() {
        quartz(() -> {
            scheduler.start();
            return null;
        });
    }

    private Set<JobKey> runningJobs() {
        return quartz(() -> scheduler.getCurrentlyExecutingJobs().stream()
                .map(JobExecutionContext::getJobDetail)
                .map(detail -> detail.getKey())
                .collect(Collectors.toSet()));
    }

    private String triggerState(JobKey key) {
        return quartz(() -> scheduler.checkExists(key)
                ? scheduler.getTriggerState(JobSynchronizer.triggerKey(key)).name()
                : "NOT_SCHEDULED");
    }

    @FunctionalInterface
    private interface QuartzCall<T> {
        T call() throws SchedulerException;
    }

    private static <T> T quartz(QuartzCall<T> call) {
        try {
            return call.call();
        } catch (SchedulerException e) {
            throw new IllegalStateException("Scheduler error: " + e.getMessage(), e);
        }
    }
}
