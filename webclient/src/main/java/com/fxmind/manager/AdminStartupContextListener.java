package com.fxmind.manager;

import com.fxmind.service.SchedulerService;
import com.fxmind.utils.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sergeizhuravlev on 5/28/14.
 */
public class AdminStartupContextListener extends org.springframework.web.context.ContextLoaderListener {

    private static final Logger log = LoggerFactory.getLogger(AdminStartupContextListener.class);


    @Autowired
    protected SchedulerService schedulerService;

    @Override
    public void contextInitialized(javax.servlet.ServletContextEvent event) {
        super.contextInitialized(event);
        // autowire this component to get autowired variables initialized
        ApplicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);

        log.info("!!!!!!!!!!! AdminStartupContextListener.contextInitialized: Init Scheduler !!!!!!!!!!!!!!!!!!");
        schedulerService.startScheduler();
    }

    @Override
    public void contextDestroyed(javax.servlet.ServletContextEvent event) {

        log.info("!!!!!!!!!!! AdminStartupContextListener.contextDestroyed: Stop Scheduler !!!!!!!!!!!!!!!!!!");

        schedulerService.stopScheduler();

        super.contextDestroyed(event);
    }
}
