package com.agency.model;

import com.agency.model.enums.JobStatus;

public class Job {
    private int id;
    private String title;
    private String description;
    private double salary;
    private String location;
    private JobStatus status;

    /**
     * First constructor with id = 0 and jobstatus set to open is a creation constructor
     * Second constructor with all six fields is the reconstitution constructor for values we already have
     * @param title - job title
     * @param description - job description
     * @param salary - job salary
     * @param location - job location - City wise
     */

    //Creation
    public Job(String title, String description, double salary, String location) {
        this.id = 0;
        this.status = JobStatus.OPEN;
        this.title = title;
        this.description = description;
        this.salary = salary;
        this.location = location;
    }

    //Reconstitution
    public Job(int id, String title, String description, double salary, String location, JobStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.salary = salary;
        this.location = location;
        this.status = status;
    }

    /**
     * Getters for all the six fields and setters for all but id
     */
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getSalary() {
        return salary;
    }

    public String getLocation() {
        return location;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    /**
     * equals and hashCode on id only
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Job)) return false;
        Job job= (Job) o;
        return this.id == job.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.id);
    }

    public String toFileString(){
        return id + "|" + title + "|" + description + "|"
                + salary + "|" + location + "|" + status.name();
    }

    public static Job fromFileString(String line){
        String[] parts = line.split("\\|");
        return new Job(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                Double.parseDouble(parts[3]),
                parts[4],
                JobStatus.valueOf(parts[5])
        );
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", salary=" + salary +
                ", location='" + location + '\'' +
                ", status=" + status +
                '}';
    }
}
