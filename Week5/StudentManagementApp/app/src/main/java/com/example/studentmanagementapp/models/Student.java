package com.example.studentmanagementapp.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Student {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private EducationLevel educationLevel;

    // Instead of enum Program, we store a reference (string)
    private String programRefName;

    // Results per course
    private List<CourseResult> results = new ArrayList<>();

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd MMM yyyy");

    // Constructor
    public Student(String firstName, String lastName, Date dateOfBirth,
                   EducationLevel educationLevel, String programRefName) {
        this.firstName = firstName;
        this.lastName = lastName;
        setDateOfBirth(dateOfBirth);
        setEducationLevel(educationLevel);
        setProgramRefName(programRefName);
    }

    // ========================
    // Utility methods
    // ========================
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        Date now = new Date();
        int age = now.getYear() - dateOfBirth.getYear();
        if (now.getMonth() < dateOfBirth.getMonth() ||
                (now.getMonth() == dateOfBirth.getMonth() && now.getDate() < dateOfBirth.getDate())) {
            age--;
        }
        return age;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    public String getDisplayDetails() {
        return "Full Name: " + getFullName() +
                "\nAge: " + getAge() +
                "\nDate of Birth: " + DATE_FORMAT.format(dateOfBirth) +
                "\nEducation Level: " + educationLevel +
                "\nProgram: " + programRefName;
    }

    // ========================
    // Getters and Setters
    // ========================
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth.after(new Date())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public EducationLevel getEducationLevel() { return educationLevel; }
    public void setEducationLevel(EducationLevel educationLevel) {
        if (educationLevel == null) {
            throw new IllegalArgumentException("Education level cannot be null.");
        }
        this.educationLevel = educationLevel;
    }

    public String getProgramRefName() { return programRefName; }
    public void setProgramRefName(String programRefName) {
        if (programRefName == null || programRefName.trim().isEmpty()) {
            throw new IllegalArgumentException("Program reference cannot be empty.");
        }
        this.programRefName = programRefName;
    }

    public List<CourseResult> getResults() { return results; }
    public void addResult(String courseName, int score) {
        this.results.add(new CourseResult(courseName, score));
    }

    // ========================
    // Inner class: CourseResult
    // ========================
    public static class CourseResult {
        private String courseName;
        private int score;

        public CourseResult(String courseName, int score) {
            this.courseName = courseName;
            this.score = score;
        }

        public String getCourseName() { return courseName; }
        public int getScore() { return score; }

        @Override
        public String toString() {
            return courseName + ": " + score;
        }
    }
}
