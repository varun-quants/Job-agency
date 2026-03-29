package com.agency.ui;

import com.agency.model.Job;
import com.agency.model.Jobseeker;
import com.agency.model.MatchResult;
import com.agency.model.Skill;
import com.agency.model.enums.JobStatus;
import com.agency.repository.impl.*;
import com.agency.service.JobService;
import com.agency.service.JobseekerService;
import com.agency.service.MatchingService;
import com.agency.service.SkillService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {

    //scanner shared across all menus - one instance for entire session
    private final Scanner scanner = new Scanner(System.in);

    //repositories
    private final FileSkillRepository skillRepository = new FileSkillRepository();
    private final FileJobRepository jobRepository = new FileJobRepository();
    private final FileJobseekerRepository seekerRepository = new FileJobseekerRepository();
    private final FileJobSkillRepository jobSkillRepo = new FileJobSkillRepository();
    private final FileSeekerSkillRepository seekerSkillRepo = new FileSeekerSkillRepository();

    //services - repositories injected
    private final SkillService skillService = new SkillService(skillRepository);
    private final JobService jobService = new JobService(jobRepository, jobSkillRepo, skillRepository);
    private final JobseekerService seekerService = new JobseekerService(seekerRepository, seekerSkillRepo, skillRepository);
    private final MatchingService matchingService = new MatchingService(jobService, seekerService);

    /**
     * Entry point - starts the main menu loop
     * Runs until user exits
     */
    public void start(){
        System.out.println("||=================================||");
        System.out.println("||         VARUN JOB AGENCY        ||");
        System.out.println("||=================================||");

        boolean running = true;

        while(running){
            printMainMenu();
            int choice = readInt("Enter choice: ");

            switch(choice) {
                case 1 -> manageSkills();
                case 2 -> manageJobs();
                case 3 -> manageSeekers();
                case 4 -> runReports();
                case 0 -> running = false;
                default -> System.out.println("Invalid choice. Try again.");

            }
        }

        System.out.println("See you again! Thanks for visiting.");
        scanner.close();
    }

    //MAIN MENU
    private void printMainMenu(){
        System.out.println("\n||=============================||");
        System.out.println("||          MAIN MENU          ||");
        System.out.println("||=============================||");
        System.out.println(" Press the numbers to continue :");
        System.out.println(" 1. Manage Skills ");
        System.out.println(" 2. Manage Jobs ");
        System.out.println(" 3. Manage Job Seekers ");
        System.out.println(" 4. Reports ");
        System.out.println(" 0. Exit ");
        System.out.println("||=============================||");
    }

    //SKILL MANAGEMENT
    private void manageSkills(){
        boolean back = false;
        while(!back) {
            System.out.println("\n ----SKILL MANAGEMENT---- ");
            System.out.println(" Press the numbers to continue :");
            System.out.println(" 1. Add Skill ");
            System.out.println(" 2. View All Skills ");
            System.out.println(" 3. Delete Skill ");
            System.out.println(" 0. Back ");

            int choice = readInt("Enter choice: ");
            switch(choice) {
                case 1 -> addSkill();
                case 2 -> viewAllSkills();
                case 3 -> deleteSkill();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addSkill(){
        String name = readString("Enter skill name: ");
        try{
            Skill skill = skillService.createSkill(name);
            System.out.println(" Skill successfully created - ID: "+skill.getId() + ", Name: "+skill.getName());
        } catch (IllegalArgumentException e){
            System.out.println(" Error : "+e.getMessage());
        }
    }

    private void viewAllSkills(){
        List<Skill> skills = skillService.getAllSkills();
        if(skills.isEmpty()) {
            System.out.println("No skills registered yet.");
            return;
        }
        System.out.println("\n ----ALL SKILLS---- ");
        for(Skill skill : skills){
            System.out.println("  ["+skill.getId()+"] " +skill.getName());
        }
    }

    private void deleteSkill(){
        viewAllSkills();
        int id = readInt("Enter skill ID to delete: ");
        try {
            skillService.deleteSkill(id);
        } catch (IllegalArgumentException e){
            System.out.println(" Error: "+e.getMessage());
        }
    }

    //JOB MANAGEMENT

    private void manageJobs(){
        boolean back =false;
        while(!back) {
            System.out.println("\n ----JOB MANAGEMENT---- ");
            System.out.println(" 1. Add Job ");
            System.out.println(" 2. View All Jobs ");
            System.out.println(" 3. Update Job ");
            System.out.println(" 4. Change Job Status ");
            System.out.println(" 5. Delete Job ");
            System.out.println(" 0. Back ");

            int choice = readInt("Enter choice: ");
            switch(choice){
                case 1 -> addJob();
                case 2 -> viewAllJobs();
                case 3 -> updateJob();
                case 4 -> changeJobStatus();
                case 5 -> deleteJob();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice. ");
            }
        }
    }

    private void addJob() {
        String title = readString("Job title: ");
        String description = readString("Description: ");
        double salary = readDouble("Salary: ");
        String location = readString("Location: ");

        //display available skills so user can select by ID
        viewAllSkills();
        List<Integer> skillIds = readSkillIds();

        try {
            Job job = jobService.createJob(title, description, salary, location, skillIds);
            System.out.println("Job created successfully - ID: "+job.getId()+", Title: "+job.getTitle());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: "+e.getMessage());
        }
    }

    private void viewAllJobs(){
        List<Job> jobs = jobService.getAllJobs();
        if(jobs.isEmpty()){
            System.out.println("No jobs registered yet. ");
            return;
        }
        System.out.println("\n ----ALL JOBS---- ");
        for(Job job: jobs){
            List<Skill> skills = jobService.getJobSkills(job.getId());
            System.out.println(" ["+job.getId()+"] "+job.getTitle() +
                    " | " + job.getLocation() +
                    " | $" + job.getSalary() +
                    " | " + job.getStatus().name() +
                    " | Skills: " + skillNames(skills)
                    );
        }
    }

    private void updateJob() {
        viewAllJobs();
        int id = readInt("Enter job ID to update: ");
        String title = readString("New Title: ");
        String description = readString("New description: ");
        double salary = readDouble("New salary: ");
        String location = readString("New location: ");

        try{
            jobService.updateJob(id, title, description, salary, location);
            System.out.println("Job updated successfully. ");
        } catch (IllegalArgumentException e){
            System.out.println(" Error: "+ e.getMessage());
        }
    }

    private void changeJobStatus(){
        viewAllJobs();
        int id = readInt("Enter job ID: ");
        System.out.println(" 1. OPEN  2. CLOSED  3. FILLED ");
        int choice = readInt("New status: ");

        JobStatus status = switch (choice) {
            case 1 -> JobStatus.OPEN;
            case 2 -> JobStatus.CLOSED;
            case 3 -> JobStatus.FILLED;
            default -> null;
        };

        if(status == null) {
            System.out.println("Invalid status choice. ");
            return;
        }

        try {
            jobService.updateJobStatus(id, status);
        } catch(IllegalArgumentException e){
            System.out.println(" Error: "+e.getMessage());
        }
    }

    private void deleteJob(){
        viewAllJobs();
        int id = readInt("Enter job ID to delete: ");
        try {
            jobService.deleteJob(id);
        } catch(IllegalArgumentException e){
            System.out.println(" Error: "+e.getMessage());
        }
    }

    //SEEKER MANAGEMENT

    private void manageSeekers(){
        boolean back = false;
        while(!back){
            System.out.println("\n ----SEEKER MANAGEMENT---- ");
            System.out.println(" 1. Register Seeker ");
            System.out.println(" 2. View All Seekers ");
            System.out.println(" 3. Update Seeker ");
            System.out.println(" 4. Delete Seeker ");
            System.out.println(" 0. Back ");

            int choice = readInt("Enter choice: ");
            switch(choice) {
                case 1 -> registerSeeker();
                case 2 -> viewAllSeekers();
                case 3 -> updateSeeker();
                case 4 -> deleteSeeker();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice. ");
            }
        }
    }

    private void registerSeeker() {
        String fullName = readString("Full name: ");
        String email = readString("Email: ");
        String phone = readString("Phone: ");
        String location = readString("Location: ");

        viewAllSkills();
        List<Integer> skillIds = readSkillIds();

        try{
            Jobseeker seeker = seekerService.registerSeeker(
                    fullName, email, phone, location, skillIds
            );
            System.out.println("Seeker registered -ID: "+ seeker.getId()+ ", Name: "+seeker.getFullName());
        } catch(IllegalArgumentException e) {
            System.out.println("Error: "+e.getMessage());
        }
    }
    private void viewAllSeekers() {
        List<Jobseeker> seekers = seekerService.getAllSeekers();
        if(seekers.isEmpty()) {
            System.out.println("No seekers registered yet.");
            return;
        }
        System.out.println("\n ----ALL SEEKERS---- ");
        for(Jobseeker seeker : seekers) {
            List<Skill> skills = seekerService.getSeekerSkills(seeker.getId());
            System.out.println(" ["+seeker.getId()+"] "+seeker.getFullName() +
                    " | "+seeker.getEmail()+
                    " | "+seeker.getLocation()+
                    " | Skills: "+skillNames(skills)
                    );
        }
    }

    private void updateSeeker() {
        viewAllSeekers();
        int id = readInt("Enter seeker ID to update: ");
        String fullName = readString("New full name: ");
        String email = readString("New email: ");
        String phone = readString("New phone: ");
        String location = readString("New location: ");

        try{
            seekerService.updateSeeker(id, fullName, email, phone, location);
            System.out.println("Seeker updated successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: "+e.getMessage());
        }
    }

    private void deleteSeeker(){
        viewAllSeekers();
        int id = readInt("Enter seeker ID to delete: ");
        try{
            seekerService.deleteSeeker(id);
        } catch(IllegalArgumentException e) {
            System.out.println("Error: "+e.getMessage());
        }
    }

    //Reports
    private void runReports(){
        boolean back = false;
        while(!back){
            System.out.println("\n ----REPORTS---- ");
            System.out.println("1. Match Job to Seekers ");
            System.out.println("2. Match Seeker to Jobs ");
            System.out.println("3. All Open Jobs ");
            System.out.println("4. All Seekers ");
            System.out.println("5. Unmatched Jobs ");
            System.out.println("6. Unmatched Seekers ");
            System.out.println("0. Back ");

            int choice = readInt("Enter choice: ");
            switch(choice) {
                case 1 -> reportJobToSeekers();
                case 2 -> reportSeekerToJobs();
                case 3 -> viewAllJobs();
                case 4 -> viewAllSeekers();
                case 5 -> reportUnmatchedJobs();
                case 6 -> reportUnmatchedSeekers();
                case 0 -> back = true;
                default -> System.out.println("Invalid choice. ");
            }
        }
    }

    private void reportJobToSeekers(){
        viewAllJobs();
        int jobId = readInt("Enter job ID to match: ");

        try{
            List<MatchResult> results = matchingService.matchJobToSeekers(jobId);

            Optional<Job> job = jobService.getJobById(jobId);
            System.out.println("\n==========================");
            System.out.println("     JOB MATCH REPORT     " + job.map(Job::getTitle).orElse("Unknown"));
            System.out.println("\n==========================");

            if(results.isEmpty()){
                System.out.println("No matching seekers above 50% threshold. ");
            } else {
                System.out.printf("   %-4s %-20s %-8s %s%n",
                        "RANK", "CANDIDATE", "SCORE", "MISSING SKILLS");
                System.out.println("==========================");
                int rank = 1;
                for(MatchResult r : results){
                    System.out.printf("  %-4d %-20s %-8.1f %s%n",
                            rank++,
                            r.getJobSeeker().getFullName(),
                            r.getScore(),
                            r.getMissingSkills().isEmpty() ? "None" :
                            skillNames(r.getMissingSkills()));
                }
            }

            System.out.println("==============================");
            System.out.println("     Total matches: "+results.size());
            System.out.println("==============================");
        } catch(IllegalArgumentException e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    private void reportSeekerToJobs(){
        viewAllSeekers();
        int seekerId = readInt("Enter seeker ID to match: ");

        try{
            List<MatchResult> results = matchingService.matchSeekerToJobs(seekerId);
            Optional<Jobseeker> seeker = seekerService.getSeekerById(seekerId);

            System.out.println("==============================");
            System.out.println("  SEEKER MATCH REPORT - "+
                    seeker.map(Jobseeker::getFullName).orElse("Unknown"));
            System.out.println("==============================");

            if(results.isEmpty()){
                System.out.println("No matching seekers above 50% threshold. ");
            } else {
                System.out.printf("   %-4s %-20s %-8s %s%n",
                        "RANK", "JOB TITLE", "SCORE", "MISSING SKILLS");
                System.out.println("==========================");
                int rank = 1;
                for(MatchResult r : results){
                    System.out.printf("  %-4d %-20s %-8.1f %s%n",
                            rank++,
                            r.getJob().getTitle(),
                            r.getScore(),
                            r.getMissingSkills().isEmpty() ? "None" :
                                    skillNames(r.getMissingSkills()));
                }
            }

            System.out.println("==============================");
            System.out.println("     Total matches: "+results.size());
            System.out.println("==============================");
        } catch(IllegalArgumentException e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    private void reportUnmatchedJobs() {
        List<Job> unmatched = matchingService.findUnmatchedJobs();
        System.out.println("\n ----UNMATCHED JOBS---- ");
        if(unmatched.isEmpty()) {
            System.out.println("All open jobs have at least one matching seeker. ");
        } else {
            for(Job job: unmatched) {
                System.out.println(" ["+job.getId()+"] "+job.getTitle()+ " | "+job.getLocation());
            }
        }
    }

    private void reportUnmatchedSeekers() {
        List<Jobseeker> unmatched = matchingService.findUnmatchedSeekers();
        System.out.println("\n ----UNMATCHED SEEKERS---- ");
        if(unmatched.isEmpty()) {
            System.out.println("All seekers match at least one open job. ");
        } else {
            for(Jobseeker seeker: unmatched) {
                System.out.println(" ["+seeker.getId()+"] "+seeker.getFullName()+ " | "+seeker.getLocation());
            }
        }
    }

    //UTILITY HELPERS

    //reads a non-empty string from the console
    //loops until user provides something non-blank
    private String readString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input =scanner.nextLine().trim();
            if(input.isEmpty()) {
                System.out.println("Input cannot be empty. Try again. ");
            }
        } while(input.isEmpty());
        return input;
    }

    //reads an integer from the console
    //loops until user provides a valid integer
    private int readInt(String prompt) {
        while(true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch(NumberFormatException e) {
                System.out.println("Please enter a valid number. ");
            }
        }
    }

    //reads a double from the console
    //loops until user provides a valid number
    private double readDouble(String prompt) {
        while(true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch(NumberFormatException e) {
                System.out.println("Please enter a valid number. ");
            }
        }
    }

    //reads a comma separated list of skill Ids from the console
    //Example input : "1,3,5"
    //Returns: [1,3,5]
    //loops until at least one valid ID is provided.
    private List<Integer> readSkillIds(){
        while(true){
            System.out.print("Enter skill Ids (comma separated, e.g. 1,2,3): ");
            String input = scanner.nextLine().trim();
            try {
                List<Integer> ids = new ArrayList<>();
                for(String part : input.split(",")) {
                    ids.add(Integer.parseInt(part.trim()));
                }
                if(!ids.isEmpty()) return ids;
            } catch (NumberFormatException e){
                System.out.println("Invalid input. Enter numbers separated by commas. ");
            }
        }
    }

    //converts a list of Skill objects to a readable comma separated string.
    //Example: [Skill(1,"Java"), Skill(2,"SQL")] -> "Java, SQL"
    //Used throughout the display methods for clean output.
    private String skillNames(List<Skill> skills){
        if(skills.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< skills.size(); i++){
            sb.append(skills.get(i).getName());
            if(i < skills.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
