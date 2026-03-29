package com.agency.repository.impl;

import com.agency.model.Skill;
import com.agency.repository.interfaces.SkillRepository;
import com.agency.util.AppInitialiser;
import com.agency.util.FileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * File based implementation of SkillRepository
 * Skills here are foundation of the entire matching system - every job and every seeker references skills by ID.
 */
public class FileSkillRepository implements SkillRepository {

    private static final String FILE_PATH = AppInitialiser.SKILLS_FILE;

    /**
     * Appends a new skill to skill.dat.
     * The service layer is responsible for checking duplicates
     * before calling save() - this method trusts valid input
     */
    @Override
    public void save(Skill skill) {
        FileUtil.appendLine(FILE_PATH, skill.toFileString());
    }

    /**
     * Reads all skills from file into mem
     */
    @Override
    public List<Skill> findAll(){
        List<String> lines = FileUtil.readAllLines(FILE_PATH);
        List<Skill> skills = new ArrayList<>();

        for(String line: lines) {
            try {
                skills.add(Skill.fromFileString(line));
            } catch (Exception e) {
                System.err.println("Skipping corrupted skill line: "+line);
            }
        }
        return skills;
    }

    /**
     * Finds a skill by its numeric id using stream
     * Returns Optional - the skill may not exist
     */
    @Override
    public Optional<Skill> findById(int id){
        return findAll().stream().filter(skill -> skill.getId() == id).findFirst();
    }

    /**
     * Finds a skill by its name - case-insensitive.
     * Returns Optional because Skill names are unique in this system
     * Used by SkillService before creating a new skill to prevent duplicates
     *  if findByName("Linux").isPresent() -> skill already exists, do not create
     */
    @Override
    public Optional<Skill> findByName(String name) {
        return findAll().stream().filter(skill -> skill.getName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Replaces the matching skill record with updated data.
     * Full file rewrite - necessary because file records are variable length
     */
    @Override
    public void update(Skill updatedSkill) {
        List<Skill> allSkills = findAll();
        List<String> lines = new ArrayList<>();

        for(Skill skill : allSkills) {
            if(skill.getId() == updatedSkill.getId()) {
                lines.add(updatedSkill.toFileString());
            } else {
                lines.add(skill.toFileString());
            }
        }
        FileUtil.writeAllLines(FILE_PATH, lines);
    }

    /**
     * Removes the skill with the given ID from file
     * The service layer will need to ensure no jobs or jobseekers still reference this skill before we delete it
     */
    @Override
    public void delete(int id) {
        List<Skill> allSkills = findAll();
        List<String> lines = new ArrayList<>();

        for(Skill skill: allSkills){
            if(skill.getId() != id){
                lines.add(skill.toFileString());
            }
        }
        FileUtil.writeAllLines(FILE_PATH, lines);
    }

    @Override
    public boolean exists(int id){
        return findById(id).isPresent();
    }
}
