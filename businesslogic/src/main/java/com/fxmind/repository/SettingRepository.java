package com.fxmind.repository;

import com.fxmind.domain.Setting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    Optional<Setting> findByName(String name);
}
