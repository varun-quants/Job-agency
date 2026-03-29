package com.agency.repository.impl;

import com.agency.model.Jobseeker;
import com.agency.repository.interfaces.JobseekerRepository;
import com.agency.util.AppInitialiser;
import com.agency.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File based implementation of JobseekerRepository
 * Follows identical pattern to FileJobRepository
 */
public class FileJobseekerRepository implements JobseekerRepository {

    private static final String FILE_PATH = AppInitialiser.SEEKERS_FILE;

    /**Appends a new seeker record to the end of seekers.dat
     * appendLine is used - not writeAllLines - because we are
     * only adding, not modifying existing records.
     */
    @Override
    public void save(Jobseeker seeker){
        FileUtil.appendLine(FILE_PATH, seeker.toFileString());
    }

    /**
     * Loads every line from seekers.dat and parses each into a Jobseeker object
     */
    @Override
    public List<Jobseeker> findAll(){
        List<String> lines = FileUtil.readAllLines(FILE_PATH);
        List<Jobseeker> seekers = new ArrayList<>();

        for(String line : lines){
            try {
                seekers.add(Jobseeker.fromFileString(line));
            } catch(Exception e){
                //defensive reading - log, skip, never crash
                System.err.println("Skipping corrupted seeker line: " + line);
            }
        }
        return seekers;
    }

    /**
     * Stream filters all seekers in memory and returns the first match.
     */
    @Override
    public Optional<Jobseeker> findById(int id) {
        return findAll().stream()
                .filter(seeker -> seeker.getId() == id)
                .findFirst();
    }

    /**
     * Loads all seekers, replaces the matching record in memory,
     * then rewrites the entire file.
     * The file is only written once - after all modifications are made.
     */
    @Override
    public void update (Jobseeker updatedSeeker) {
        List<Jobseeker> allSeekers = findAll();
        List<String> lines = new ArrayList<>();

        for (Jobseeker seeker : allSeekers) {
            if (seeker.getId() == updatedSeeker.getId()) {
                lines.add(updatedSeeker.toFileString()); //replace with updated version
            } else {
                lines.add(seeker.toFileString()); //keep unchanged
            }
        }
        FileUtil.writeAllLines(FILE_PATH, lines);
    }

    /**
     * loads all seekers, skips the one matching the given id,
     * then rewrites the file without it.
     * the deleted record simple never gets added to the output list
     */
    @Override
    public void delete(int id){
        List<Jobseeker> allSeekers = findAll();
        List<String> lines = new ArrayList<>();

        for(Jobseeker seeker : allSeekers){
            if(seeker.getId() != id) {
                lines.add(seeker.toFileString()); //keep everything except deleted
            }
        }
        FileUtil.writeAllLines(FILE_PATH, lines);
    }

    /**
     * Delegates entirely to findById()
     * isPresent() returns true if Optional contains a value - record exists.
     */
    @Override
    public boolean exists(int id) {
        return findById(id).isPresent();
    }

    /**
     * findByName method() - case insensitive
     */
    @Override
    public List<Jobseeker> findByName(String name) {
        List<Jobseeker> result = new ArrayList<>();
        for(Jobseeker seeker : findAll()) {
            if(seeker.getFullName().toLowerCase().contains(name.toLowerCase())) {
                result.add(seeker);
            }
        }
        return result;
    }
}
