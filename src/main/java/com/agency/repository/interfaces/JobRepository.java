package com.agency.repository.interfaces;

import com.agency.model.Job;
import com.agency.model.enums.JobStatus;

import java.util.List;

/**
 * JobRepository extending Repository<T> with specific query methods.
 * Extends Repository<Job> which means all six generic methods are inherited with T replaced by Job throughout.
 */
public interface JobRepository extends Repository<Job>{

    /**
     * Returns all jobs matching a specific status.
     * Logic is stored in repository because service does not need to know how jobs are filtered
     */
    List<Job> findByStatus(JobStatus status);

    /**
     * Returns all jobs in a specific location.
     *
     * To show the jobseeker jobs available in their location
     */
    List<Job> findByLocation(String location);
}
