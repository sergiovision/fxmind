package com.fxmind.dao.impl;

import com.fxmind.dao.PersonDao;
import com.fxmind.entity.Person;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

/**
 * <p>Title: PersonDaoImpl</p>
 *
 * @author Sergei Zhuravlev
 *         <p>Description: Interface of a Data access object dealing with PersonDaoImpl
 *         persistence. It offers a set of methods which allow for saving,
 *         deleting and searching PersonDaoImpl objects</p>
 */
@Repository
public class PersonDaoImpl extends GenericDaoImpl<Person> implements PersonDao {

    @Override
    public Person findByMail(String mail) {
        Query query = mssEntityManager.createNamedQuery(Person.FIND_BY_MAIL).setParameter("mail", mail);
        List<Person> persons = query.getResultList();
        if (persons.size() == 0) {
            return null;
        }
        return persons.get(0);
    }

    @Override
    public Person findByUuid(String uuid) {
        Query query = mssEntityManager.createNamedQuery(Person.FIND_BY_UUID).setParameter("uuid", uuid);
        List<Person> persons = query.getResultList();
        if (persons.size() == 0) {
            return null;
        }
        return persons.get(0);
    }

    @Override
    public Person findById(Integer id) {
        Query query = mssEntityManager.createNamedQuery(Person.FIND_BY_ID).setParameter("id", id);
        List<Person> persons = query.getResultList();
        if (persons.size() == 0) {
            return null;
        }
        return persons.get(0);
    }

}
