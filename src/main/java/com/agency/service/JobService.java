package com.agency.service;

import com.agency.model.Job;
import com.agency.model.Skill;
import com.agency.model.enums.JobStatus;
import com.agency.repository.impl.FileJobSkillRepository;
import com.agency.repository.interfaces.JobRepository;
import com.agency.repository.interfaces.SkillRepository;
import com.agency.util.IdGenerator;
import com.agency.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobService {

    //three dependencies - injected through constructor
    private final JobRepository jobRepository;
    private final FileJobSkillRepository jobSkillRepository;
    private final SkillRepository skillRepository;

    //constructor takes all three
    public JobService(JobRepository jobRepository, FileJobSkillRepository jobSkillRepository, SkillRepository skillRepository){
        this.jobRepository = jobRepository;
        this.jobSkillRepository = jobSkillRepository;
        this.skillRepository = skillRepository;
    }

    //create job and links skills - returns created Job
    public Job createJob(String title, String description, double salary, String location, List<Integer> skillIds){
        Validator.validateNotEmpty(title, "Job Title");
        Validator.validateNotEmpty(description, "Job Description");
        Validator.validatePositive(salary, "Salary");
        Validator.validateNotEmpty(location, "Location");
        Validator.validateSkillListNotEmpty(skillIds);

        List<Skill> skills = resolveSkills(skillIds);

        int newId = IdGenerator.getInstance().nextId("jobs");
        Job job = new Job(newId, title, description, salary, location, JobStatus.OPEN);

        jobRepository.save(job);

        jobSkillRepository.saveAll(newId, skills);

        return job;
    }

    //returns every job in the system regardless of status
    public List<Job> getAllJobs(){
        return jobRepository.findAll();
    }

    //returns only jobs with open status
    public List<Job> getOpenJobs(){
        return jobRepository.findByStatus(JobStatus.OPEN);
    }

    //return a single job by id wrapped in optional
    public Optional<Job> getJobById(int id){
        return jobRepository.findById(id);
    }

    //return all skills linked to a specific job
    public List<Skill> getJobSkills(int jobId){
        //validates job exists before attempting skill lookup
        if(!jobRepository.exists(jobId)){
            throw new IllegalArgumentException(
                    "No job found with ID : " + jobId
            );
        }
        return jobSkillRepository.findSkillsForJob(jobId, skillRepository);
    }

    //updates core details of an existing job
    public void updateJob(int id, String title, String description, double salary, String location) {
        Validator.validateNotEmpty(title, "Job Title");
        Validator.validateNotEmpty(description, "Job description");
        Validator.validatePositive(salary, "Salary");
        Validator.validateNotEmpty(location, "Location");

        //load existing job - throw if not found
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot update - no job found with ID: " +id
                ));

        //apply updates to the loaded object
        //status is preserved - not changed by this method
        existingJob.setTitle(title);
        existingJob.setDescription(description);
        existingJob.setSalary(salary);
        existingJob.setLocation(location);

        //persist the updated object - rewrites entire jobs.dat
        jobRepository.update(existingJob);
    }

    //change the status of job -OPEN, CLOSED, or FILLED
    public void updateJobStatus(int id, JobStatus newStatus){
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot update status - no job found with ID: "+id
                ));

        existingJob.setStatus(newStatus);
        jobRepository.update(existingJob);

        System.out.println("Job status updated to: "+newStatus.name());
    }

    public void deleteJob(int id) {
        //verify job exists before performing deletion
        if(!jobRepository.exists(id)){
            throw new IllegalArgumentException(
                    "Cannot delete - no job found with ID: " +id
            );
        }

        //remove skill relationships first
        jobSkillRepository.deleteAllForJob(id);

        //remove the job record
        jobRepository.delete(id);

        System.out.println("Job with iD: "+id+" and all its skill links deleted. ");
    }

    private List<Skill> resolveSkills(List<Integer> skillIds) {
        List<Skill> skills = new ArrayList<>();

        for(int skillId : skillIds) {
            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Skill ID does not exist: "+ skillId +". Please create the skill first."
                    ));
            skills.add(skill);
        }
        return skills;
    }
}
