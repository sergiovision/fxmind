package com.fxmind.dao;

import com.fxmind.entity.Country;

import java.util.List;

/**
 * <p>Title: CountryDao</p>
 *
 * @author Sergei Zhuravlev
 *         <p>Description: Interface of a Data access object dealing with CountryDao
 *         persistence. It offers a set of methods which allow for saving,
 *         deleting and searching country objects</p>
 */
public interface CountryDao extends GenericDao<Country> {

    List<Country> findById(List<Integer> ids);

    List<Country> searchPrototypeCountry(Country country);
}
