package com.fxmind.web.ui.view;

import com.fxmind.dto.NewsEventDto;
import com.fxmind.service.JobDefinitionService;
import com.fxmind.service.NewsEventService;
import com.fxmind.service.PersonService;
import com.fxmind.web.scheduling.SchedulerService;
import com.fxmind.web.ui.Formats;
import com.fxmind.web.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/** At-a-glance health of data ingestion and the user base. */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | FXMind")
@RolesAllowed("ADMIN")
public class DashboardView extends VerticalLayout {

    private static final int PREVIEW_ROWS = 10;

    public DashboardView(
            NewsEventService news, PersonService persons, JobDefinitionService jobs, SchedulerService scheduler) {
        var allJobs = jobs.findAll();
        var enabledJobs = allJobs.stream().filter(job -> job.isEnabled()).count();

        var cards = new FlexLayout(
                card("Upcoming events", Long.toString(news.countUpcoming())),
                card("Registered users", Long.toString(persons.count())),
                card("Active jobs", enabledJobs + " / " + allJobs.size()),
                card("Scheduler", scheduler.isPaused() ? "Paused" : "Running"));
        cards.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        cards.addClassNames(LumoUtility.Gap.MEDIUM);

        var upcoming = new Grid<NewsEventDto>();
        upcoming.addColumn(event -> Formats.utc(event.happenTime()))
                .setHeader("Time")
                .setAutoWidth(true);
        upcoming.addColumn(NewsEventDto::currency).setHeader("Currency").setAutoWidth(true);
        upcoming.addColumn(NewsEventDto::importance).setHeader("Impact").setAutoWidth(true);
        upcoming.addColumn(NewsEventDto::name).setHeader("Event").setFlexGrow(1);
        upcoming.setItems(news.upcoming(PageRequest.of(0, PREVIEW_ROWS, Sort.by("happenTime")))
                .getContent());
        upcoming.setAllRowsVisible(true);

        add(new H2("Overview"), cards, new H2("Next events"), upcoming);
        setPadding(true);
    }

    private static Component card(String label, String value) {
        var number = new Span(value);
        number.addClassNames(LumoUtility.FontSize.XXLARGE, LumoUtility.FontWeight.SEMIBOLD);
        var caption = new Span(label);
        caption.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
        var card = new Div(number, caption);
        card.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Background.CONTRAST_5);
        card.setMinWidth("12rem");
        return card;
    }
}
