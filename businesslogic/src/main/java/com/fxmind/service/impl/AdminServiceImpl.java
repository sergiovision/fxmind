package com.fxmind.service.impl;

import com.fxmind.dao.*;
import com.fxmind.entity.Jobs;
import com.fxmind.entity.Person;
import com.fxmind.entity.Settings;
import com.fxmind.entity.Symbol;
import com.fxmind.service.AdminService;
import com.fxmind.utils.StringCleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Service class for managing Admin tasks
 * @author Sergei Zhuravlev
 */
@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger log = Logger.getLogger(AdminServiceImpl.class);

    protected ZoneId brokerZone;
    protected ZoneId utcZone;

    @PostConstruct
    public void init() {
        utcZone = ZoneId.of("UTC");
    }

    @Autowired
    private JobsDao joblistDao;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private SettingsDao settingsDao;

    @Autowired
    protected CurrencyDao currencyDao;

    @Autowired
    protected SymbolDao symbolDao;

    protected Map<String, Short> currencyMap;
    protected Map<Short, String> currencyMapIDs;

    protected Map<String, Integer> symbolMap;
    protected Map<Integer, String> symbolMapIDs;

    @Resource(name = "ServiceProps")
    private Properties props;

    @Override
    public List<Jobs> getAllJobs() {
        List<Jobs> jobs = joblistDao.findAll();
        return jobs;
    }

    @Override
    public List<Jobs> getActiveJobs() {
        List<Jobs> jobs = joblistDao.findActive();
        return jobs;
    }

    @Override
    public String getProperty(String name) {
        Settings set = settingsDao.findByName(name);
        if (set == null)
            return props.getProperty(name);
        return set.getValue();
    }

    @Transactional
    @Override
    public void setProperty(String name, String value) {
        Settings settings = new Settings();
        settings.setName(name);
        settings.setValue(value);
        settingsDao.save(settings);
    }

    @Override
    public ZoneId GetBrokerTimeZone() {
        if ( brokerZone == null ) {
            String tzString = getProperty(Settings.BROKER_TIMEZONE);
            brokerZone = ZoneId.of(tzString);
        }
        return brokerZone;
    }

    @Override
    public LocalDateTime ConvertBrokerTimeToUtc(LocalDateTime date) {
        ZonedDateTime zdate = date.atZone(GetBrokerTimeZone());
        return zdate.toOffsetDateTime().atZoneSameInstant(utcZone).toLocalDateTime();
    }

    @Override
    public LocalDateTime ConvertUtcToBrokerTime(LocalDateTime date) {
        ZonedDateTime zdate = date.atZone(utcZone);
        return zdate.toOffsetDateTime().atZoneSameInstant(GetBrokerTimeZone()).toLocalDateTime();
    }

    @Override
    public Timestamp GetCurrentTimestampUTC() {
        LocalDateTime localDateTime = LocalDateTime.now(utcZone);
        return Timestamp.valueOf(localDateTime);
    }

    @Override
    public List<Settings> getAllProperties() {
        return settingsDao.findAll();
    }

    @Transactional
    @Override
    public void resetSettingToDefaultValue(Settings set) {
        String defValue = props.getProperty(set.getName());
        if (!StringCleanUtils.stringIsNullOrEmpty(defValue)) {
            set.setValue(defValue);
            settingsDao.save(set);
        }
    }

    @Transactional
    @Override
    public void saveProperty(Settings settings) {
        settingsDao.save(settings);
    }

    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public Jobs loadJob(String group, String name) {
       return joblistDao.findByGroupName(group, name);
    }

    @Override
    public List<Person> loadAllPersons() {
        return personDao.findAll();
    }

    @Transactional
    @Override
    public void saveJob(Jobs job) {
        joblistDao.merge(job);
    }

    @Override
    public Person findPersonById(Integer id) {
        return personDao.find(id);
    }

    @Transactional
    @Override
    public void updatePerson(Person person) {
        personDao.merge(person);
    }

    @Override
    public Map<String, Short> getCurrencyMap() {
        if (currencyMap == null) {
            currencyMap = new HashMap<>();
            currencyMapIDs = new HashMap<>();
            for (com.fxmind.entity.Currency currency: currencyDao.findAll()) {
                currencyMap.put(currency.getName(), currency.getId());
                currencyMapIDs.put(currency.getId(), currency.getName());
            }
        }
        return currencyMap;
    }

    @Override
    public Map<Short, String> getCurrencyMapIds() {
        return currencyMapIDs;
    }


    @Override
    public Map<String, Integer> getSymbolMap() {
        if (symbolMap == null) {
            symbolMap = new HashMap<>();
            symbolMapIDs = new HashMap<>();
            for (Symbol sym: symbolDao.findAll()) {
                symbolMap.put(sym.getName(), sym.getId());
                symbolMapIDs.put(sym.getId(), sym.getName());
            }
        }
        return symbolMap;
    }

}
