package com.fxmind.web.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fxmind.domain.JobDefinition;
import com.fxmind.dto.IngestionResult;
import com.fxmind.exception.DataProviderException;
import com.fxmind.service.DataIngestionService;
import com.fxmind.service.JobDefinitionService;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

class SchedulingTest {

    private final JobDefinitionService definitions = mock(JobDefinitionService.class);
    private Scheduler scheduler;

    @BeforeEach
    void createScheduler() throws Exception {
        var properties = new Properties();
        properties.setProperty("org.quartz.scheduler.instanceName", "test-" + System.nanoTime());
        properties.setProperty("org.quartz.threadPool.threadCount", "1");
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        scheduler = new StdSchedulerFactory(properties).getScheduler();
    }

    @AfterEach
    void shutdown() throws Exception {
        scheduler.shutdown();
    }

    @Test
    void synchronizerAddsReschedulesAndRemovesManagedJobs() throws Exception {
        var synchronizer = new JobSynchronizer(scheduler, definitions);
        var ff = new JobDefinition("News", "FF", "forexfactory", "0 0 * * * ?", "ff");
        var oanda = new JobDefinition("Ratio", "Oanda", "oanda", "0 */5 * * * ?", "oanda");
        when(definitions.findEnabled()).thenReturn(List.of(ff, oanda));

        assertThat(synchronizer.synchronize()).isEqualTo(new JobSynchronizer.SyncResult(2, 0, 0, 0));
        assertThat(synchronizer.synchronize()).isEqualTo(new JobSynchronizer.SyncResult(0, 0, 0, 0));

        ff.setCron("0 30 * * * ?");
        when(definitions.findEnabled()).thenReturn(List.of(ff));
        assertThat(synchronizer.synchronize()).isEqualTo(new JobSynchronizer.SyncResult(0, 1, 1, 0));

        var trigger = (CronTrigger) scheduler.getTrigger(JobSynchronizer.triggerKey(JobKey.jobKey("FF", "News")));
        assertThat(trigger.getCronExpression()).isEqualTo("0 30 * * * ?");
        assertThat(scheduler.checkExists(JobKey.jobKey("Oanda", "Ratio"))).isFalse();
    }

    @Test
    void invalidCronIsRecordedWithoutBlockingOtherJobs() throws Exception {
        var synchronizer = new JobSynchronizer(scheduler, definitions);
        var broken = new JobDefinition("News", "Broken", "exness", "not a cron", null);
        var valid = new JobDefinition("Ratio", "Oanda", "oanda", "0 */5 * * * ?", null);
        when(definitions.findEnabled()).thenReturn(List.of(broken, valid));

        assertThat(synchronizer.synchronize()).isEqualTo(new JobSynchronizer.SyncResult(1, 0, 0, 1));
        assertThat(scheduler.checkExists(JobKey.jobKey("Oanda", "Ratio"))).isTrue();
        verify(definitions).recordExecution(eq("News"), eq("Broken"), startsWith("INVALID SCHEDULE"), any(), any());
    }

    @Test
    void jobWhoseCronBecomesInvalidIsUnscheduledInsteadOfFiringOnTheOldCron() throws Exception {
        var synchronizer = new JobSynchronizer(scheduler, definitions);
        var ff = new JobDefinition("News", "FF", "forexfactory", "0 0 * * * ?", null);
        when(definitions.findEnabled()).thenReturn(List.of(ff));
        synchronizer.synchronize();

        ff.setCron("not a cron");
        assertThat(synchronizer.synchronize()).isEqualTo(new JobSynchronizer.SyncResult(0, 0, 0, 1));
        assertThat(scheduler.checkExists(JobKey.jobKey("FF", "News"))).isFalse();
        verify(definitions).recordExecution(eq("News"), eq("FF"), startsWith("INVALID SCHEDULE"), any(), any());

        ff.setCron("0 15 * * * ?");
        assertThat(synchronizer.synchronize()).isEqualTo(new JobSynchronizer.SyncResult(1, 0, 0, 0));
    }

    @Test
    void providerJobRecordsSuccessAndFailureWithoutThrowing() {
        var ingestion = mock(DataIngestionService.class);
        var job = new ProviderJob(definitions, ingestion);
        when(ingestion.run("oanda")).thenReturn(new IngestionResult("oanda", 3, 2, 1));

        job.execute(context("oanda"));
        verify(definitions)
                .recordExecution(eq("Ratio"), eq("Oanda"), startsWith("OK - oanda: fetched 3"), any(), any());

        when(ingestion.run("oanda")).thenThrow(new DataProviderException("oanda", "HTTP 503"));
        job.execute(context("oanda"));
        verify(definitions)
                .recordExecution(eq("Ratio"), eq("Oanda"), startsWith("FAILED - source unavailable"), any(), any());
    }

    @Test
    void supervisorDoesNotRecordOnJobDefinitions() throws Exception {
        var synchronizer = mock(JobSynchronizer.class);
        when(synchronizer.synchronize()).thenReturn(new JobSynchronizer.SyncResult(0, 0, 0, 0));

        new JobSupervisor(definitions, synchronizer).execute(context(null));

        verify(definitions, never()).recordExecution(any(), any(), any(), any(), any());
    }

    @Test
    void schedulerServiceRejectsInvalidCron() {
        var service = new QuartzSchedulerService(scheduler, definitions, new JobSynchronizer(scheduler, definitions));

        assertThatThrownBy(() -> service.updateJob("News", "FF", "every minute", true))
                .isInstanceOf(IllegalArgumentException.class);
        verify(definitions, never()).update(any(), any(), any(), any(Boolean.class));
    }

    private static JobExecutionContext context(String handler) {
        var detail = mock(JobDetail.class);
        when(detail.getKey()).thenReturn(JobKey.jobKey("Oanda", "Ratio"));
        var context = mock(JobExecutionContext.class);
        when(context.getJobDetail()).thenReturn(detail);
        var data = new JobDataMap();
        if (handler != null) {
            data.put(ProviderJob.HANDLER_KEY, handler);
        }
        when(context.getMergedJobDataMap()).thenReturn(data);
        return context;
    }
}
