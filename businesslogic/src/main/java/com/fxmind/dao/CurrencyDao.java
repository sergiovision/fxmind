package com.fxmind.dao;

import com.fxmind.entity.Currency;

import java.util.List;

/**
 * Created by sergeizhuravlev on 5/27/14.
 */
public interface CurrencyDao extends GenericDao<Currency> {

    Currency find(Integer id);

    Currency findByName(String name);

    List<Currency> findAll();

}
