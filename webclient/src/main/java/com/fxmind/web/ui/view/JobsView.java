package com.fxmind.web.ui.view;

import com.fxmind.exception.FxMindException;
import com.fxmind.web.scheduling.ScheduledJobView;
import com.fxmind.web.scheduling.SchedulerService;
import com.fxmind.web.ui.Formats;
import com.fxmind.web.ui.MainLayout;
import com.fxmind.web.ui.Notifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/** Scheduled data-import jobs: state, last result, manual trigger and cron editing. */
@Route(value = "jobs", layout = MainLayout.class)
@PageTitle("Scheduled jobs | FxHubJava")
@RolesAllowed("ADMIN")
public class JobsView extends VerticalLayout {

    private final SchedulerService scheduler;
    private final Grid<ScheduledJobView> grid = new Grid<>();
    private final Button pauseResume = new Button();

    public JobsView(SchedulerService scheduler) {
        this.scheduler = scheduler;

        grid.addColumn(ScheduledJobView::group).setHeader("Group").setAutoWidth(true);
        grid.addColumn(ScheduledJobView::name).setHeader("Job").setAutoWidth(true);
        grid.addColumn(ScheduledJobView::cron).setHeader("Cron").setAutoWidth(true);
        grid.addColumn(job -> job.running() ? "RUNNING" : job.state())
                .setHeader("State")
                .setAutoWidth(true);
        grid.addColumn(job -> Formats.utc(job.previousFireTime()))
                .setHeader("Last run")
                .setAutoWidth(true);
        grid.addColumn(job -> Formats.utc(job.nextFireTime()))
                .setHeader("Next run")
                .setAutoWidth(true);
        grid.addColumn(ScheduledJobView::lastStatus).setHeader("Last result").setFlexGrow(1);
        grid.addComponentColumn(this::actions).setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        pauseResume.addClickListener(click -> togglePause());
        var refresh = new Button("Refresh", VaadinIcon.REFRESH.create(), click -> refresh());
        add(new HorizontalLayout(new H2("Scheduled jobs"), refresh, pauseResume), grid);
        setSizeFull();
        refresh();
    }

    private HorizontalLayout actions(ScheduledJobView job) {
        var run = new Button(VaadinIcon.PLAY.create(), click -> runNow(job));
        run.setTooltipText("Run now");
        run.setEnabled(job.enabled());
        run.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        var edit = new Button(VaadinIcon.EDIT.create(), click -> edit(job));
        edit.setTooltipText("Edit schedule");
        edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return new HorizontalLayout(run, edit);
    }

    private void runNow(ScheduledJobView job) {
        try {
            scheduler.runNow(job.group(), job.name());
            Notifications.success(job.name() + " triggered");
        } catch (IllegalArgumentException | IllegalStateException e) {
            Notifications.error(e.getMessage());
        }
    }

    private void edit(ScheduledJobView job) {
        var cron = new TextField("Quartz cron expression", job.cron(), "");
        cron.setHelperText("sec min hour day-of-month month day-of-week [year]");
        cron.setWidthFull();
        var enabled = new Checkbox("Enabled", job.enabled());
        var dialog = new Dialog("Edit " + job.group() + " / " + job.name());
        dialog.add(new FormLayout(cron, enabled));
        var save = new Button("Save", click -> {
            try {
                scheduler.updateJob(job.group(), job.name(), cron.getValue().strip(), enabled.getValue());
                Notifications.success("Schedule updated");
                dialog.close();
                refresh();
            } catch (IllegalArgumentException | IllegalStateException | FxMindException e) {
                cron.setInvalid(true);
                cron.setErrorMessage(e.getMessage());
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(new Button("Cancel", click -> dialog.close()), save);
        dialog.open();
    }

    private void togglePause() {
        if (scheduler.isPaused()) {
            scheduler.resume();
        } else {
            scheduler.pause();
        }
        refresh();
    }

    private void refresh() {
        grid.setItems(scheduler.listJobs());
        var paused = scheduler.isPaused();
        pauseResume.setText(paused ? "Resume scheduler" : "Pause scheduler");
        pauseResume.setIcon(paused ? VaadinIcon.PLAY_CIRCLE.create() : VaadinIcon.PAUSE.create());
    }
}
