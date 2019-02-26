package com.fxmind.dao.impl;

import com.fxmind.dao.SymbolDao;
import com.fxmind.entity.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by sergeizhuravlev on 5/27/14.
 */
@Repository
public class SymbolDaoImpl extends GenericDaoImpl<Symbol> implements SymbolDao {
    private static final Logger log = LoggerFactory.getLogger(SymbolDaoImpl.class);

    @Override
    public Symbol findByName(String name) {
        Query q = mssEntityManager.createNamedQuery(Symbol.FIND_BY_NAME)
                .setParameter("name", name);
        List results = q.getResultList();

        if (results.isEmpty()) {
            return null;
        } else {
            if (results.size() >= 1)
                return (Symbol) results.get(0);
            else
                return null;
        }
    }

    @Override
    public List<Symbol> findAll() {
        Query q = mssEntityManager.createNamedQuery(Symbol.FIND_ALL);
        return q.getResultList();
    }

}
