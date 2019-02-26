package com.fxmind.service.impl;

import com.fxmind.dao.CurrencyDao;
import com.fxmind.service.AdminService;
import com.fxmind.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Properties;

/*import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.BinaryResponseParser;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;*/

/**
 * @author Sergei Zhuravlev
 */
@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    public static final String ENCRYPT_KEY = "123456789012345678901234";
    public static final String VERTICAL_BAR = "|";
    public static final String TICKET_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Resource(name = "ServiceProps")
    private Properties props;

    // Define search types:
    @Value("${Solr.QueryFieldsByArtist}")
    public String SOLR_QFBYARTIST;
    @Value("${Solr.QueryFieldsByAlbum}")
    public String SOLR_QFBYALBUM;
    @Value("${Solr.QueryFieldsBySong}")
    public String SOLR_QFBYSONG;
    @Value("${Solr.FuzzyCoeff}")
    public String SOLR_FUZZYCOEF;

    @Autowired
    protected AdminService adminService;
    @Autowired
    protected CurrencyDao currencyDao;


/*
    @Override
    public SolrServer getSolrServer() {

        if (solrServer == null) {
            String serverType = adminService.getProperty(Settings.SOLR_TYPE);
            String serverURL = adminService.getProperty(Settings.SOLR_SERVER_URL);
            if (serverType.contains("Embed")) {
               // String solrLocalPath = adminService.getProperty(Settings.SOLR_LOCAL_HOME_PATH);
               // System.setProperty("solr.solr.home", solrLocalPath);
              //  File file = new File(solrLocalPath + "/solr.xml");
               // org.apache.solr.core.CoreContainer coreContainer = org.apache.solr.core.CoreContainer.createAndLoad(solrLocalPath, file);
               // String[] path = serverURL.split("/");
               // String coreName = path[path.length-1];
               // solrServer = new org.apache.solr.client.solrj.embedded.EmbeddedSolrServer(coreContainer, coreName);

            } else {
                HttpSolrServer server = new HttpSolrServer(serverURL);

                server.setRequestWriter(new BinaryRequestWriter());
                server.setMaxRetries(1);
                server.setConnectionTimeout(5000);
                server.setParser(new BinaryResponseParser());

                solrServer = server;
            }
        }

        return solrServer;
    }

    @Override
    public void clearAllSolrIndex() {
        SolrServer server = getSolrServer();
        try {
            server.deleteByQuery( "*:*" ); // CAUTION: deletes everything!
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void commitToSolr() {
        try {
            solrServer.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void optimizeSolr() {
        try {
            solrServer.optimize();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdownSolr() {
        if (solrServer != null) {
            solrServer.shutdown();
            solrServer = null;
        }
    }

    @Override
    public NewsEventInfo GetNextNewsEvent(String dateStr, String symbolStr, byte minImportance) {
        NewsEventInfo resInfo = null;
        try {
            //CachedDateTimeZone brokerTZ = adminService.GetBrokerTimeZone();
            Byte hrOffset = GetBrokerGMTOffsetHours();

            String C1 = symbolStr.substring(0, 3);
            String C2 = C1;
            if (symbolStr.length() == 6)
                C2 = symbolStr.substring(3);

            Map<String, Short> curmap = getCurrencyMap();

            DateTimeFormatter df = DateTimeFormat.forPattern(fxmindConstants.MTDATETIMEFORMAT);
            DateTime date = DateTime.parse(dateStr, df).minusHours(hrOffset);

            String from = date.toString(fxmindConstants.SOLRDATETIMEFORMAT); //ConvertTimeToUtc(date, BrokerTimeZoneInfo);
            //Date to = date.plusDays(1).toDate();

            SolrServer server = getSolrServer();
            String resQuery = String.format("currencyid:(%d OR %d) AND happentime:[%s TO %s+1DAY] AND importance:[%d TO *]", curmap.get(C1), curmap.get(C2), from, from, minImportance);
            SolrQuery query = new SolrQuery().setQuery(resQuery)
                    .addSort("happentime", SolrQuery.ORDER.asc)
                    .addSort("importance", SolrQuery.ORDER.desc);

            QueryResponse response = server.query(query);
            SolrDocumentList solrlist = response.getResults();

            Iterator<SolrDocument> iter = solrlist.iterator();
            while (iter.hasNext()) {
                SolrDocument resultDoc = iter.next();
                resInfo = new NewsEventInfo();
                String currid = resultDoc.getFieldValue("currencyid").toString();
                resInfo.Currency = currencyMapIDs.get(Short.parseShort(currid));
                //String happentime = .toString();
                //DateTimeFormatter dfSolr = DateTimeFormat.forPattern(fxmindConstants.SOLRDATETIMEFORMAT);
                DateTime raiseDate = new DateTime(resultDoc.getFieldValue("happentime"));//.parse(happentime, dfSolr);
                resInfo.RaiseDateTime = raiseDate.plusHours(hrOffset).toString(fxmindConstants.MTDATETIMEFORMAT);// dateTime.toInstant().getMillis();
                //eventInfo.RaiseDateTime = TimeZoneInfo.ConvertTimeFromUtc(eventInfo.RaiseDateTime,
                //       BrokerTimeZoneInfo);
                resInfo.Name = resultDoc.getFieldValue("name").toString();
                String importance = resultDoc.getFieldValue("importance").toString();
                resInfo.Importance = Byte.parseByte(importance);
                break;
            }
        } catch (SolrServerException e) {
            log.error(e.toString());
        }
        return resInfo;
    }

*/
}
