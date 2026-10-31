package com.fxmind.service;

import com.fxmind.domain.Setting;
import com.fxmind.exception.ResourceNotFoundException;
import com.fxmind.repository.SettingRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Runtime-editable settings stored in the database. */
@Service
@Transactional(readOnly = true)
public class SettingsService {

    private final SettingRepository settings;

    public SettingsService(SettingRepository settings) {
        this.settings = settings;
    }

    public List<Setting> findAll() {
        return settings.findAll(Sort.by("name"));
    }

    public Optional<String> value(String name) {
        return settings.findByName(name).map(Setting::getValue);
    }

    @Transactional
    public Setting update(String name, String value) {
        var setting = require(name);
        setting.setValue(value);
        return setting;
    }

    @Transactional
    public Setting resetToDefault(String name) {
        var setting = require(name);
        setting.resetToDefault();
        return setting;
    }

    private Setting require(String name) {
        return settings.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Setting", name));
    }
}
