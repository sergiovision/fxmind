package com.fxmind.dao.impl;

import com.fxmind.dao.JobsDao;
import com.fxmind.entity.Jobs;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

/**
 * <p>Title: JobsDaoImpl</p>
 *
 * @author : Sergei Zhuravlev
 */
@Repository
public class JobsDaoImpl extends GenericDaoImpl<Jobs> implements JobsDao {

    @Override
    public Jobs findByGroupName(String group, String name) {
        Query q = mssEntityManager.createNamedQuery(Jobs.FIND_BY_GROUP_NAME)
                .setParameter("grp", group)
                .setParameter("name", name);
        List results = q.getResultList();

        if (results.isEmpty()) {
            return null;
        } else {
            if (results.size() >= 1)
                return (Jobs) results.get(0);
            else
                return null;
        }
    }

    @Override
    public List<Jobs> findActive() {
        Query q = mssEntityManager.createNamedQuery(Jobs.FIND_ACTIVE);
        return q.getResultList();
    }
}
