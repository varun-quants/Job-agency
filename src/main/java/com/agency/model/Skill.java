package com.agency.model;

public class Skill {

    private int id;
    private String name;

    public Skill(String name) {
        this.id = 0; //0 means not yet persisted
        this.name = name;
    }

    public Skill(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Skill)) return false;
        Skill skill= (Skill) o;
        return this.id == skill.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.id);
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public String toFileString(){
        return id + "|" + name;
    }

    public static Skill fromFileString(String line){
        String[] parts = line.split("\\|");
        return new Skill(Integer.parseInt(parts[0]), parts[1]);
    }
}
