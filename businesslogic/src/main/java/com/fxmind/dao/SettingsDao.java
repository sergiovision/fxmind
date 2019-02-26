package com.fxmind.dao;

import com.fxmind.entity.Settings;

/**
 * <p>Title: SettingsDao</p>
 *
 * @Author Sergei Zhuravlev
 */
public interface SettingsDao extends GenericDao<Settings> {

    void save(Settings settings);

    Settings findByName(String name);

}
