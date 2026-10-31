package com.fxmind.web.scheduling;

import com.fxmind.service.DataIngestionService;
import com.fxmind.service.JobDefinitionService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

/** Runs the {@code DataProvider} named by the job definition's handler and stores its output. */
@DisallowConcurrentExecution
public class ProviderJob extends AbstractJob {

    static final String HANDLER_KEY = "handler";

    private final DataIngestionService ingestion;

    public ProviderJob(JobDefinitionService jobDefinitions, DataIngestionService ingestion) {
        super(jobDefinitions);
        this.ingestion = ingestion;
    }

    @Override
    protected String doExecute(JobExecutionContext context) {
        return ingestion
                .run(context.getMergedJobDataMap().getString(HANDLER_KEY))
                .summary();
    }
}
