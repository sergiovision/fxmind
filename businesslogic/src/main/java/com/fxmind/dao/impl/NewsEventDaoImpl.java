package com.fxmind.dao.impl;

import com.fxmind.dao.NewsEventDao;
import com.fxmind.entity.Newsevent;
import com.fxmind.global.NewsEventInfo;
import com.fxmind.global.fxmindConstants;
import com.fxmind.service.AdminService;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.commons.lang.mutable.MutableInt;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by sergeizhuravlev on 5/27/14.
 */
@Repository
public class NewsEventDaoImpl extends GenericDaoImpl<Newsevent> implements NewsEventDao {
    private static final Logger log = LoggerFactory.getLogger(NewsEventDaoImpl.class);

    @Autowired
    protected AdminService adminService;

    /*@Override
    public NewsEventInfo GetNextNewsEvent(String dateStr, String symbolStr, byte minImportance) {

        NewsEventInfo eventInfo = null;
        try
        {
            //CachedDateTimeZone brokerTZ = adminService.GetBrokerTimeZone();

            String C1 = symbolStr.substring(0, 3);
            String C2 = C1;
            if (symbolStr.length() == 6)
                C2 = symbolStr.substring(3, 3);

            DateTimeFormatter df = DateTimeFormatter.ofPattern(fxmindConstants.MTDATETIMEFORMAT);
            DateTime date = DateTime.parse(dateStr, df.withZoneUTC());

            //SimpleDateFormat sdf = new SimpleDateFormat(fxmindConstants.MTDATETIMEFORMAT);
            //sdf.setTimeZone(brokerTZ.toTimeZone());
            //Date date = sdf.parse(dateStr);

            java.util.Date from = new Date(0);// = date.toDate(); //ConvertTimeToUtc(date, BrokerTimeZoneInfo);
            java.util.Date to = new Date(0);// = date.plusDays(1).toDate();

            Query query = mssEntityManager.createNamedQuery(Newsevent.FIND_NEXTEVENT)
                    .setParameter("c1", C1)
                    .setParameter("c2", C2)
                    .setParameter("fr_dt", from)
                    .setParameter("to_dt", to)
                    .setParameter("imp", minImportance);

            List<Newsevent> newsList = query.getResultList();
            if (newsList.size() <= 0)
            {
                eventInfo = null;
                return eventInfo;
            }
            for (Newsevent news : newsList)
            {
                eventInfo = new NewsEventInfo();
                eventInfo.Currency = news.getCurrencyid().getName();
                //DateTime dateTime = new DateTime(news.getHappentime(), brokerTZ);
                //dateTime = dateTime.toDateTime(brokerTZ);
                DateTime resDate = new DateTime(news.getHappentime());
                eventInfo.RaiseDateTime = resDate.toString(fxmindConstants.MTDATETIMEFORMAT);// dateTime.toInstant().getMillis();
                //eventInfo.RaiseDateTime = TimeZoneInfo.ConvertTimeFromUtc(eventInfo.RaiseDateTime,
                //       BrokerTimeZoneInfo);
                eventInfo.Name = news.getName();
                eventInfo.Importance = news.getImportance();
                break;
            }
        }
        catch (Exception e)
        {
            log.error(e.toString());
        }
        return eventInfo;
    }*/

    @Override
    public NewsEventInfo GetNextNewsEventNative(String dateStr, String symbolStr, final byte minImportance) {
        final NewsEventInfo resInfo = new NewsEventInfo();
        try {
            Map<String, Short> curMap = adminService.getCurrencyMap();
            Map<Short, String> curMapIds = adminService.getCurrencyMapIds();

            final String C1 = symbolStr.substring(0, 3);
            Short c1 = curMap.get(C1);
            String C2 = "";
            if (symbolStr.length() == 6)
                C2 = symbolStr.substring(3);
            Short c2 = curMap.get(C2);

            LocalDateTime date = LocalDateTime.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MTDATETIMEFORMAT));
            LocalDateTime utcDate =  adminService.ConvertBrokerTimeToUtc(date);

            final String from = utcDate.format(java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MYSQLDATETIMEFORMAT)); //date.toString(fxmindConstants.MYSQLDATETIMEFORMAT); //ConvertTimeToUtc(date, BrokerTimeZoneInfo);
            final String to = utcDate.plusDays(1).format(java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MYSQLDATETIMEFORMAT));
            final String queryStr = new String("SELECT NE.CurrencyId, NE.HappenTime, NE.Name, NE.Raised, NE.Importance FROM NewsEvent NE " +
                    "WHERE (NE.CurrencyId=? OR NE.CurrencyId=?) AND (NE.HappenTime >= ?) AND (NE.HappenTime <= ?) AND (NE.Importance >= ?) ORDER BY NE.HappenTime ASC, NE.Importance DESC");

            MutableBoolean hasNews = new MutableBoolean(false);
            Session session = ((SessionImpl) mssEntityManager.getDelegate()).getSessionFactory().openSession();
            session.doWork( connection -> {
                PreparedStatement stmt = connection.prepareStatement(queryStr, java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
                stmt.setFetchSize(Integer.MIN_VALUE);
                stmt.setInt(1, c1);
                stmt.setInt(2, c2);
                stmt.setString(3, from);
                stmt.setString(4, to);
                stmt.setByte(5, minImportance);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Short curId = rs.getShort(1);
                    resInfo.Currency = curMapIds.get(curId);

                    LocalDateTime raiseDate = adminService.ConvertUtcToBrokerTime(rs.getTimestamp(2).toLocalDateTime());
                    resInfo.RaiseDateTime = raiseDate.format(java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MTDATETIMEFORMAT));
                    // eventInfo.RaiseDateTime = TimeZoneInfo.ConvertTimeFromUtc(eventInfo.RaiseDateTime, BrokerTimeZoneInfo);
                    resInfo.Name = rs.getString(3);
                    resInfo.Importance = rs.getByte(5);
                    hasNews.setValue(true);
                    rs.close();
                    break;
                }
            });
            session.close();
            if (hasNews.booleanValue())
                return resInfo;
            else
                return null;

        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

    @Transactional
    public void saveEvent(Newsevent event) {
        super.persist(event);
    }

    /*
        mCheckQuery = "SELECT NE.ID " +
            ",NE.HappenTime " +
            ",NE.Name " +
            ",NE.ParseTime " +
            ",NE.Raised " +
            ",NE.Importance " +
            ",C.Name " +
        "FROM NewsEvent NE " +
        "INNER JOIN Currency C ON NE.CurrencyId = C.ID" +
        "WHERE C.Name='{0}' AND YEAR(NE.HappenTime)={1} AND MONTH(NE.HappenTime) = {2} AND DAY(NE.HappenTime)={3} AND HOUR(NE.HappenTime)={4} AND " +
        "MINUTE(NE.HappenTime)={5} AND NE.Importance={6}";
    */
    public static final String mCheckQuery = new String("SELECT NE.ID, NE.HappenTime, NE.Name, NE.ParseTime, NE.Raised, NE.Importance, NE.CurrencyId FROM NewsEvent NE " +
                                     "WHERE (NE.CurrencyId=?) AND YEAR(NE.HappenTime)=? AND MONTH(NE.HappenTime) = ? AND DAY(NE.HappenTime)=? AND HOUR(NE.HappenTime)=? " +
                                     " AND MINUTE(NE.HappenTime)=? AND (NE.Importance = ?)");


    public int countEventsOnCurrencyDate(int currencyId, LocalDateTime curDateTime, byte Importance) {
        MutableInt countNews = new MutableInt(0);
        Session session = ((SessionImpl) mssEntityManager.getDelegate()).getSessionFactory().openSession();
        session.doWork(connection -> {
            PreparedStatement stmt = connection.prepareStatement(mCheckQuery, java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
            stmt.setFetchSize(Integer.MIN_VALUE);
            stmt.setInt(1, currencyId);
            stmt.setInt(2, curDateTime.getYear());
            stmt.setInt(3, curDateTime.getMonthValue());
            stmt.setInt(4, curDateTime.getDayOfMonth());
            stmt.setInt(5, curDateTime.getHour());
            stmt.setInt(6, curDateTime.getMinute());
            stmt.setInt(7, Importance);

            ResultSet rs = stmt.executeQuery();
            int res = 0;
            while (rs.next()) {
                res++;
            }
            countNews.setValue(res);
            rs.close();
        });
        session.close();
        return countNews.intValue();
    }

    @Override
    public List<Newsevent> GetUpcomingEvents(int startIndex, int MaxResults) {

        Query query = mssEntityManager.createNamedQuery(Newsevent.FIND_UPCOMINGEVENTS)
                .setParameter("curdate", adminService.GetCurrentTimestampUTC());

        query.setFirstResult(startIndex);
        query.setMaxResults(MaxResults);
        List<Newsevent> newsList = query.getResultList();

        return newsList;
    }

}
