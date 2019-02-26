package com.fxmind.dao;

import com.fxmind.entity.Jobs;

import java.util.List;

/**
 * <p>Title: JobsDao</p>
 *
 * @author: Sergei Zhuravlev
 */
public interface JobsDao extends GenericDao<Jobs> {

    List<Jobs> findActive();

    Jobs findByGroupName(String group, String name);

}
