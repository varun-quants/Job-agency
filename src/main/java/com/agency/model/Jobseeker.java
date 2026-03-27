package com.agency.model;

public class Jobseeker {

    /**
     * Necessary fields for a jobseeker's data such as demographic of an individual/applicant
     */
    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String location;

    /** Creation Constructor - when entity does not yet exist in the system. */
    public Jobseeker(String fullName, String email, String phone, String location) {
        this.id = 0;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.location = location;
    }

    /** Reconstitution Constructor - to rebuild an entity from stored data. */
    public Jobseeker(int id, String fullName, String email, String phone, String location) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.location = location;
    }

    /** Getter for all the six fields*/
    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    /** Setter for all except Id - because we want it to stay consistent throughout the system. */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /** Equals and HashCode for id only. */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Jobseeker)) return false;
        Jobseeker jobSeeker= (Jobseeker) o;
        return this.id == jobSeeker.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.id);
    }

    /** Now implementing two important methods for formating to file and formating from file namely toFileString and fromFileString respectively. */
    public String toFileString(){
        return id + "|" + fullName + "|" + email + "|"
                + phone + "|" + location;
    }

    public static Jobseeker fromFileString(String line){
        String[] parts = line.split("\\|"); // line splits are necessary for pipe character indication
        return new Jobseeker(
                //all fields are strings except id so no typecasting
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                parts[3],
                parts[4]
        );
    }

    /** Finally a toString() method. */
    @Override
    public String toString() {
        return "Jobseeker{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
