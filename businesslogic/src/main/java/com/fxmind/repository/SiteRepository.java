package com.fxmind.repository;

import com.fxmind.domain.Site;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, Long> {

    Optional<Site> findByName(String name);
}
