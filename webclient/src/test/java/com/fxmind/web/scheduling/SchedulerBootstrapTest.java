package com.fxmind.web.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fxmind.service.SettingsService;
import java.util.Optional;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

class SchedulerBootstrapTest {

    @Test
    void registersSupervisorOnceUsingConfiguredInterval() throws Exception {
        var properties = new Properties();
        properties.setProperty("org.quartz.scheduler.instanceName", "bootstrap-test");
        properties.setProperty("org.quartz.threadPool.threadCount", "1");
        var scheduler = new StdSchedulerFactory(properties).getScheduler();
        var settings = mock(SettingsService.class);
        when(settings.value(SchedulerBootstrap.SYNC_INTERVAL_SETTING)).thenReturn(Optional.of("30"));
        try {
            var bootstrap = new SchedulerBootstrap(scheduler, settings);
            bootstrap.registerSupervisor();
            bootstrap.registerSupervisor();

            var trigger = (SimpleTrigger) scheduler.getTrigger(TriggerKey.triggerKey("JobSupervisor", "SYSTEM"));
            assertThat(trigger.getRepeatInterval()).isEqualTo(30_000L);
        } finally {
            scheduler.shutdown();
        }
    }
}
