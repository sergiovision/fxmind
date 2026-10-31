package com.fxmind.web.scheduling;

import com.fxmind.exception.DataProviderException;
import com.fxmind.exception.FxMindException;
import com.fxmind.service.JobDefinitionService;
import java.time.Duration;
import java.time.Instant;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Template Method for every Quartz job: {@link #execute} fixes the skeleton (timing, error
 * translation, logging and persisting the outcome on the job definition) while subclasses implement
 * only {@link #doExecute}. Failures never escape to Quartz, so a broken source cannot unschedule a job.
 */
public abstract class AbstractJob implements Job {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JobDefinitionService jobDefinitions;

    protected AbstractJob(JobDefinitionService jobDefinitions) {
        this.jobDefinitions = jobDefinitions;
    }

    @Override
    public final void execute(JobExecutionContext context) {
        var key = context.getJobDetail().getKey();
        var started = Instant.now();
        String status;
        try {
            status = "OK - " + doExecute(context);
        } catch (Exception e) {
            status = "FAILED - " + describe(e);
            log.warn("Job {} failed: {}", key, status, e);
        }
        var elapsed = Duration.between(started, Instant.now());
        status = status + " (" + elapsed.toMillis() + " ms)";
        context.setResult(status);
        log.info("Job {} finished: {}", key, status);
        if (recordsExecution()) {
            var fireTime = context.getFireTime() == null
                    ? started
                    : context.getFireTime().toInstant();
            var next = context.getNextFireTime() == null
                    ? null
                    : context.getNextFireTime().toInstant();
            jobDefinitions.recordExecution(key.getGroup(), key.getName(), status, fireTime, next);
        }
    }

    /** Performs the job's work and returns a short, human-readable result summary. */
    protected abstract String doExecute(JobExecutionContext context) throws Exception;

    /** Whether the outcome is persisted on the matching {@code JobDefinition} row. */
    protected boolean recordsExecution() {
        return true;
    }

    static String describe(Exception e) {
        return switch (e) {
            case DataProviderException provider -> "source unavailable: " + provider.getMessage();
            case FxMindException business -> business.getMessage();
            case IllegalArgumentException invalid -> "invalid configuration: " + invalid.getMessage();
            default -> e.getClass().getSimpleName() + ": " + e.getMessage();
        };
    }
}
