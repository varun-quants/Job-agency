package com.agency.repository.interfaces;

import com.agency.model.Jobseeker;

import java.util.List;

/**
 * Repository interface for Jobseeker operations
 */
public interface JobseekerRepository extends Repository<Jobseeker> {
    //To search for a seeker by name without actually be needing to know their id.
    List<Jobseeker> findByName(String name);
}
