package com.fxmind.manager.jobs;

import com.fxmind.dao.CurrencyDao;
import com.fxmind.dao.NewsEventDao;
import com.fxmind.dao.SymbolDao;
import com.fxmind.entity.Newsevent;
import com.fxmind.service.AdminService;
import com.fxmind.utils.ApplicationContextProvider;
import com.fxmind.utils.StringCleanUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**

 http://www.forexfactory.com/ffcal_week_this.xml

 */
@Component
public class ForexFactoryNewsJob extends SystemTask {
	private static final Logger log = LoggerFactory.getLogger(ForexFactoryNewsJob.class);

    public static String DATEFORMAT = "MM-dd-yyyy";
    public static String TIMEFORMAT = "h:mm a";
    public static String SHORTDATETIMEFORMAT = "yyyy-M-d";
    protected String mCheckQuery;
    protected Timestamp parseDateTime;
    public static String url = "http://www.forexfactory.com/ffcal_week_this.xml";
    public int eventsAdded = 0;

    @Autowired
    protected SymbolDao symbolDao;

    @Autowired
    protected CurrencyDao currencyDao;

    @Autowired
    protected AdminService adminService;

    @Autowired
    protected NewsEventDao newsEventDao;

    public ForexFactoryNewsJob()
    {
        super();
        try {
            // autowire this component to get autowired variables initialized
            ApplicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
        } catch (Exception e) {
            log.error("Failed dynamic autowiring : " + e);
        }
    }

	@Override
	public void run() {
        try {
            parseDateTime = adminService.GetCurrentTimestampUTC();
            LocalDate curDate = parseDateTime.toLocalDateTime().toLocalDate();
            LocalDate nowDate = parseDateTime.toLocalDateTime().toLocalDate();
            Document document = null;
            //document = Jsoup.connect(url).get();
            Connection con = Jsoup.connect(url);
            document = con.parser(Parser.xmlParser()).get();//Jsoup.parse("", url, Parser.xmlParser());
            ParseOnePage(document, LocalDateTime.of(curDate, LocalTime.MIDNIGHT));
            statMessage = "Success";
        }
        catch (RuntimeException e) {
            statMessage = "Cancelled: " + e.getMessage();
            //log.error(status());
        }
        catch (Exception e) {
            statMessage = "Exception: " + e;
            //log.error(status());
            stop();
        }
	}

    private boolean ParseOnePage(Document document, LocalDateTime curDateTime) {

        eventsAdded = 0;
        Newsevent eventRow = null;
        for (Element el : document.select("event")) {
            try {

                eventRow = new Newsevent();
                eventRow.setIndicatorvalue(0.0);
                eventRow.setParsetime(parseDateTime);

                Elements eTitle = el.select("title");
                String eventName = eTitle.first().text();
                eventRow.setName(eventName);

                Elements eCountry = el.select("country");
                String Curr = eCountry.first().text();
                if (!StringCleanUtils.stringIsNullOrEmpty(Curr)) {
                    if (Curr.equalsIgnoreCase("ALL"))
                        eventRow.setCurrencyid(currencyDao.findByName("USD"));
                    else
                        eventRow.setCurrencyid(currencyDao.findByName(Curr));
                }

                Elements eDate = el.select("date");
                String Date = eDate.first().text();
                LocalDate curDate = LocalDate.parse(Date, DateTimeFormatter.ofPattern(DATEFORMAT));

                Elements eTime = el.select("time");
                String Time = eTime.first().text();
                int i = Time.indexOf('a');
                if (i <= 0)
                    i = Time.indexOf('p');
                Time = new StringBuilder(Time).insert(i, " ").toString();
                Time = Time.toUpperCase();
                LocalTime currentTime = LocalTime.parse(Time, DateTimeFormatter.ofPattern(TIMEFORMAT).withLocale(Locale.US));

                curDateTime = LocalDateTime.of(curDate.getYear(), curDate.getMonth(), curDate.getDayOfMonth(),
                        currentTime.getHour(), currentTime.getMinute(), 0);

                eventRow.setHappentime(Timestamp.valueOf(curDateTime));

                Elements eImpact = el.select("impact");
                String Impact = eImpact.first().text();
                eventRow.setImportance((byte) 0);
                switch (Impact) {
                    case "Low":
                        eventRow.setImportance((byte)0);
                        break;
                    case "Medium":
                        eventRow.setImportance((byte)1);
                        break;
                    case "High":
                        eventRow.setImportance((byte)2);
                        break;
                }

                Elements eForecast = el.select("forecast");
                String Forecast = eForecast.first().text();
                eventRow.setForecastval(Forecast);

                Elements ePrevious = el.select("previous");
                String Previous = ePrevious.first().text();
                eventRow.setPreviousval(Previous);

                // save event
                int count = newsEventDao.countEventsOnCurrencyDate(eventRow.getCurrencyid().getId(), curDateTime, eventRow.getImportance());
                if (count > 0)
                    continue;
                if (parseDateTime.compareTo(eventRow.getHappentime()) >= 0)
                    eventRow.setRaised(true);
                else
                    eventRow.setRaised(false);

                eventsAdded++;

                newsEventDao.saveEvent(eventRow);

            } catch (Exception e) {
                log.info("Error parsing news event: " + el.text() + "Error: " + e.toString());
                continue;
            }
        }
        statMessage = "Events for End Date: " + curDateTime + " done. Added " + eventsAdded + " events.";
        log.info(status());
        return true;
    }

}

