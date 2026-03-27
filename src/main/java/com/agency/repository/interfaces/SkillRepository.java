package com.agency.repository.interfaces;

import com.agency.model.Skill;

import java.util.Optional;
/**
 * Repository interface for Skill entity operations.
 */
public interface SkillRepository extends Repository<Skill>{
    /**
     * Skills are unique by name in this system - there should
     * never be two skills both called "Linux", so because only one can
     * exist, Optional is the correct return type.
     *
     * Before creating a new skill, the service layer must check whether that skill
     * already exists- to prevent duplicates.
     * "Linux" and "Linux" should not be two separate skills
     * in the system, as this would corrupt the matching algorithm.
     */
    Optional<Skill> findByName(String name);
}
