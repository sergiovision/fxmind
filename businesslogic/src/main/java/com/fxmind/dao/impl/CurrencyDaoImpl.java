package com.fxmind.dao.impl;

import com.fxmind.dao.CurrencyDao;
import com.fxmind.entity.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by sergeizhuravlev on 5/27/14.
 */
@Repository
public class CurrencyDaoImpl extends GenericDaoImpl<Currency> implements CurrencyDao {
    private static final Logger log = LoggerFactory.getLogger(CurrencyDaoImpl.class);

    @Override
    public Currency findByName(String name) {
        Query q = mssEntityManager.createNamedQuery(Currency.FIND_BY_NAME)
                .setParameter("name", name);
        List results = q.getResultList();

        if (results.isEmpty()) {
            return null;
        } else {
            if (results.size() >= 1)
                return (Currency) results.get(0);
            else
                return null;
        }
    }

    @Override
    public List<Currency> findAll() {
        Query q = mssEntityManager.createNamedQuery(Currency.FIND_ALL);
        return q.getResultList();
    }

}
