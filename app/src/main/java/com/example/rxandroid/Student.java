package com.example.rxandroid;

public class Student {
    private String name;
    private String email;
    private String registrationDate;
    private int age;

    public Student(String name, String email, int age, String registrationDate) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.registrationDate = registrationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
