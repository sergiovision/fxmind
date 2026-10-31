package com.fxmind.provider;

import com.fxmind.dto.ParsedNewsEvent;
import java.util.List;

/** Source of economic-calendar events. */
public non-sealed interface NewsProvider extends DataProvider {

    /**
     * @throws com.fxmind.exception.DataProviderException if the source is unreachable or malformed
     */
    List<ParsedNewsEvent> fetchEvents();
}
