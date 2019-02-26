package com.fxmind.dao;

import com.fxmind.entity.AbstractEntity;

import java.util.List;

/**
 * @author Andrey.Charnamys
 */
public interface GenericDao<Entity extends AbstractEntity> {

    void persist(Entity entity);

    Entity merge(Entity entity);

    void remove(Entity entity);

    Entity find(Integer id);

    List<Entity> findAll();
}
