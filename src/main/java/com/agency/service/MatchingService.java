package com.agency.service;

import com.agency.model.Job;
import com.agency.model.Jobseeker;
import com.agency.model.MatchResult;
import com.agency.model.Skill;

import java.util.*;

public class MatchingService {

    //minimum score a seeker must achieve to appear in results
    //expressed as a constant - easy to change without touching algorithm
    private static final double MATCH_THRESHOLD = 50.0;

    private final JobService jobService;
    private final JobseekerService jobseekerService;

    //Constructor injection
    public MatchingService(JobService jobService, JobseekerService jobseekerService){
        this.jobService = jobService;
        this.jobseekerService = jobseekerService;
    }

    public List<MatchResult> matchJobToSeekers(int jobId) {

        //load the job
        Job job = jobService.getJobById(jobId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No job found with ID: " +jobId
                ));

        //get required skills for this job as a HashSet
        List<Skill> requiredList = jobService.getJobSkills(jobId);
        Set<Skill> requiredSet = new HashSet<>(requiredList);

        //nothing to match against if job has no skills
        if(requiredSet.isEmpty()){
            return new ArrayList<>();
        }

        List<MatchResult> results = new ArrayList<>();

        //iterate every seeker and compute their match score
        for(Jobseeker seeker : jobseekerService.getAllSeekers()) {

            List<Skill> offeredList = jobseekerService.getSeekerSkills(seeker.getId());
            Set<Skill> offeredSet = new HashSet<>(offeredList);

            //computing the three set operations
            //matched - skills both job and seeker share
            Set<Skill> matched = new HashSet<>(requiredSet);
            matched.retainAll(offeredSet); //keeps only elements in BOTH sets

            //missing - skills job needs but seeker lacks
            Set<Skill> missing = new HashSet<>(requiredSet);
            missing.removeAll(offeredSet); // removes anything seeker has

            //bonus - extra skills seeker has
            Set<Skill> bonus = new HashSet<>(offeredSet);
            missing.removeAll(requiredSet); // removes anything job requires

            //score = matched/ required * 100
            double score = ((double) matched.size() / requiredSet.size()) * 100.0;

            //only include results above threshold
            if(score >= MATCH_THRESHOLD) {
                results.add(new MatchResult(
                        job,
                        seeker,
                        score,
                        new ArrayList<>(matched),
                        new ArrayList<>(missing),
                        new ArrayList<>(bonus)

                ));
            }
        }

        //sort by descending order - best match first
        results.sort(
                Comparator.comparingDouble(MatchResult::getScore).reversed()
                        .thenComparingInt(r -> r.getMissingSkills().size())
        );

        return results;
    }

    public List<MatchResult> matchSeekerToJobs(int seekerId) {

        Jobseeker seeker = jobseekerService.getSeekerById(seekerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No seeker found with ID: " +seekerId
                ));

        List<Skill> offeredList = jobseekerService.getSeekerSkills(seekerId);
        Set<Skill> offeredSet = new HashSet<>(offeredList);

        List<MatchResult> results = new ArrayList<>();

        for(Job job : jobService.getOpenJobs()) {

            List<Skill> requiredList = jobService.getJobSkills(job.getId());
            Set<Skill> requiredSet = new HashSet<>(requiredList);

            if(requiredSet.isEmpty()) continue;

            //computing the three set operations
            //matched - skills both job and seeker share
            Set<Skill> matched = new HashSet<>(requiredSet);
            matched.retainAll(offeredSet); //keeps only elements in BOTH sets

            //missing - skills job needs but seeker lacks
            Set<Skill> missing = new HashSet<>(requiredSet);
            missing.removeAll(offeredSet); // removes anything seeker has

            //bonus - extra skills seeker has
            Set<Skill> bonus = new HashSet<>(offeredSet);
            missing.removeAll(requiredSet); // removes anything job requires

            //score = matched/ required * 100
            double score = ((double) matched.size() / requiredSet.size()) * 100.0;

            //only include results above threshold
            if(score >= MATCH_THRESHOLD) {
                results.add(new MatchResult(
                        job,
                        seeker,
                        score,
                        new ArrayList<>(matched),
                        new ArrayList<>(missing),
                        new ArrayList<>(bonus)

                ));
            }
        }

        //sort by descending order - best match first
        results.sort(
                Comparator.comparingDouble(MatchResult::getScore).reversed()
                        .thenComparingInt(r -> r.getMissingSkills().size())
        );

        return results;
    }

    public List<Job> findUnmatchedJobs() {
        List<Job> unmatched = new ArrayList<>();

        for(Job job : jobService.getOpenJobs()) {
            List<MatchResult> matches = matchJobToSeekers(job.getId());
            if(matches.isEmpty()) {
                unmatched.add(job);
            }
        }
        return unmatched;
    }

    public List<Jobseeker> findUnmatchedSeekers() {
        List<Jobseeker> unmatched = new ArrayList<>();

        for(Jobseeker seeker : jobseekerService.getAllSeekers()) {
            List<MatchResult> matches = matchSeekerToJobs(seeker.getId());
            if(matches.isEmpty()) {
                unmatched.add(seeker);
            }
        }
        return unmatched;
    }
}
