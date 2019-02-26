package com.fxmind.global;

import com.fxmind.dao.NewsEventDao;
import com.fxmind.dao.OpenPosRatioDao;
import com.fxmind.service.AdminService;
import com.fxmind.utils.ApplicationContextProvider;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Here is a class of server implementation
 * Will be much functionality in it
 *
 * Created by sergeizhuravlev on 5/8/14.
 */
public class FXMindMQLHandler implements FXMindMQL.Iface {
    private static final Logger log = LoggerFactory.getLogger(FXMindMQLHandler.class);

    @Autowired
    protected NewsEventDao newsEventDao;

    @Autowired
    protected OpenPosRatioDao openPosRatioDao;

    @Autowired
    protected AdminService adminService;

    //@Autowired
    //protected SearchService searchService;

    public FXMindMQLHandler() {
        ApplicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public List<String> ProcessStringData(Map<String, String> paramsList, List<String> inputData) throws TException {
        //if (fxmind.IsDebug())
        //   log.Info("server(" + GetHashCode() + ") ProcessStringData: " + inputData.Count);
        List<String> list = new ArrayList<String>();
        try
        {
            if (!paramsList.containsKey("func"))
            {
                log.error("server(" + hashCode() + ") ProcessStringData: Params error");
                return list;
            }
            String func = paramsList.get("func");
            switch (func)
            {
                case "NextNewsEvent":
                {
                    String dateStr = paramsList.get("time");
                    String symbolStr = paramsList.get("symbol");
                    byte minImportance = Byte.parseByte(paramsList.get("importance"));
                    NewsEventInfo info = newsEventDao.GetNextNewsEventNative(dateStr, symbolStr, minImportance);
                    //NewsEventInfo info = searchService.GetNextNewsEvent(dateStr, symbolStr, minImportance);
                    if (info != null)
                    {
                        //log.Info( info.RaiseDateTime.ToString(MainService.MTDATETIMEFORMAT) + " Got news: (" + info.Name + ") Importance:  " + info.Importance.ToString());
                        list.add(info.getCurrency());
                        list.add(new Byte(info.getImportance()).toString());
                        list.add(info.getRaiseDateTime());
                        list.add(info.getName());
                    }
                }
                break;
                case "Somefunc":
                {

                }
                break;
            }
        }
        catch (Exception e)
        {
            log.error("ProcessStringData Error:" + e);
        }
        return list;
    }

    @Override
    public List<Double> ProcessDoubleData(Map<String, String> paramsList, List<String> inputData) throws TException {
        List<Double> list = new ArrayList<Double>();
        try
        {
            if (!paramsList.containsKey("func"))
            {
                log.error("server(" + hashCode() + ") ProcessDoubleData: Params error");
                return list;
            }
            String func = paramsList.get("func");
            switch (func)
            {
                case "CurrentSentiments":
                {
                    String symbolStr = paramsList.get("symbol");
                    String dateStr = paramsList.get("time");
                    Double[] pairVal = { 0.0, 0.0 };
                    openPosRatioDao.GetAverageLastGlobalSentiments(dateStr, symbolStr, pairVal);
                    list.add(pairVal[0]);
                    list.add(pairVal[1]);
                }
                break;
                case "SentimentsArray":
                {
                    String symbolStr = paramsList.get("symbol");
                    int siteId = Short.parseShort(paramsList.get("site"));
                    list = openPosRatioDao.iGlobalSentimentsArray(symbolStr, inputData, siteId);
                }
                break;
                case "CurrencyStrengthArray":
                {
                    // removed
                }
                break;
            }
        }
        catch (Exception e)
        {
            log.error("ProcessDoubleData Error:" + e);
        }
        return list;
    }

    @Override
    public long IsServerActive(Map<String, String> paramsList) throws TException {
        //if (fxmind.IsDebug())
        log.info("server(" + hashCode() + ") IsServerActive");
        return 1;
    }

    @Override
    public void PostStatusMessage(Map<String, String> paramsList) throws TException {
        //if (fxmind.IsDebug())
        log.info("server(" + hashCode() + ") PostStatusMessage ("
                + paramsList.get("account") + ", " + paramsList.get("magic") + "): " + paramsList.get("message"));
    }

}
