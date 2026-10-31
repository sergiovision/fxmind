package com.fxmind.web.scheduling;

import com.fxmind.service.JobDefinitionService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

/** Periodically reconciles Quartz with the {@code jobs} table so console edits take effect live. */
@DisallowConcurrentExecution
public class JobSupervisor extends AbstractJob {

    private final JobSynchronizer synchronizer;

    public JobSupervisor(JobDefinitionService jobDefinitions, JobSynchronizer synchronizer) {
        super(jobDefinitions);
        this.synchronizer = synchronizer;
    }

    @Override
    protected String doExecute(JobExecutionContext context) throws Exception {
        return synchronizer.synchronize().toString();
    }

    @Override
    protected boolean recordsExecution() {
        return false;
    }
}
