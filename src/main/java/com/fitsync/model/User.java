package com.fitsync.model;

public class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private int age;
    private String gender;
    private double heightCm;
    private double weightKg;
    private String createdAt;

    public User(String name, String email, String password,
                int age, String gender, double heightCm, double weightKg, String createdAt){
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender =gender;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.createdAt = createdAt;
    }

    public User(int id, String name, String email, String password,
                int age, String gender, double heightCm, double weightKg,
                String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.createdAt = createdAt;
    }

    //Getters
    public int getId() { return id;}
    public String getName() { return name; }
    public String getEmail() {return email; }
    public String getPassword() {return password; }
    public int getAge() {return age; }
    public String getGender() {return gender; }
    public double getHeightCm() {return heightCm; }
    public double getWeightKg() {return weightKg; }
    public String getCreatedAt() {return createdAt; }

    //Setters
    public void setId(int id) {this.id = id; }
    public void setName(String name) {this.name = name; }
    public void setEmail(String email) {this.email = email; }
    public void setPassword(String password) {this.password = password; }
    public void setAge(int age) {this.age = age; }
    public void setGender(String gender) {this.gender = gender; }
    public void setHeightCm(double h) {this.heightCm = h; }
    public void setWeightKg(double w) {this.weightKg = w; }
    public void setCreatedAt(String c) {this.createdAt = c; }

    @Override
    public String toString(){
        return "User{id=" + id + ",name=" + name + ",email=" + email + "}";
    }
}

