package com.fxmind.dao.impl;

import com.fxmind.dao.GenericDao;
import com.fxmind.entity.AbstractEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @author Andrey.Charnamys
 */
public abstract class GenericDaoImpl<Entity extends AbstractEntity> implements GenericDao<Entity> {

    @PersistenceContext(unitName = "mss")
    protected EntityManager mssEntityManager;

    private final Class<Entity> type;

    public GenericDaoImpl() {
        type = (Class<Entity>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void persist(Entity entity) {
        mssEntityManager.persist(entity);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Entity merge(Entity entity) {
        return mssEntityManager.merge(entity);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void remove(Entity entity) {
        mssEntityManager.remove(entity);
    }

    @Override
    public Entity find(Integer id) {
        return mssEntityManager.find(type, id);
    }

    @Override
    public List<Entity> findAll() {
        final Query query = mssEntityManager.createQuery("select e from " + type.getSimpleName() + " e");
        return query.getResultList();
    }


}
