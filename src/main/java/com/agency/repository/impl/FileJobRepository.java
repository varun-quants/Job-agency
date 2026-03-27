package com.agency.repository.impl;

import com.agency.model.Job;
import com.agency.model.enums.JobStatus;
import com.agency.repository.interfaces.JobRepository;
import com.agency.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.agency.util.AppInitialiser.JOBS_FILE;

public class FileJobRepository implements JobRepository {

    /**Save() Method
     * toFileString() does the serialization, appendLine() does the writing.
     */
    public void save(Job job) {
        FileUtil.appendLine(JOBS_FILE, job.toFileString());
    }

    /**findById() Method*/
    // get List<Job>, convert list to stream for processing, keep only jobs where id matches,  return first match as Optional<Job> - returns Optional.empty() if none found.
    public Optional<Job> findById(int id) {
        return findAll()
                .stream()
                .filter(job -> job.getId() == id)
                .findFirst();
    }

    /**Every method will be built on top of findAll()
     */
    public List<Job> findAll(){
        List<String> lines = FileUtil.readAllLines(JOBS_FILE);
        List<Job> jobs = new ArrayList<>();

        for(String line : lines){
            try{
                jobs.add(Job.fromFileString(line));
            } catch(Exception e) {
                //defensive reading - skip corrupted lines, never crash
                System.err.println("Skipping corrupted line: " +line);
            }
        }
        return jobs;
    }

    /**Update() method - read, modify and write cycle */
    public void update(Job updatedJob) {
        List<Job> allJobs = findAll();
        List<String> lines = new ArrayList<>();

        for(Job job : allJobs) {
            if(job.getId() == updatedJob.getId()) {
                lines.add(updatedJob.toFileString()); //replace with updated
            } else {
                lines.add(job.toFileString()); //keep original
            }
        }
        FileUtil.writeAllLines(JOBS_FILE, lines);
    }

    /**Delete() Method - has the same pattern as update but instead of replacing,
     * it simply skips the record with the matching ID.
     */
    public void delete(int id){
        List<Job> allJobs = findAll();
        List<String> lines = new ArrayList<>();

        for(Job job : allJobs){
            if(job.getId() != id) {
                lines.add(job.toFileString()); //keep everything except deleted
            }
        }
        FileUtil.writeAllLines(JOBS_FILE, lines);
    }

    /**Exists() method - isPresent() returns true if the Optional contains a value - meaning the record was found. */
    public boolean exists(int id) {
        return findById(id).isPresent();
    }

    /**The findByStatus() Method comparing the status from input param with status of all jobs
     * and storing them into an Arraylist object result if the status matches
     */
    public List<Job> findByStatus(JobStatus status) {
        List<Job> result = new ArrayList<>();
        for(Job job : findAll()) {
            //using == for enum comparison
            if(job.getStatus() == status) {
                result.add(job);
            }
        }
        return result;
    }

    /**findByLocation() method */
    public List<Job> findByLocation(String location) {
        List<Job> result = new ArrayList<>();
        for(Job job : findAll()) {
            if(job.getLocation().equalsIgnoreCase(location)) {
                result.add(job);
            }
        }
        return result;
    }
}
