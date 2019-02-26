package com.fxmind.manager.view;

import com.fxmind.manager.quartz.SchedulerServiceImpl;
import com.fxmind.quartz.JobDescription;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import ru.xpoft.vaadin.VaadinView;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(ScheduledJobsView.VIEW_NAME)
public class ScheduledJobsView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "scheduled-jobs";

    private static final String COL_SCHEDULED_NAME = "Name";
    private static final String COL_SCHEDULED_GROUP = "Group";
    private static final String COL_SCHEDULED_DESCRIPTION = "Description";
    private static final String COL_SCHEDULED_MESSAGE = "Status";
    private static final String COL_SCHEDULED_CRON = "Cron";
    private static final String COL_SCHEDULED_PREV_TIME = "Last Time";
    private static final String COL_SCHEDULED_NEXT_TIME = "Next Time";
    private static final String COL_SCHEDULED_RUN_NOW = "Action";

    private static final String COL_RUNNING_MESSAGE = "Result";
    private static final String COL_RUNNING_PREV_TIME = "Fire Time";
    private static final String COL_RUNNING_STOP_NOW = "Action";

    private static final long serialVersionUID = 1L;

    @Autowired
    protected SchedulerServiceImpl scheduler;

    protected Table schedTable;
    protected Table runningTable;

    public void LoadScheduledJobsList()
    {
        schedTable.removeAllItems();

        List<JobDetail> jobs = scheduler.getScheduledJobs();

        for (JobDetail jd: jobs) {

            JobDescription job = scheduler.getJobDescription(jd.getKey().getGroup(), jd.getKey().getName());

            Button runJobButton = new NativeButton("Run Now");
            runJobButton.addStyleName("small");
            //runJobButton.setStyleName("job-run-button");
            runJobButton.setData(jd);

            runJobButton.addClickListener(event -> {
                JobDetail selectedJob = (JobDetail) event.getButton().getData();
                if (selectedJob != null) {
                    scheduler.triggerNow(selectedJob);
                    LoadRunningJobsList();
                }
            });

            Trigger trigger = scheduler.getFirstTrigger(jd.getKey());
            Date firstDate = Calendar.getInstance().getTime();
            Date nextDate = Calendar.getInstance().getTime();
            if (trigger!=null) {
                if (job != null) {
                    Timestamp prevDate = job.lastExecutionDate();
                    if ( prevDate != null)
                    firstDate = new Date(prevDate.getTime());
                } else
                    firstDate = trigger.getPreviousFireTime();
                nextDate = trigger.getNextFireTime();
            }

            if (job != null)
                schedTable.addItem(new Object[]{ runJobButton,
                        job.group(),
                        job.name(), job.jobDescription(), job.jobParams().get("STATMESSAGE"), job.cronExpression(),
                        firstDate, nextDate },
                        job);
            else
                schedTable.addItem(new Object[]{runJobButton,
                        jd.getKey().getGroup(), jd.getKey().getName(), jd.getDescription(), "", "",
                        firstDate, nextDate},
                        job);

        }

        schedTable.sort(new Object[]{COL_SCHEDULED_NAME}, new boolean[]{false});

        schedTable.setColumnWidth(COL_SCHEDULED_GROUP, 60);
        schedTable.setColumnWidth(COL_SCHEDULED_RUN_NOW, 100);
        schedTable.setColumnWidth(COL_SCHEDULED_CRON, 100);
        schedTable.setColumnWidth(COL_SCHEDULED_PREV_TIME, 180);
        schedTable.setColumnWidth(COL_SCHEDULED_NEXT_TIME, 180);
        schedTable.setColumnWidth(COL_SCHEDULED_NAME, 200);
        schedTable.setColumnWidth(COL_SCHEDULED_DESCRIPTION, 400);

        schedTable.setWidth("100%");
    }

    protected void InitScheduledTable() {
        schedTable = new Table();
        schedTable.setCaption("Scheduled jobs");
        schedTable.setSizeFull();
        schedTable.setNullSelectionAllowed(false);
        schedTable.setImmediate(true);
        //schedTable.addStyleName("plain");
        schedTable.addStyleName("borderless");
        schedTable.setSelectable(true);
        schedTable.setColumnCollapsingAllowed(true);
        schedTable.setColumnReorderingAllowed(true);
        schedTable.setFooterVisible(false);
        schedTable.setMultiSelect(false);

        //schedTable.setPageLength(0);
        //schedTable.setRowHeaderMode(Table.RowHeaderMode.INDEX);

        schedTable.addContainerProperty(COL_SCHEDULED_RUN_NOW, Button.class, null, COL_SCHEDULED_RUN_NOW, null, Table.Align.CENTER);
        schedTable.addContainerProperty(COL_SCHEDULED_GROUP, String.class, null, COL_SCHEDULED_GROUP, null, Table.Align.RIGHT);
        schedTable.addContainerProperty(COL_SCHEDULED_NAME, String.class, null, COL_SCHEDULED_NAME, null, Table.Align.LEFT);
        schedTable.addContainerProperty(COL_SCHEDULED_DESCRIPTION, String.class, null, COL_SCHEDULED_DESCRIPTION, null, Table.Align.LEFT);
        schedTable.addContainerProperty(COL_SCHEDULED_MESSAGE, String.class, null, COL_SCHEDULED_MESSAGE, null, Table.Align.LEFT);
        schedTable.addContainerProperty(COL_SCHEDULED_CRON, String.class, null, COL_SCHEDULED_CRON, null, Table.Align.LEFT);
        schedTable.addContainerProperty(COL_SCHEDULED_PREV_TIME, Date.class, null, COL_SCHEDULED_PREV_TIME, null, Table.Align.RIGHT);
        schedTable.addContainerProperty(COL_SCHEDULED_NEXT_TIME, Date.class, null, COL_SCHEDULED_NEXT_TIME, null, Table.Align.RIGHT);

    }

    protected void InitRunningTable() {
        runningTable = new Table();
        runningTable.setCaption("Running jobs");
        runningTable.setSizeFull();
        runningTable.setNullSelectionAllowed(false);
        runningTable.setImmediate(true);
        //propTable.addStyleName("plain");
        runningTable.addStyleName("borderless");
        runningTable.setSelectable(true);
        runningTable.setColumnCollapsingAllowed(true);
        runningTable.setColumnReorderingAllowed(true);
        runningTable.setFooterVisible(false);
        runningTable.setMultiSelect(false);

        runningTable.setPageLength(0);
        //propTable.setRowHeaderMode(Table.RowHeaderMode.INDEX);

        runningTable.addContainerProperty(COL_RUNNING_STOP_NOW, Button.class, null, COL_RUNNING_STOP_NOW, null, Table.Align.CENTER);
        runningTable.addContainerProperty(COL_SCHEDULED_GROUP, String.class, null, COL_SCHEDULED_GROUP, null, Table.Align.RIGHT);
        runningTable.addContainerProperty(COL_SCHEDULED_NAME, String.class, null, COL_SCHEDULED_NAME, null, Table.Align.LEFT);
        runningTable.addContainerProperty(COL_RUNNING_MESSAGE, String.class, null, COL_RUNNING_MESSAGE, null, Table.Align.LEFT);
        runningTable.addContainerProperty(COL_RUNNING_PREV_TIME, Date.class, null, COL_RUNNING_PREV_TIME, null, Table.Align.RIGHT);

    }

    public void LoadRunningJobsList()
    {
        runningTable.removeAllItems();

        List<JobExecutionContext> jobs = scheduler.getRunningJobs();
        if (jobs == null)
            return;

        for ( JobExecutionContext jc: jobs) {

            Button stopJobButton = new NativeButton("Stop Now");
            stopJobButton.addStyleName("small");
            stopJobButton.setData(jc);

            stopJobButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    JobExecutionContext selectedJob = (JobExecutionContext) event.getButton().getData();
                    if (selectedJob != null) {
                        scheduler.stopJob(selectedJob);
                    }
                }
            });

            Date firstDate = jc.getScheduledFireTime();

            JobDetail jd = jc.getJobDetail();
            String result = "";
            Object oResult = jc.getResult();
            if (oResult!=null)
                result = oResult.toString();
            runningTable.addItem(new Object[] {stopJobButton,
                        jd.getKey().getGroup(), jd.getKey().getName(), result,
                        firstDate},
                        jc);
        }

        runningTable.sort(new Object[]{COL_RUNNING_PREV_TIME}, new boolean[]{false});
        runningTable.setColumnWidth(COL_SCHEDULED_GROUP, 60);
        runningTable.setColumnWidth(COL_RUNNING_STOP_NOW, 100);
        runningTable.setColumnWidth(COL_SCHEDULED_NAME, 200);
        runningTable.setWidth("100%");
        getUI().push();
    }

    public void RefreshJobs() {
        LoadRunningJobsList();
        LoadScheduledJobsList();
    }

    @Override
    public void enter(ViewChangeEvent event) {

        setSizeFull();
        addStyleName("dashboard-view");

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");
        toolbar.setSpacing(true);
        toolbar.setMargin(true);
        toolbar.addStyleName("toolbar");
        addComponent(toolbar);

        Label title = new Label("Jobs");
        title.addStyleName("h1");
        title.setSizeUndefined();
        toolbar.addComponent(title);
        toolbar.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

        Button refresh = new Button("Refresh");
        refresh.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                RefreshJobs();
            }
        });
        refresh.addStyleName("h1");
        toolbar.addComponent(refresh);
        toolbar.setComponentAlignment(refresh, Alignment.MIDDLE_LEFT);

        HorizontalLayout rowRunning = new HorizontalLayout();
        rowRunning.setSizeFull();
        rowRunning.setMargin(new MarginInfo(true, true, false, true));
        rowRunning.setSpacing(true);
        addComponent(rowRunning);
        setExpandRatio(rowRunning, 1.5f);

        InitRunningTable();
        LoadRunningJobsList();
        rowRunning.addComponent(createPanel(runningTable));

        HorizontalLayout rowScheduled = new HorizontalLayout();
        rowScheduled.setMargin(true);
        rowScheduled.setMargin(new MarginInfo(true, true, false, true));
        rowScheduled.setSizeFull();
        rowScheduled.setSpacing(true);
        addComponent(rowScheduled);
        setExpandRatio(rowScheduled, 2);

        InitScheduledTable();
        LoadScheduledJobsList();

        rowScheduled.addComponent(createPanel(schedTable));

        getUI().push();
    }

    private CssLayout createPanel(Component content) {
        CssLayout panel = new CssLayout();
        panel.addStyleName("layout-panel");
        panel.setSizeFull();
        panel.addComponent(content);
        return panel;
    }

}
