package com.agency.service;

import com.agency.model.Job;
import com.agency.model.Jobseeker;
import com.agency.model.Skill;
import com.agency.model.enums.JobStatus;
import com.agency.repository.impl.FileJobSkillRepository;
import com.agency.repository.impl.FileSeekerSkillRepository;
import com.agency.repository.interfaces.JobRepository;
import com.agency.repository.interfaces.JobseekerRepository;
import com.agency.repository.interfaces.SkillRepository;
import com.agency.util.IdGenerator;
import com.agency.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobseekerService {

        //three dependencies - injected through constructor
        private final JobseekerRepository jobseekerRepository;
        private final FileSeekerSkillRepository seekerSkillRepository;
        private final SkillRepository skillRepository;

        //constructor takes all three
        public JobseekerService(JobseekerRepository jobseekerRepository, FileSeekerSkillRepository seekerSkillRepository, SkillRepository skillRepository){
            this.jobseekerRepository = jobseekerRepository;
            this.seekerSkillRepository = seekerSkillRepository;
            this.skillRepository = skillRepository;
        }


        public Jobseeker registerSeeker(String fullName, String email, String phone, String location, List<Integer> skillIds){
            Validator.validateNotEmpty(fullName, "Full name");
            Validator.validateEmail(email);
            Validator.validatePhone(phone);
            Validator.validateNotEmpty(location, "Location");
            Validator.validateSkillListNotEmpty(skillIds);

            List<Skill> skills = resolveSkills(skillIds);

            int newId = IdGenerator.getInstance().nextId("seekers");
            Jobseeker seeker = new Jobseeker(newId, fullName, email, phone, location);

            jobseekerRepository.save(seeker);

            seekerSkillRepository.saveAll(newId, skills);

            return seeker;
        }


        public List<Jobseeker> getAllSeekers(){
            return jobseekerRepository.findAll();
        }


        public Optional<Jobseeker> getSeekerById(int id){
            return jobseekerRepository.findById(id);
        }


        public List<Skill> getSeekerSkills(int seekerId){
            if(!jobseekerRepository.exists(seekerId)){
                throw new IllegalArgumentException(
                        "No seeker found with ID : " + seekerId
                );
            }
            return seekerSkillRepository.findSkillsForSeeker(seekerId, skillRepository);
        }


        public void updateSeeker(int id, String fullName, String email, String phone, String location) {
            Validator.validateNotEmpty(fullName, "Full name");
            Validator.validateEmail(email);
            Validator.validatePhone(phone);
            Validator.validateNotEmpty(location, "Location");

            //load existing job - throw if not found
            Jobseeker existing = jobseekerRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Cannot update - no seeker found with ID: " +id
                    ));

            //apply updates to the loaded object
            //status is preserved - not changed by this method
            existing.setFullName(fullName);
            existing.setEmail(email);
            existing.setPhone(phone);
            existing.setLocation(location);

            //persist the updated object - rewrites entire jobs.dat
            jobseekerRepository.update(existing);
            System.out.println("Seeker updated successfully. ");
        }

        public void deleteSeeker(int id) {
            if(!jobseekerRepository.exists(id)){
                throw new IllegalArgumentException(
                        "Cannot delete - no seeker found with ID: " +id
                );
            }

            //remove skill relationships first
            seekerSkillRepository.deleteAllForSeeker(id);

            //remove the job record
            jobseekerRepository.delete(id);

            System.out.println("Seeker with iD: "+id+" deleted. ");
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