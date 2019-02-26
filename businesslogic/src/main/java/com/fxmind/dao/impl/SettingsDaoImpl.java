package com.fxmind.dao.impl;

import com.fxmind.dao.SettingsDao;
import com.fxmind.entity.Settings;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

/**
 * <p>Title: SettingsDaoImpl</p>
 *
 * @author Sergei Zhuravlev
 */
@Repository(value = "settingsDao")
public class SettingsDaoImpl extends GenericDaoImpl<Settings> implements SettingsDao {

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void save(Settings settings) {
        Settings oldSettings = findByName(settings.getName());

        if (oldSettings != null) {
            mssEntityManager.merge(settings);
        } else {
            mssEntityManager.persist(settings);
        }
    }

    @Override
    public Settings findByName(String name) {
        Query query = mssEntityManager.createNamedQuery(Settings.FIND_BY_NAME);
        query.setParameter("name", name);
        List<Settings> settingsList = query.getResultList();

        return settingsList.size() > 0 ? settingsList.get(0) : null;
    }
}
