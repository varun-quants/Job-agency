package com.agency.service;

import com.agency.model.Skill;
import com.agency.repository.interfaces.SkillRepository;
import com.agency.util.IdGenerator;
import com.agency.util.Validator;

import java.util.List;
import java.util.Optional;

/**
 * Business logic related to Skills is made
 *
 * Service layer basically validates input, enforces business rules, coordinates repositories,
 * and ensures the system stays in a consistent state
 *
 * SkillRepository is injected through the constructor.
 * This class never creates its own repository internally.
 * This keeps the service loosely coupled - it works with any implementation
 * of SkillRepository, not just the file based one.
 */
public class SkillService {

    //final so that repository reference never changes after construction
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository){
        this.skillRepository = skillRepository;
    }


    public Skill createSkill(String name){
        //Validate input
        Validator.validateNotEmpty(name, "Skill name");

        //duplicate check using findByName()
        if(skillRepository.findByName(name).isPresent()){
            throw new IllegalArgumentException(
                    "Skill already exists: " +name+ ". Use the existing skill instead of creating a duplicate."
            );
        }
        //generate a new uid
        int newId = IdGenerator.getInstance().nextId("skills");

        //construct the skill using the reconstitution constructor
        Skill skill = new Skill(newId, name);

        //persist to skills.dat via repository
        skillRepository.save(skill);

        //returning the complete object so caller has the assigned ID
        return skill;
    }

    /**
     * Returns all skills in the system
     * Used by the console menu to display a numbered list of skills
     * so the user can select skills by number when creating jobs or seekers
     * Also used by other services that need the full skill catalogue.
     */
    public List<Skill> getAllSkills(){
        return skillRepository.findAll();
    }

    /**
     * Returns a single skill by its numeric id
     */
    public Optional<Skill> getSkillsById(int id) {
        return skillRepository.findById(id);
    }

    /**
     * Finds a skill by its exact name - case-insensitive
     */
    public Optional<Skill> findByName(String name){
        //validate before querying
        Validator.validateNotEmpty(name, "Skill name");
        return skillRepository.findByName(name);
    }

    /**
     * Deletes skill by ID
     *
     * At this phase it deletes the skill without checking its references with jobs or seekers
     */
    public void deleteSkill(int id){
        //check skill exists before attempting deletion
        if(!skillRepository.exists(id)) {
            throw new IllegalArgumentException(
                    "Cannot delete - no skill found with ID: " +id
            );
        }

        skillRepository.delete(id);
        System.out.println("Skill with ID " +id+ "deleted successfully. ");
    }
}
