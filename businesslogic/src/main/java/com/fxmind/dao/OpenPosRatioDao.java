package com.fxmind.dao;

import com.fxmind.entity.Openposratio;

import java.util.List;

/**
 * Created by sergeizhuravlev on 5/27/14.
 */
public interface OpenPosRatioDao extends GenericDao<Openposratio> {

    void GetAverageLastGlobalSentiments(String mqlDateStr, String symbolStr, Double[] pairVal);

    List<Double> iGlobalSentimentsArray(String symbolName, List<String> brokerDates, int siteId);

    void saveRatio(Openposratio ratio);
}
