package com.agency.model;

import java.util.Objects;

public class JobSkill {

    private int jobId;
    private int skillId;

    /** Only one constructor here because both Ids exist and are already known. */
    public JobSkill(int jobId, int skillId) {
        this.jobId = jobId;
        this.skillId = skillId;
    }

    /** Getters for both the fields and no setters because we are not setting any values for them we're simple taking the ids from different objects. */
    public int getJobId() {
        return jobId;
    }

    public int getSkillId() {
        return skillId;
    }

    /** equals() and hashCode() has both fields combined because it has to check the equality for the skills and that both fields together form a unique combination. */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof JobSkill)) return false;
        JobSkill jobSkill = (JobSkill) o;
        return this.jobId == jobSkill.jobId &&
                this.skillId == jobSkill.skillId;
    }

    @Override
    public int hashCode(){
        return Objects.hash(jobId, skillId);
    }

    public String toFileString() {
        return jobId + "|" + skillId;
    }

    public static JobSkill fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new JobSkill(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1])
        );
    }

    @Override
    public String toString() {
        return "JobSkill{" +
                "jobId=" + jobId +
                ", skillId=" + skillId +
                '}';
    }
}
