package com.fxmind.dao;

import com.fxmind.entity.Newsevent;
import com.fxmind.global.NewsEventInfo;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Created by sergeizhuravlev on 5/27/14.
 */
public interface NewsEventDao extends GenericDao<Newsevent> {

    //NewsEventInfo GetNextNewsEvent(String mqlDateStr, String symbolStr, byte minImportance);
    NewsEventInfo GetNextNewsEventNative(String dateStr, String symbolStr, byte minImportance);
    void saveEvent(Newsevent event);
    int countEventsOnCurrencyDate(int currencyId, LocalDateTime curDateTime, byte Importance);
    List<Newsevent> GetUpcomingEvents(int startIndex, int MaxResults);
}

