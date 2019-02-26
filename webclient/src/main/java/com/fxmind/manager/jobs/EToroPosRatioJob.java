package com.fxmind.manager.jobs;

import com.fxmind.dao.OpenPosRatioDao;
import com.fxmind.dao.SymbolDao;
import com.fxmind.entity.Openposratio;
import com.fxmind.entity.Symbol;
import com.fxmind.service.AdminService;
import com.fxmind.utils.ApplicationContextProvider;
import com.fxmind.utils.StringCleanUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Parse news events job on http://www.mt5.com/forex_calendar/index/2014/23/timezone_0/
 {"SentimentPercent":53.00,"SentimentType":"Selling","Rates":{"Ask":1.3597,"Bid":1.3594,"Precision":4,"InstrumentID":1,"PeriodChangePrecent":-0.0514668039114770972722593900,"PeriodChangeValue":-0.0007},"InstrumentID":1,"PeriodChangePrecent":-0.0514668039114770972722593900,"PeriodChangeValue":-0.0007}

 https://openbook.etoro.com/API/Markets/Symbol/InstrumentRate/?name=eur-usd

 how to parse JSON read http://theoryapp.com/parse-json-in-java/
 */
@Component
public class EToroPosRatioJob extends SystemTask {
	private static final Logger log = LoggerFactory.getLogger(EToroPosRatioJob.class);

    public static final String URLformat = "https://openbook.etoro.com/API/Markets/Symbol/InstrumentRate/?name=";

    @Autowired
    protected SymbolDao symbolDao;

    @Autowired
    protected OpenPosRatioDao openPosRatioDao;

    @Autowired
    protected AdminService adminService;

    protected Set<String> set;

    public EToroPosRatioJob()
    {
        super();
        try {
            // autowire this component to get autowired variables initialized
            ApplicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
        } catch (Exception e) {
            log.error("Failed dynamic autowiring : " + e);
        }
        SetNonSupportedSymbols();
    }

    private void SetNonSupportedSymbols()
    {
        set = new HashSet<String>();
        set.add("GBP/CHF");
        set.add("AUD/NZD");
        set.add("AUD/CAD");
        set.add("AUD/CHF");
        set.add("CAD/CHF");
        set.add("EUR/NZD");
        set.add("GBP/AUD");
        set.add("GBP/CAD");
        set.add("GBP/NZD");
        set.add("NZD/CAD");
        set.add("NZD/CHF");
        set.add("NZD/JPY");
    }

    protected void DoParsing() {

        Timestamp parseTime = adminService.GetCurrentTimestampUTC();

        List<Symbol> symbols = symbolDao.findAll();
        for (Symbol sym:symbols) {
            if (set.contains(sym.getName()))
                continue;
            if (!sym.getUse4tech())
                continue;

            String url = URLformat.concat(sym.getC1().toLowerCase() + "-" + sym.getC2().toLowerCase());
            try {
                InputStream input = new URL(url).openStream();
                String jsonString = IOUtils.toString(input, "UTF-8");
                if (!ParseSentimentSymbol(sym, jsonString, parseTime))
                    log.info("Failed to parse symbol: " + sym.getName());

            } catch (IOException e) {
                statMessage = "IOException: " + e.getMessage();
                log.error(status());
            } catch (JSONException e) {
                statMessage = "JSONException: " + e.getMessage();
                log.error(status());
            }
        }

    }

    @Transactional
    protected boolean ParseSentimentSymbol(Symbol sym, String data, Timestamp parseTime) throws JSONException {
        JSONObject obj = new JSONObject(data);
        Double value = obj.getDouble("SentimentPercent");
        //if (StringCleanUtils.stringIsNullOrEmpty(percent))
        //    return false;
        //Double value = Double.parseDouble(percent);
        if (value == null)
            return false;
        String type = obj.getString("SentimentType");
        if (StringCleanUtils.stringIsNullOrEmpty(type))
            return false;

        Openposratio ratio = new Openposratio();
        ratio.setSymbolid(sym.getId());
        ratio.setParsetime(parseTime);
        ratio.setSiteid(2);
        boolean hasBuy = false;
        boolean hasSell = false;
        if (type.contains("Buy")) {
            hasBuy = true;
            ratio.setLongratio(value);
        }
        if (type.contains("Sell")) {
            hasSell = true;
            ratio.setShortratio(value);
        }
        if (hasBuy)
        {
            ratio.setShortratio(100.0 - value);
        }
        if (hasSell)
        {
            ratio.setLongratio(100.0 - value);
        }
        openPosRatioDao.saveRatio(ratio);
        return true;
    }

	@Override
	public void run() {
        try {
            DoParsing();
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

}

