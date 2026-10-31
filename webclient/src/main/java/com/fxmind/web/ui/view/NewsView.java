package com.fxmind.web.ui.view;

import com.fxmind.dto.NewsEventDto;
import com.fxmind.service.NewsEventService;
import com.fxmind.web.ui.Formats;
import com.fxmind.web.ui.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/** Lazily-loaded economic calendar of upcoming events. */
@Route(value = "news", layout = MainLayout.class)
@PageTitle("News calendar | FXMind")
@RolesAllowed("ADMIN")
public class NewsView extends VerticalLayout {

    public NewsView(NewsEventService news) {
        var grid = new Grid<NewsEventDto>();
        grid.addColumn(event -> Formats.utc(event.happenTime()))
                .setHeader("Time")
                .setAutoWidth(true);
        grid.addColumn(NewsEventDto::currency).setHeader("Currency").setAutoWidth(true);
        grid.addColumn(NewsEventDto::importance).setHeader("Impact").setAutoWidth(true);
        grid.addColumn(NewsEventDto::name).setHeader("Event").setFlexGrow(1);
        grid.addColumn(NewsEventDto::actualValue).setHeader("Actual").setAutoWidth(true);
        grid.addColumn(NewsEventDto::forecastValue).setHeader("Forecast").setAutoWidth(true);
        grid.addColumn(NewsEventDto::previousValue).setHeader("Previous").setAutoWidth(true);
        grid.addColumn(event -> Formats.utc(event.parsedAt()))
                .setHeader("Imported")
                .setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.setItems(query ->
                news
                        .upcoming(PageRequest.of(query.getPage(), query.getPageSize(), Sort.by("happenTime", "id")))
                        .stream());
        grid.setSizeFull();

        add(new H2("Upcoming economic events"), grid);
        setSizeFull();
    }
}
