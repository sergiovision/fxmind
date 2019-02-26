package com.fxmind.dao.impl;

import com.fxmind.dao.OpenPosRatioDao;
import com.fxmind.entity.Openposratio;
import com.fxmind.global.fxmindConstants;
import com.fxmind.service.AdminService;
import org.apache.commons.lang.mutable.MutableDouble;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sergeizhuravlev on 5/27/14.
 */
@Repository
public class OpenPosRatioDaoImpl extends GenericDaoImpl<Openposratio> implements OpenPosRatioDao {
    private static final Logger log = LoggerFactory.getLogger(OpenPosRatioDaoImpl.class);

    @Autowired
    protected AdminService adminService;

    @Override
    public void GetAverageLastGlobalSentiments(String mqlDateStr, String symbolName, Double[] pairVal) {
        pairVal[0] = -1.0;
        pairVal[1] = -1.0;
        try
        {
            if (symbolName.length() == 6) {
                StringBuilder strSym = new StringBuilder(symbolName);
                strSym = strSym.insert(3, "/");
                symbolName = strSym.toString();
            }
            Map<String, Integer> symMap = adminService.getSymbolMap();
            Integer symId = symMap.get(symbolName);
            if (symId == null) {
                log.info("Not found symbol in DB for symbolString: " + symbolName);
                return;
            }

            Session session = ((SessionImpl)mssEntityManager.getDelegate()).getSessionFactory().openSession();

            LocalDateTime date = LocalDateTime.parse(mqlDateStr, java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MTDATETIMEFORMAT));
            LocalDateTime utcDate = adminService.ConvertBrokerTimeToUtc(date);

            final String to = utcDate.format(java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MYSQLDATETIMEFORMAT));
            final String from = utcDate.minusMinutes(fxmindConstants.SENTIMENTS_FETCH_PERIOD).format(java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MYSQLDATETIMEFORMAT));

            //DateTime to = TimeZoneInfo.ConvertTimeToUtc(date, BrokerTimeZoneInfo);
            //DateTime from = to.AddMinutes(-SENTIMENTS_FETCH_PERIOD);
            final String queryStr = "SELECT LongRatio, ShortRatio, SiteID FROM OpenPosRatio " +
            " WHERE (SymbolID=?) AND (ParseTime >= ?) AND (ParseTime <= ?) ORDER BY ParseTime DESC";

            final MutableDouble longV = new MutableDouble(pairVal[0]);
            final MutableDouble shortV = new MutableDouble(pairVal[0]);
            session.doWork( connection -> {
                PreparedStatement stmt = connection.prepareStatement(queryStr, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                stmt.setFetchSize(Integer.MIN_VALUE);
                stmt.setInt(1, symId);
                stmt.setString(2, from);
                stmt.setString(3, to);

                ResultSet rs = stmt.executeQuery();

                int cnt = 0;
                double valLong = 0;
                double valShort = 0;
                while (rs.next()) {
                    valLong += rs.getDouble(1);
                    valShort += rs.getDouble(2);
                    cnt++;
                }
                if (cnt > 0) {
                    longV.setValue(valLong / cnt);
                    shortV.setValue(valShort / cnt);
                }
                rs.close();
            });
            pairVal[0] = longV.doubleValue();
            pairVal[1] = shortV.doubleValue();
            session.close();
        }
        catch (Exception e)
        {
            log.info(e.toString());
        }
    }

    @Override
    public List<Double> iGlobalSentimentsArray(String symbolName, List<String> brokerDates, int siteId) {
        List<Double> list = new ArrayList<Double>();

        if (symbolName.length() == 6) {
            StringBuilder strSym = new StringBuilder(symbolName);
            strSym = strSym.insert(3, "/");
            symbolName = strSym.toString();
        }
        Map<String, Integer> symMap = adminService.getSymbolMap();
        Integer symId = symMap.get(symbolName);
        if (symId == null) {
            log.info("Not found symbol in DB for symbolString: " + symbolName);
            return list;
        }
        String queryStrInterval;
        if (siteId == 0)
        {
            queryStrInterval = "SELECT LongRatio, ShortRatio, SiteID FROM OpenPosRatio " +
            " WHERE (SymbolID=?) AND (ParseTime >= ?) AND (ParseTime <= ?) " +
            " ORDER BY ParseTime DESC";
        }
        else
        {
            queryStrInterval = "SELECT LongRatio, ShortRatio FROM OpenPosRatio " +
            " WHERE (SymbolID = ?) AND (ParseTime >= ?) AND (ParseTime <= ?) AND (SiteID = ?) " +
            " ORDER BY ParseTime ASC";
        }

        Session session = ((SessionImpl)mssEntityManager.getDelegate()).getSessionFactory().openSession();

        session.doWork( connection -> {

            for (String brokerDate : brokerDates)
            {
                LocalDateTime date = LocalDateTime.parse(brokerDate, java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MTDATETIMEFORMAT));
                LocalDateTime utcDate = adminService.ConvertBrokerTimeToUtc(date);

                final String to = utcDate.format(java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MYSQLDATETIMEFORMAT));
                final String from = utcDate.minusHours(1).format(java.time.format.DateTimeFormatter.ofPattern(fxmindConstants.MYSQLDATETIMEFORMAT));

                PreparedStatement stmt = connection.prepareStatement(queryStrInterval, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                stmt.setFetchSize(Integer.MIN_VALUE);
                if (siteId == 0) {
                    stmt.setInt(1, symId);
                    stmt.setString(2, from);
                    stmt.setString(3, to);
                } else {
                    stmt.setInt(1, symId);
                    stmt.setString(2, from);
                    stmt.setString(3, to);
                    stmt.setInt(4, siteId);
                }

                ResultSet rs = stmt.executeQuery();
                int cnt = 0;
                double valLong = 0;
                while (rs.next()) {
                    valLong += rs.getDouble(1);
                    cnt++;
                }
                if (cnt > 0) {
                    list.add(valLong / cnt);
                } else {
                    list.add(fxmindConstants.GAP_VALUE);
                }
                rs.close();
            }
        });
        session.close();
        return list;
    }

    @Transactional
    @Override
    public void saveRatio(Openposratio ratio) {
        super.persist(ratio);
    }

}
