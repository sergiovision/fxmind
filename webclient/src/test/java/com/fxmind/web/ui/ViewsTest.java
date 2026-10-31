package com.fxmind.web.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fxmind.domain.Importance;
import com.fxmind.domain.JobDefinition;
import com.fxmind.domain.Privilege;
import com.fxmind.domain.Setting;
import com.fxmind.dto.NewsEventDto;
import com.fxmind.dto.PersonDto;
import com.fxmind.service.JobDefinitionService;
import com.fxmind.service.NewsEventService;
import com.fxmind.service.PersonService;
import com.fxmind.service.SettingsService;
import com.fxmind.web.scheduling.ScheduledJobView;
import com.fxmind.web.scheduling.SchedulerService;
import com.fxmind.web.ui.view.DashboardView;
import com.fxmind.web.ui.view.JobsView;
import com.fxmind.web.ui.view.LoginView;
import com.fxmind.web.ui.view.NewsView;
import com.fxmind.web.ui.view.PersonsView;
import com.fxmind.web.ui.view.SettingsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.internal.CurrentInstance;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.SliceImpl;

/** Server-side smoke tests: every view builds its component tree from (mocked) services. */
class ViewsTest {

    private static final Instant NOW = Instant.parse("2026-10-31T10:00:00Z");

    /**
     * Vaadin keeps the current UI in a weak reference, so the test must hold a strong one; otherwise
     * a GC between tests clears it and component constructors fail with "No currently active UI".
     */
    private UI ui;

    @BeforeEach
    void bindUi() {
        CurrentInstance.clearAll();
        ui = new UI();
        UI.setCurrent(ui);
    }

    @AfterEach
    void unbindUi() {
        CurrentInstance.clearAll();
        ui = null;
    }

    @Test
    void dashboardAndNewsRenderUpcomingEvents() {
        var news = mock(NewsEventService.class);
        var event = new NewsEventDto(1L, "USD", "NFP", Importance.HIGH, NOW, null, "120K", "254K", NOW, false);
        when(news.upcoming(any())).thenReturn(new SliceImpl<>(List.of(event)));
        when(news.countUpcoming()).thenReturn(1L);
        var jobs = mock(JobDefinitionService.class);
        when(jobs.findAll()).thenReturn(List.of(new JobDefinition("News", "FF", "forexfactory", "0 0 * * * ?", null)));

        assertThat(grids(new DashboardView(news, mock(PersonService.class), jobs, mock(SchedulerService.class))))
                .isEqualTo(1);
        assertThat(grids(new NewsView(news))).isEqualTo(1);
    }

    @Test
    void administrationViewsListTheirItems() {
        var persons = mock(PersonService.class);
        when(persons.findAll())
                .thenReturn(List.of(
                        new PersonDto(1L, "a@example.com", Privilege.ADMIN, BigDecimal.ONE, true, NOW, null, null)));
        var settings = mock(SettingsService.class);
        when(settings.findAll()).thenReturn(List.of(new Setting("db.version", "2", "schema revision")));
        var scheduler = mock(SchedulerService.class);
        when(scheduler.listJobs())
                .thenReturn(List.of(new ScheduledJobView(
                        "News", "FF", "forexfactory", null, "0 0 * * * ?", true, "NORMAL", false, "OK", NOW, NOW)));

        assertThat(grids(new PersonsView(persons))).isEqualTo(1);
        assertThat(grids(new SettingsView(settings))).isEqualTo(1);
        assertThat(grids(new JobsView(scheduler))).isEqualTo(1);
    }

    @Test
    void loginAndFormattingHelpers() {
        assertThat(new LoginView().isOpened()).isTrue();
        assertThat(Formats.utc(NOW)).isEqualTo("2026-10-31 10:00 UTC");
        assertThat(Formats.utc(null)).isEmpty();
    }

    private static long grids(Component root) {
        return root.getChildren()
                .flatMap(child -> child instanceof Grid<?> ? java.util.stream.Stream.of(child) : child.getChildren())
                .filter(Grid.class::isInstance)
                .count();
    }
}
