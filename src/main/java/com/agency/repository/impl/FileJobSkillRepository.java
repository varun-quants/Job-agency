package com.agency.repository.impl;

import com.agency.model.JobSkill;
import com.agency.model.Skill;
import com.agency.repository.interfaces.SkillRepository;
import com.agency.util.AppInitialiser;
import com.agency.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository to manage Job-to-Skill relationships
 * Does not extend Repository<T> because JobSkill has no single Id-
 * its identity is the combination of jobId + skillId
 */
public class FileJobSkillRepository {

    private static final String FILE_PATH = AppInitialiser.JOB_SKILLS_FILE;

    /**
     * Saves all skill links for a given job in one operation
     * Called by JobService after creating or updating a job's skill list.
     * Each skill becomes one line: "jobId|skillId"
     */
    public void saveAll(int jobId, List<Skill> skills){
        for(Skill skill : skills){
            JobSkill jobSkill = new JobSkill(jobId, skill.getId());
            FileUtil.appendLine(FILE_PATH, jobSkill.toFileString());
        }
    }

    /**
     * Returns all Skill objects linked to a given job.
     *
     * HOW IT WORKS:
     * Step1 - read all lines from job_skills.dat
     * Step2 - parse each line into a JobSkill object
     * Step3 - filter for lines where jobId matches
     * Step4 - for each matching skillId, lookup the full Skill object using the provided SkillRepository
     * Step5 - collect and return all found Skill objects
     */
    public List<Skill> findSkillsForJob(int jobId, SkillRepository skillRepo){
        List<String> lines = FileUtil.readAllLines(FILE_PATH);
        List<Skill> skills = new ArrayList<>();

        for(String line : lines){
            try{
                JobSkill jobSkill = JobSkill.fromFileString(line);

                //only processes relationships belonging to this job
                if(jobSkill.getJobId() == jobId) {

                    //look up the full Skill object by ID
                    Optional<Skill> skill = skillRepo.findById(jobSkill.getSkillId());

                    //only add if skill still exists - defensive against orphaned IDs
                    skill.ifPresent(skills::add);
                }
            } catch(Exception e) {
                System.err.println("Skipping corrupted job_skill line: "+line);
            }
        }
        return skills;
    }

    /**
     * Removes all skill links for a given job
     * Called by JobService before deleting a job, or when replacing
     * a job's entire skill list with a new one.
     */
    public void deleteAllForJob(int jobId){
        List<String> lines = FileUtil.readAllLines(FILE_PATH);
        List<String> remaining = new ArrayList<>();

        for(String line : lines) {
            try{
                JobSkill jobSkill = JobSkill.fromFileString(line);
                if(jobSkill.getJobId() != jobId){
                    remaining.add(line); //keep relationships for other jobs
                }
            } catch (Exception e){
                System.err.println("Skipping corrupted job_skill line: " + line);
            }
        }
        FileUtil.writeAllLines(FILE_PATH, remaining);
    }
}
