package com.fxmind.provider;

import com.fxmind.dto.PositionRatioSample;
import java.util.List;

/** Source of retail long/short positioning ("crowd sentiment"). */
public non-sealed interface PositionRatioProvider extends DataProvider {

    /** Name of the {@code site} row the samples are attributed to. */
    String siteName();

    /**
     * @throws com.fxmind.exception.DataProviderException if the source is unreachable or malformed
     */
    List<PositionRatioSample> fetchRatios();
}
