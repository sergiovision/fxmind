package com.fxmind.dao;


import com.fxmind.entity.Person;

/**
 * <p>Title: PersonDao</p>
 *
 * @author Sergei Zhuravlev
 *         <p>Description: Interface of a Data access object dealing with PersonDao
 *         persistence. It offers a set of methods which allow for saving,
 *         deleting and searching person objects</p>
 */
public interface PersonDao extends GenericDao<Person> {

    Person findByMail(String mail);

    Person findByUuid(String uuid);

    Person findById(Integer id);
}
