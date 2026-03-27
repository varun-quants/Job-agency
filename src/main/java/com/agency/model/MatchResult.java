package com.agency.model;

import java.util.ArrayList;
import java.util.List;

/** Represents the result of matching a job against a jobseeker*/
public class MatchResult {
    //All the fields are final so they are immutable and can never be changed
    private final Job job;
    private final Jobseeker jobSeeker;
    private final double score; // we'll be keeping this in percentage
    private final List<Skill> matchedSkills; // intersection of skills from both job and jobseeker
    private final List<Skill> missingSkills; // skills job needs but jobseeker lacks
    private final List<Skill> bonusSkills; //extra skills that jobseeker has but not required for the job

    public MatchResult(Job job, Jobseeker jobSeeker, double score, List<Skill> matchedSkills, List<Skill> missingSkills, List<Skill> bonusSkills) {
        this.job = job;
        this.jobSeeker = jobSeeker;
        this.score = score;

        // These are defensive copies - this object owns these lists, nobody else can touch them
        this.matchedSkills = new ArrayList<>(matchedSkills);
        this.missingSkills = new ArrayList<>(missingSkills);
        this.bonusSkills = new ArrayList<>(bonusSkills);
    }

    public Job getJob() {
        return job;
    }

    public Jobseeker getJobSeeker() {
        return jobSeeker;
    }

    public double getScore() {
        return score;
    }

    public List<Skill> getMatchedSkills() {
        return new ArrayList<>(matchedSkills); //defensive copy on the way out
    }

    public List<Skill> getMissingSkills() {
        return new ArrayList<>(missingSkills);
    }

    public List<Skill> getBonusSkills() {
        return new ArrayList<>(bonusSkills);
    }

    public boolean isFullMatch(){
        return Math.abs(this.score - 100.0) < 0.0001;
    }

    public String getSummary(){
        StringBuilder missingNames = new StringBuilder();
        for(int i=0; i < missingSkills.size(); i++) {
            missingNames.append(missingSkills.get(i).getName());

            if(i < missingSkills.size() - 1) {
                missingNames.append(", ");
            }
        }

        if(isFullMatch()) {
            return jobSeeker.getFullName() + " --- " + score + "% (perfect match)";
        } else {
            return jobSeeker.getFullName() + " --- " + score + "% (missing: " + missingNames + ")";
        }
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "job=" + job +
                ", jobSeeker=" + jobSeeker +
                ", score=" + score +
                ", matchedSkills=" + matchedSkills +
                ", missingSkills=" + missingSkills +
                ", bonusSkills=" + bonusSkills +
                '}';
    }
}
