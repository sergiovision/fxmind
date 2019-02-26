package com.fxmind.service;

import com.fxmind.entity.Jobs;
import com.fxmind.entity.Person;
import com.fxmind.entity.Settings;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * Service for managing admin tasks. Like Music Processing, User Profiles management etc.
 * @author SergeiZhuravlev
 *
 */
public interface AdminService {
    List<Jobs> getAllJobs( );

    List<Jobs> getActiveJobs( );

    String getProperty(String name);

    void setProperty(String name, String value);

    ZoneId GetBrokerTimeZone();

    Map<String, Short> getCurrencyMap();
    Map<Short, String> getCurrencyMapIds();

    Map<String, Integer> getSymbolMap();

    LocalDateTime ConvertBrokerTimeToUtc(LocalDateTime dateTime);

    LocalDateTime ConvertUtcToBrokerTime(LocalDateTime dateTime);

    Timestamp GetCurrentTimestampUTC();

    Properties getProps();

    Jobs loadJob(String group, String name);

    List<Person> loadAllPersons();

    void saveJob(Jobs job);

    Person findPersonById(Integer id);

    void updatePerson(Person person);

    List<Settings> getAllProperties();

    void saveProperty(Settings settings);

    void resetSettingToDefaultValue(Settings set);
}
