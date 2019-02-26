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

 https://www.myfxbook.com/community/outlook

 */
@Component
public class MyFXBookRatioJob extends SystemTask {
	private static final Logger log = LoggerFactory.getLogger(MyFXBookRatioJob.class);

    public static final String url = "https://www.myfxbook.com/community/outlook";

    public static final String stripChars = " \n%&nbsp,;\u00A0";

    @Autowired
    protected SymbolDao symbolDao;

    @Autowired
    protected OpenPosRatioDao openPosRatioDao;

    @Autowired
    protected AdminService adminService;

    public MyFXBookRatioJob()
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

            Element table = doc.getElementById("outlookSymbolsTable");
            int i = 0;
            Element nodeTooltip = table.getElementById("outlookTip" + i);
            while (nodeTooltip != null)
            {
                String HTMLTable = nodeTooltip.val();
                if (!StringCleanUtils.stringIsNullOrEmpty(HTMLTable))
                {
                    Document docTable = Jsoup.parse(HTMLTable);
                    Elements nodeRows = docTable.select("html body table tbody tr");
                    Element nodeSymbol = nodeRows.get(1);
                    String strSymbol = "";
                    if (nodeSymbol != null)
                        strSymbol = nodeSymbol.getElementsByTag("td").get(0).text();
                    strSymbol = StringUtils.strip(strSymbol, stripChars);
                    if (strSymbol.length() == 6)
                        strSymbol = strSymbol.substring(0, 3) + "/" + strSymbol.substring(3);
                    Symbol sym = symbolDao.findByName(strSymbol);
                    if (sym != null)
                    {
                        Openposratio ratio = new Openposratio();
                        ratio.setSymbolid(sym.getId());
                        ratio.setParsetime(parseTime);
                        ratio.setSiteid(4);
                        Element nodeLong = nodeRows.get(3).getElementsByTag("td").get(1);
                        if (nodeLong != null)
                        {
                            String trim = StringUtils.strip(nodeLong.text(), stripChars);
                            if (StringCleanUtils.stringIsNullOrEmpty(trim))
                                ratio.setLongratio(0.0);
                            else
                                ratio.setLongratio(Double.parseDouble(trim));
                        }
                        Element nodeShort = nodeRows.get(2).getElementsByTag("td").get(1);
                        if (nodeShort != null)
                        {
                            String trim = StringUtils.strip(nodeShort.text(), stripChars);
                            if (StringCleanUtils.stringIsNullOrEmpty(trim))
                                ratio.setShortratio(0.0);
                            else
                                ratio.setShortratio(Double.parseDouble(trim));
                        }
                        openPosRatioDao.saveRatio(ratio);
                    }
                }
                nodeTooltip = table.getElementById("outlookTip" + (++i));
            }
        } catch (IOException e) {
            statMessage = "IOException: " + e.getMessage();
            log.error(status());
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

