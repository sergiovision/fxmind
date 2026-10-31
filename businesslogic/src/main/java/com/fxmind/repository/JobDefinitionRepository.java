package com.fxmind.repository;

import com.fxmind.domain.JobDefinition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDefinitionRepository extends JpaRepository<JobDefinition, Long> {

    List<JobDefinition> findByEnabledTrue();

    Optional<JobDefinition> findByGroupAndName(String group, String name);
}
