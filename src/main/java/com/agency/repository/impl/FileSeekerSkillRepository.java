package com.agency.repository.impl;

import com.agency.model.SeekerSkill;
import com.agency.model.Skill;
import com.agency.repository.interfaces.SkillRepository;
import com.agency.util.AppInitialiser;
import com.agency.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Seeker-to-Skill relationships
 *
 * Stores and retrieves which skills each jobseeker offers
 * Used by Matching Service to retrieve a seeker's skill set
 * for each intersection against job requirements
 */
public class FileSeekerSkillRepository {

    private static final String FILE_PATH = AppInitialiser.SEEKER_SKILLS_FILE;

    /**
     * Saves all skill links for a given seeker in one operation
     * Each skill becomes one line: "seekerId|skillId"
     * Called by JobseekerService after registering or updating a seeker.
     */
    public void saveAll(int seekerId, List<Skill> skills) {
        for(Skill skill : skills){
            SeekerSkill seekerSkill = new SeekerSkill(seekerId, skill.getId());
            FileUtil.appendLine(FILE_PATH, seekerSkill.toFileString());
        }
    }

    /**
     * Returns all skill objects linked to a given seeker
     * Identical flow to findSkillsForJob() - filter by seekerId,
     * resolve each skillId to a full Skill object via SkillRepository.
     */
    public List<Skill> findSkillsForSeeker(int seekerId, SkillRepository skillRepo) {
        List<String> lines = FileUtil.readAllLines(FILE_PATH);
        List<Skill> skills = new ArrayList<>();

        for(String line : lines) {
            try{
                SeekerSkill seekerSkill = SeekerSkill.fromFileString(line);

                if(seekerSkill.getSeekerId() == seekerId) {
                    Optional<Skill> skill = skillRepo.findById(seekerSkill.getSkillId());
                    skill.ifPresent(skills::add); //add only if skill exists
                }
            } catch (Exception e){
                System.err.println("Skipping corrupted seeker_skill line: "+line);
            }
        }
        return skills;
    }

    /**
     * Removes all skill links for a given seeker.
     * Called before deleting a seeker or replacing their skill list entirely.
     * Keeps all lines where seekerId does not match.
     */
    public void deleteAllForSeeker(int seekerId) {
        List<String> lines = FileUtil.readAllLines(FILE_PATH);
        List<String> remaining = new ArrayList<>();

        for(String line: lines) {
            try{
                SeekerSkill seekerSkill = SeekerSkill.fromFileString(line);
                if(seekerSkill.getSeekerId() != seekerId) {
                    remaining.add(line);
                }
            } catch(Exception e){
                System.err.println("Skipping corrupted seeker_skill line: "+line);
            }
        }
        FileUtil.writeAllLines(FILE_PATH, remaining);
    }
}
