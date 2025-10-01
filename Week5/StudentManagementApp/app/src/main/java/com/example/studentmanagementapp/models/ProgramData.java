package com.example.studentmanagementapp.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProgramData {
    private String name;
    private String description;

    // courseName -> description
    private Map<String, String> courses = new LinkedHashMap<>();

    public ProgramData(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, String> getCourses() { return courses; }
    public void addCourse(String name, String desc) {
        courses.put(name, desc);
    }
}
