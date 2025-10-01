package com.example.studentmanagementapp;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.example.studentmanagementapp.models.EducationLevel;
import com.example.studentmanagementapp.models.ProgramData;
import com.example.studentmanagementapp.models.Student;

import org.xmlpull.v1.XmlPullParser;

import java.text.SimpleDateFormat;
import java.util.*;

public class XmlDataParser {
    private static Map<String, ProgramData> programMap;
    private static List<Student> students;

    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    // =========================
    // Public API
    // =========================
    public static void init(Context ctx) {
        if (programMap == null) {
            initPrograms(ctx);
        }
        if (students == null) {
            initStudents(ctx);
        }
    }

    public static Map<String, ProgramData> getPrograms(Context ctx) {
        if (programMap == null) initPrograms(ctx);
        return programMap;
    }

    public static List<Student> getStudents(Context ctx) {
        if (students == null) initStudents(ctx);
        return students;
    }

    public static ProgramData getProgram(String name) {
        return programMap != null ? programMap.get(name) : null;
    }

    // =========================
    // Programs
    // =========================
    private static void initPrograms(Context ctx) {
        programMap = new LinkedHashMap<>();

        try {
            XmlResourceParser parser = ctx.getResources().getXml(R.xml.programs);

            ProgramData currentProgram = null;
            String currentCourseName = null;
            String currentTag = null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();
                        if ("program".equals(currentTag)) {
                            String name = parser.getAttributeValue(null, "name");
                            currentProgram = new ProgramData(name);
                        } else if ("course".equals(currentTag)) {
                            currentCourseName = parser.getAttributeValue(null, "name");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText().trim();
                        if (!text.isEmpty() && "description".equals(currentTag)) {
                            if (currentCourseName != null && currentProgram != null) {
                                currentProgram.addCourse(currentCourseName, text);
                            } else if (currentProgram != null) {
                                currentProgram.setDescription(text);
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("program".equals(parser.getName()) && currentProgram != null) {
                            programMap.put(currentProgram.getName(), currentProgram);
                            currentProgram = null;
                        } else if ("course".equals(parser.getName())) {
                            currentCourseName = null;
                        }
                        currentTag = null;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // Students
    // =========================
    private static void initStudents(Context ctx) {
        students = new ArrayList<>();

        try {
            XmlResourceParser parser = ctx.getResources().getXml(R.xml.students);

            Student currentStudent = null;
            String currentTag = null;
            String currentCourse = null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();

                        if ("student".equals(currentTag)) {
                            // Delay setting programRef until <programRef> tag
                            currentStudent = new Student(
                                    "", "", new Date(),
                                    EducationLevel.COLLEGE, "TEMP"
                            );
                        } else if ("programRef".equals(currentTag) && currentStudent != null) {
                            String programName = parser.getAttributeValue(null, "name");
                            currentStudent.setProgramRefName(programName);
                        } else if ("result".equals(currentTag)) {
                            currentCourse = parser.getAttributeValue(null, "course");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText().trim();
                        if (!text.isEmpty() && currentStudent != null) {
                            switch (currentTag) {
                                case "firstName":
                                    currentStudent.setFirstName(text);
                                    break;
                                case "lastName":
                                    currentStudent.setLastName(text);
                                    break;
                                case "dateOfBirth":
                                    currentStudent.setDateOfBirth(SDF.parse(text));
                                    break;
                                case "educationLevel":
                                    currentStudent.setEducationLevel(EducationLevel.valueOf(text));
                                    break;
                                case "result":
                                    int score = Integer.parseInt(text);
                                    currentStudent.addResult(currentCourse, score);
                                    break;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("student".equals(parser.getName()) && currentStudent != null) {
                            students.add(currentStudent);
                            currentStudent = null;
                        } else if ("result".equals(parser.getName())) {
                            currentCourse = null;
                        }
                        currentTag = null;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
