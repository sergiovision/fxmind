package com.fxmind.manager.jobs;

import com.fxmind.dao.OpenPosRatioDao;
import com.fxmind.dao.SymbolDao;
import com.fxmind.entity.Openposratio;
import com.fxmind.entity.Symbol;
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

import java.io.IOException;
import java.sql.Timestamp;

/**

 http://fxtrade.oanda.com/analysis/open-position-ratios

 */
@Component
public class OandaRatioJob extends SystemTask {
	private static final Logger log = LoggerFactory.getLogger(OandaRatioJob.class);

    public static final String url = "http://fxtrade.oanda.com/analysis/open-position-ratios";

    public static final String stripChars = " \n%&nbsp,;\u00A0" ;

    @Autowired
    protected SymbolDao symbolDao;

    @Autowired
    protected OpenPosRatioDao openPosRatioDao;

    @Autowired
    protected AdminService adminService;

    public OandaRatioJob()
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

        try {
            Timestamp parseTime = adminService.GetCurrentTimestampUTC();
            Document doc = Jsoup.connect(url).get();

            Elements colSymbols = doc.select("html body#top section.container div#main_core_container div#main_content div.main_content_layout_container div.index_vanilla_30px div.index_content div#orderbook-long-short.orderbook-ratio-graph ol#long-short-ratio-graph.position-ratio-list li");
                    //"/html/body/section/div/div[4]/div/div/div/div[3]/ol/li");
            if (colSymbols == null) {
                statMessage = "Error parsing OANDA ratio page";
                return;
            }
            for (Element symnode : colSymbols) {

                String symbolname = symnode.getElementsByTag("span").first().text();//.getElementsByAttribute("name").first().val();
                Symbol sym = symbolDao.findByName(symbolname);
                if (sym == null)
                    continue;
                Openposratio ratio = new Openposratio();
                ratio.setSymbolid(sym.getId());
                ratio.setParsetime(parseTime);
                ratio.setSiteid(5);

                boolean hasBuy = false;
                boolean hasSell = false;
                Element nodeLong = symnode.getElementsByTag("div").first().getElementsByTag("span").first();
                if (nodeLong != null)
                {
                    String trim = StringUtils.strip(nodeLong.text(), stripChars);
                    if (StringCleanUtils.stringIsNullOrEmpty(trim))
                        ratio.setLongratio(0.0);
                    else {
                        ratio.setLongratio(Double.parseDouble(trim));
                        hasBuy = true;
                    }
                }
                Element nodeShort = symnode.getElementsByTag("div").get(1).getElementsByTag("span").get(1);
                if (nodeShort != null)
                {
                    String trim = StringUtils.strip(nodeShort.text(), stripChars);
                    if (StringCleanUtils.stringIsNullOrEmpty(trim)) {
                        if (hasBuy)
                            ratio.setShortratio(100.0-ratio.getLongratio());
                        else
                            ratio.setShortratio(0.0);
                    }
                    else {
                        ratio.setShortratio(Double.parseDouble(trim));
                        hasSell = true;
                    }
                }
                if ((hasBuy == false) && (hasSell == true))
                    ratio.setLongratio(100.0-ratio.getShortratio());
                openPosRatioDao.saveRatio(ratio);
            }

        } catch (IOException e) {
            statMessage = "IOException: " + e.getMessage();
            //log.error(status());
        }
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

