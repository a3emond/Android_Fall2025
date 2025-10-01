package com.example.studentmanagementapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studentmanagementapp.models.Student;
import com.example.studentmanagementapp.models.ProgramData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner studentSpinner, coursesSpinner;
    private Button showStudentButton, showCourseButton;
    private TextView programText;
    private List<Student> studentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBoilerplateCode();

        // Initialize and parse XML once
        XmlDataParser.init(this);

        studentSpinner = findViewById(R.id.spinner_student);
        coursesSpinner = findViewById(R.id.spinner_courses);
        showStudentButton = findViewById(R.id.button_show_student);
        showCourseButton = findViewById(R.id.button_show_course);
        programText = findViewById(R.id.text_program);

        // Load students list
        studentsList = XmlDataParser.getStudents(this);

        // Populate student spinner
        ArrayAdapter<Student> studentAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(studentsList)
        );
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentSpinner.setAdapter(studentAdapter);

        // Update program + courses whenever student changes
        studentSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                updateProgramAndCourses(studentsList.get(position));
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                programText.setText("Program: N/A");
                coursesSpinner.setAdapter(null);
            }
        });

        // Show student info
        showStudentButton.setOnClickListener(v -> {
            int pos = studentSpinner.getSelectedItemPosition();
            if (pos >= 0) {
                Student selected = studentsList.get(pos);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Student Information")
                        .setMessage(selected.getDisplayDetails())
                        .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();
            }
        });

        // Show course info (name + description + student score)
        showCourseButton.setOnClickListener(v -> showCourseInfo());
    }

    private void updateProgramAndCourses(Student student) {
        programText.setText("Program: " + student.getProgramRefName());

        ProgramData program = XmlDataParser.getProgram(student.getProgramRefName());
        List<String> courseNames = new ArrayList<>();
        if (program != null) {
            courseNames.addAll(program.getCourses().keySet());
        }

        ArrayAdapter<String> coursesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                courseNames
        );
        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesSpinner.setAdapter(coursesAdapter);
    }

    private void showCourseInfo() {
        int studentPos = studentSpinner.getSelectedItemPosition();
        int coursePos = coursesSpinner.getSelectedItemPosition();

        if (studentPos >= 0 && coursePos >= 0) {
            Student student = studentsList.get(studentPos);
            String courseName = (String) coursesSpinner.getSelectedItem();

            // Lookup program + description
            ProgramData program = XmlDataParser.getProgram(student.getProgramRefName());
            String description = "";
            if (program != null && program.getCourses().containsKey(courseName)) {
                description = program.getCourses().get(courseName);
            }

            // Lookup student score
            int score = student.getResults().stream()
                    .filter(r -> r.getCourseName().equals(courseName))
                    .map(Student.CourseResult::getScore)
                    .findFirst()
                    .orElse(-1);

            // Build message
            StringBuilder msg = new StringBuilder();
            msg.append("Course: ").append(courseName).append("\n");
            msg.append("Description: ").append(description).append("\n\n");
            msg.append("Student Score: ").append(score >= 0 ? score : "No score recorded.");

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Course Information")
                    .setMessage(msg.toString())
                    .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
        }
    }

    private void addBoilerplateCode() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
