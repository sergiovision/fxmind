package com.fxmind.service;

import com.fxmind.domain.JobDefinition;
import com.fxmind.exception.ResourceNotFoundException;
import com.fxmind.repository.JobDefinitionRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD and execution bookkeeping for persistent job definitions. */
@Service
@Transactional(readOnly = true)
public class JobDefinitionService {

    private final JobDefinitionRepository jobs;

    public JobDefinitionService(JobDefinitionRepository jobs) {
        this.jobs = jobs;
    }

    public List<JobDefinition> findAll() {
        return jobs.findAll(Sort.by("group", "name"));
    }

    public List<JobDefinition> findEnabled() {
        return jobs.findByEnabledTrue();
    }

    public Optional<JobDefinition> find(String group, String name) {
        return jobs.findByGroupAndName(group, name);
    }

    @Transactional
    public JobDefinition update(String group, String name, String cron, boolean enabled) {
        var job = require(group, name);
        job.setCron(cron);
        job.setEnabled(enabled);
        return job;
    }

    @Transactional
    public void recordExecution(String group, String name, String status, Instant firedAt, Instant nextFire) {
        jobs.findByGroupAndName(group, name).ifPresent(job -> job.recordExecution(status, firedAt, nextFire));
    }

    private JobDefinition require(String group, String name) {
        return jobs.findByGroupAndName(group, name)
                .orElseThrow(() -> new ResourceNotFoundException("Job", group + "." + name));
    }
}
