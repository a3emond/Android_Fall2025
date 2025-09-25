package com.example.studentmanagementapp;

import java.util.Date;

public class Student {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private int educationalLevel;

    // Constructor
    public Student(String firstName, String lastName, Date dateOfBirth, int educationalLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.educationalLevel = educationalLevel;
    }

    // Utility method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // utility method to get age
    public int getAge() {
        Date now = new Date();
        int age = now.getYear() - dateOfBirth.getYear();
        if (now.getMonth() < dateOfBirth.getMonth() ||
            (now.getMonth() == dateOfBirth.getMonth() && now.getDate() < dateOfBirth.getDate())) {
            age--;
        }
        return age;
    }

    // Override toString for easy display
    @Override
    public String toString() {
        return "Student{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", educationalLevel=" + educationalLevel +
                '}';
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth.after(new Date())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }
        // date have to be in a reasonable range (calculated from current date)
        if (dateOfBirth.before(new Date(new Date().getTime() - new Date(100, 0, 0).getTime()))) {
            throw new IllegalArgumentException("Date of birth is too far in the past.");
        }
        this.dateOfBirth = dateOfBirth;
    }
    public int getEducationalLevel() {
        return educationalLevel;
    }
    public void setEducationalLevel(int educationalLevel) {
        if (educationalLevel < 1 || educationalLevel > 12) {
            throw new IllegalArgumentException("Educational level must be between 1 and 12.");
        }
        this.educationalLevel = educationalLevel;
    }
}