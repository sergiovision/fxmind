package com.fxmind.manager.jobs;

import com.fxmind.dao.CurrencyDao;
import com.fxmind.dao.NewsEventDao;
import com.fxmind.dao.SymbolDao;
import com.fxmind.entity.Newsevent;
import com.fxmind.entity.Settings;
import com.fxmind.service.AdminService;
import com.fxmind.utils.ApplicationContextProvider;
import com.fxmind.utils.StringCleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**

 https://www.exness.com/intl/en/tools/calendar

 */
@Component
public class ExnessNewsJob extends SystemTask {
	private static final Logger log = LoggerFactory.getLogger(ExnessNewsJob.class);

    public static String DATEFORMAT = "dd MMM yyyy";
    public static String TIMEFORMAT = "HH:mm";
    public static String SHORTDATETIMEFORMAT = "yyyy-M-d";
    protected String mCheckQuery;
    protected Timestamp parseDateTime;
    public static String url = "https://www.exness.com/intl/en/tools/calendar";

    public static final String stripChars = " \n\t\"\'";

    @Autowired
    protected SymbolDao symbolDao;

    @Autowired
    protected CurrencyDao currencyDao;

    @Autowired
    protected AdminService adminService;

    @Autowired
    protected NewsEventDao newsEventDao;

    public ExnessNewsJob()
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
            boolean parseAllHistory = false;
            String strParseHistory = adminService.getProperty(Settings.NEWS_PARSE_HISTORY);
            Document document = null;
            if (strParseHistory != null)
            {
                parseAllHistory = Boolean.parseBoolean(strParseHistory);
            }
            if (parseAllHistory) {
                String strParseHistoryStartDate = adminService.getProperty(Settings.NEWS_HISTORY_STARTDATE);
                String strParseHistoryEndDate = adminService.getProperty(Settings.NEWS_HISTORY_ENDDATE);
                if (strParseHistoryStartDate == null)
                {
                    strParseHistoryStartDate = curDate.format(DateTimeFormatter.ofPattern(SHORTDATETIMEFORMAT));
                }
                if (strParseHistoryEndDate == null)
                {
                    strParseHistoryEndDate = curDate.format(DateTimeFormatter.ofPattern(SHORTDATETIMEFORMAT));
                }

                document = postRequest(strParseHistoryStartDate);
                curDate = LocalDate.parse(strParseHistoryStartDate, DateTimeFormatter.ofPattern(SHORTDATETIMEFORMAT));
                LocalDate endDate = LocalDate.parse(strParseHistoryEndDate, DateTimeFormatter.ofPattern(SHORTDATETIMEFORMAT));

                do
                {
                    ParseOnePage(document, LocalDateTime.of(curDate, LocalTime.MIDNIGHT));
                    curDate = curDate.plusDays(7);
                    document = postRequest(curDate.format(DateTimeFormatter.ofPattern(SHORTDATETIMEFORMAT)));
                    if (document == null)
                        break;
                } while ((curDate.compareTo(nowDate) <=0) && (curDate.compareTo(endDate) <=0 ));
            }
            else
            {
                document = Jsoup.connect(url + "/?SortField=DateTime&SortUpDown=down").get();
                ParseOnePage(document, LocalDateTime.of(curDate, LocalTime.MIDNIGHT));
            }
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
        Elements colEvents = document.getElementsByAttributeValue("class", "ui-table economyCalendar-table");
        //getElementsByAttributeValueStarting("class", "economyCalendar-td economyCalendar-td");
        Element rootTable = colEvents.first();
        if ( rootTable == null ) {
            statMessage = "Error parsing Events page";
        }
        LocalDate curDate = curDateTime.toLocalDate();
        int i = 0;
        int TimeOfDay = -1;
        int dayTime = 0;
        colEvents = rootTable.getElementsByAttributeValueStarting("class", "economyCalendar-td economyCalendar-td");
        Newsevent eventRow = null;
        for (Element eventNode : colEvents) {
            i++;
            if (i<=8)
                continue;
            try {
                switch (eventNode.className()) {
                    case "economyCalendar-td economyCalendar-td__1":
                        // create event
                        eventRow = new Newsevent();
                        eventRow.setIndicatorvalue(0.0);
                        eventRow.setParsetime(parseDateTime);

                        String trimDate = StringUtils.strip(eventNode.text(), stripChars);
                        if (!StringCleanUtils.stringIsNullOrEmpty(trimDate)) {
                            trimDate += " " + curDateTime.getYear();
                            curDate = LocalDate.parse(trimDate, DateTimeFormatter.ofPattern(DATEFORMAT));
                            TimeOfDay = -1;
                            dayTime = 0;
                        }
                        break;
                    case "economyCalendar-td economyCalendar-td__2":
                        LocalTime currentTime;
                        String trimTime = StringUtils.strip(eventNode.text(), stripChars);
                        if (!StringCleanUtils.stringIsNullOrEmpty(trimTime)) {
                            currentTime = LocalTime.parse(trimTime, DateTimeFormatter.ofPattern(TIMEFORMAT));
                            if ((TimeOfDay == -1) && (dayTime==0) && currentTime.getHour() < 12)
                               dayTime = 1;

                            if (TimeOfDay >= 0 && TimeOfDay > currentTime.getHour())
                                dayTime++;
                            if ((dayTime==0) && currentTime.getHour() >= 12) {
                                TimeOfDay = currentTime.getHour();
                                currentTime = currentTime.minusHours(12);
                            } else if ((currentTime.getHour() < 12) &&  (TimeOfDay > 0)) {
                                TimeOfDay = currentTime.getHour();
                                if (dayTime == 2) {
                                    currentTime = currentTime.plusHours(12);
                                }
                            } else {
                                TimeOfDay = currentTime.getHour();
                            }
                            curDateTime = LocalDateTime.of(curDate.getYear(), curDate.getMonth(), curDate.getDayOfMonth(),
                                    currentTime.getHour(), currentTime.getMinute(), 0);
                        }
                        eventRow.setHappentime(Timestamp.valueOf(curDateTime));
                        break;
                    case "economyCalendar-td economyCalendar-td__3":
                        String trimCurr = StringUtils.strip(eventNode.text(), stripChars);
                        if (!StringCleanUtils.stringIsNullOrEmpty(trimCurr)) {
                            eventRow.setCurrencyid(currencyDao.findByName(trimCurr));
                        }
                        break;
                    case "economyCalendar-td economyCalendar-td__4":
                        String eventName = eventNode.text();
                        eventName = org.apache.commons.lang.StringEscapeUtils.escapeHtml(eventName);
                        eventName = StringUtils.strip(eventName, stripChars);
                        eventRow.setName(eventName);
                        break;
                    case "economyCalendar-td economyCalendar-td__5":
                            String eventImportance = StringUtils.strip(eventNode.text(), stripChars).toLowerCase();
                            eventRow.setImportance((byte) 0);
                            switch (eventImportance) {
                                case "low":
                                    eventRow.setImportance((byte)0);
                                    break;
                                case "medium":
                                    eventRow.setImportance((byte)1);
                                    break;
                                case "high":
                                    eventRow.setImportance((byte)2);
                                    break;
                            }
                        break;
                    case "economyCalendar-td economyCalendar-td__6":
                        String eventActual = StringUtils.strip(eventNode.text(), stripChars);
                        eventRow.setActualval(eventActual);
                        break;
                    case "economyCalendar-td economyCalendar-td__7":
                        String eventForecast = StringUtils.strip(eventNode.text(), stripChars);
                        eventRow.setForecastval(eventForecast);
                        break;
                    case "economyCalendar-td economyCalendar-td__8":
                        String eventPrevious = StringUtils.strip(eventNode.text(), stripChars);
                        eventRow.setPreviousval(eventPrevious);
                        // save event
                        int count = newsEventDao.countEventsOnCurrencyDate(eventRow.getCurrencyid().getId(), curDateTime, eventRow.getImportance());
                        if (count > 0)
                            continue;
                        if (parseDateTime.compareTo(eventRow.getHappentime()) >= 0)
                            eventRow.setRaised(true);
                        else
                            eventRow.setRaised(false);

                        newsEventDao.saveEvent(eventRow);
                        break;
                }
            } catch (Exception e) {
                log.info("Error parsing news event: " + e);
                continue;
            }

        }
        statMessage = "Events for start Date: " + curDateTime + " parsed successfully";
        log.info(status());
        return true;
    }

    protected Document postRequest(String strParseHistoryStartDate) {
        Document doc = null;
        try {
            //https://www.exness.com/intl/en/tools/calendar/?Important=All&Date=2014-12-15
            String postData =
                    "Important=All&Date=" + strParseHistoryStartDate +
                            "&SortField=Datetime&SortUpDown=down&ViewPeriod=Week1&EUR=on&USD=on&JPY=on&GBP=on&CHF=on&AUD=on&CAD=on&NZD=on";
            String postUrl = url + "?" + postData;

            URL obj = new URL(postUrl);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            // Send post request
            //con.setDoOutput(true);
            //DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            //wr.writeBytes(postData);
            //wr.flush();
            //wr.close();

            int responseCode = con.getResponseCode();

            doc = Jsoup.parse(con.getInputStream(), "UTF-8", postUrl);
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }
        return doc;
    }
}

