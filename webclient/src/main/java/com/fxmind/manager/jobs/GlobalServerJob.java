package com.fxmind.manager.jobs;

import com.fxmind.entity.Settings;
import com.fxmind.exceptions.GenericException;
import com.fxmind.global.FXMindMQLServer;
import com.fxmind.service.AdminService;
import com.fxmind.utils.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by sergeizhuravlev on 5/8/14.
 */
@Component
public class GlobalServerJob extends SystemTask {

    private static final Logger log = LoggerFactory.getLogger(GlobalServerJob.class);

    @Autowired
    AdminService adminService;

    public GlobalServerJob() {
        super();
        try {
            // autowire this component to get autowired variables initialized
            ApplicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
        } catch (Exception e) {
            log.error("Failed to initialise GlobalServerJob task: " + e);
        }
    }

//    @PostConstruct
//    public void init() {
//    }

    @Override
    public void run() {
        try {
            String strPort = adminService.getProperty(Settings.THRIFT_JAVA_SERVER_PORT);
            Short portVal = Short.parseShort(strPort);
            if (portVal <= 0)
                throw new GenericException("Failed to retrieve port number to start GlobalService server!!!");

            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

            statMessage = "GlobalServerJob listening endpoint localhost:" + strPort + " ...";
            log.info(status());

            FXMindMQLServer.start(portVal);

        }
        catch (RuntimeException e) {
            statMessage = "GlobalServerJob Cancelled! " + e.getMessage();
            log.error(status());
        }
        catch (Exception e) {
            statMessage = "GlobalServerJob. Exception occurred: " + e;
            log.error(status());
            stop();
        }
    }

}
