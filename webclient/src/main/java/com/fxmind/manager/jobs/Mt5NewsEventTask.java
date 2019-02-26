package com.fxmind.manager.jobs;

import com.fxmind.dao.NewsEventDao;
import com.fxmind.service.AdminService;
import com.fxmind.utils.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Parse news events job on http://www.mt5.com/forex_calendar/index/2014/23/timezone_0/
 */
@Component
public class Mt5NewsEventTask extends SystemTask {
	private static final Logger log = LoggerFactory.getLogger(Mt5NewsEventTask.class);

    public static String URLformat = "http://www.mt5.com/forex_calendar/index/%d/%d/timezone_0";

    @Autowired
    protected NewsEventDao newsEventDao;

    @Autowired
    protected AdminService adminService;

    public Mt5NewsEventTask()
    {
        super();
        try {
            // autowire this component to get autowired variables initialized
            ApplicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
        } catch (Exception e) {
            log.error("Failed dynamic autowiring : " + e);
        }
    }

    protected void DoParsing() {
        // DISABLED
/*        Timestamp dateTime = adminService.GetCurrentTimestampUTC();
        LocalDateTime parseDateTime = dateTime.toLocalDateTime();
        String url = String.format(URLformat, parseDateTime.getYear(), parseDateTime.getWeekOfWeekyear());

        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("html body div#main_m div#main-content div#rp-content div.text-block div.calendar div.calendar_table table.cal tbody.caldata");
            if (links != null) {
                statMessage = "Mt5NewsEventTask : links" + links.toString();
                log.info(status());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
         */
    }

	@Override
	public void run() {
		statMessage = "Mt5NewsEventTask started.";
        log.info(status());
        try {
            DoParsing();
            //log.info(status());
            statMessage = "Success.";
            //log.info(status());
        }
        catch (RuntimeException e) {
            statMessage = "Mt5NewsEventTask  Cancelled! " + e.getMessage();
            //log.error(status());
        }
        catch (Exception e) {
            statMessage = "Mt5NewsEventTask. Exception occurred: " + e;
            //log.error(status());
            stop();
        }
	}

}

