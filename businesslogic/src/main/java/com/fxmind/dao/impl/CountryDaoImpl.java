package com.fxmind.dao.impl;

import com.fxmind.dao.CountryDao;
import com.fxmind.entity.Country;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * <p>Title: CountryDaoImpl</p>
 * <p/>
 * <p>Description: Interface of a Data access object dealing with CountryDaoImpl
 * persistence. It offers a set of methods which allow for saving,
 * deleting and searching CountryDaoImpl objects</p>
 */
@Repository
public class CountryDaoImpl extends GenericDaoImpl<Country> implements CountryDao {

    // TODO: marked to remove
    protected void insertCountry(EntityManager emForRecursiveDao, Country country) {
        emForRecursiveDao.persist(country);
    }

    // TODO: marked to implement or remove
    public List<Country> findById(List<Integer> ids) {
        return null;
    }

    // TODO: marked to implement or remove
    public List<Country> searchPrototypeCountry(Country country) {
        return searchPrototype(country, null);
    }

    // TODO: marked to implement or remove
    protected List<Country> searchPrototype(Country country, Integer maxResults) {
        return searchPrototype(country, maxResults);
    }

    // TODO: marked to implement or remove
    protected List<Country> searchPrototype(String query, Integer maxResults) {
        Query hquery = mssEntityManager.createQuery(query);
        if (maxResults != null)
            hquery.setMaxResults(maxResults);
        return hquery.getResultList();
    }
}
