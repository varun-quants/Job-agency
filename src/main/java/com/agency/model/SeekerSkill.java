package com.agency.model;

import java.util.Objects;

public class SeekerSkill {

    private int seekerId;
    private int skillId;

    /** Only one constructor here because both Ids exist and are already known. */
    public SeekerSkill(int seekerId, int skillId) {
        this.seekerId = seekerId;
        this.skillId = skillId;
    }

    /** Getters for both the fields and no setters because we are not setting any values for them we're simple taking the ids from different objects. */
    public int getSeekerId() {
        return seekerId;
    }

    public int getSkillId() {
        return skillId;
    }

    /** equals() and hashCode() has both fields combined because it has to check the equality for the skills and that both fields together form a unique combination. */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof SeekerSkill)) return false;
        SeekerSkill seekerSkill = (SeekerSkill) o;
        return this.seekerId == seekerSkill.seekerId &&
                this.skillId == seekerSkill.skillId;
    }

    @Override
    public int hashCode(){
        return Objects.hash(seekerId, skillId);
    }

    public String toFileString() {
        return seekerId + "|" + skillId;
    }

    public static SeekerSkill fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new SeekerSkill(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1])
        );
    }

    @Override
    public String toString() {
        return "SeekerSkill{" +
                "seekerId=" + seekerId +
                ", skillId=" + skillId +
                '}';
    }
}
