![image-20250925125910133](/Users/a3emond/Library/Application Support/typora-user-images/image-20250925125910133.png)![image-20250925125936258](/Users/a3emond/Library/Application Support/typora-user-images/image-20250925125936258.png)![image-20250925125958499](/Users/a3emond/Library/Application Support/typora-user-images/image-20250925125958499.png)

```java
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
```

``` java
package com.example.studentmanagementapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Spinner studentSpinner;
    private Button showButton;
    private ArrayList<Student> studentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBoilerplateCode();

        studentSpinner = findViewById(R.id.spinner);
        showButton = findViewById(R.id.button);

        // Create fake data
        studentsList = createFakeStudents();

        // Populate spinner with student names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                getStudentNames(studentsList)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentSpinner.setAdapter(adapter);

        // Button click shows selected student's info
        showButton.setOnClickListener(v -> {
            int pos = studentSpinner.getSelectedItemPosition();
            if (pos >= 0) {
                Student selected = studentsList.get(pos);

                SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy");
                String message = "Full Name: " + selected.getFullName() +
                        "\nAge: " + selected.getAge() +
                        "\nDate of Birth: " + fmt.format(selected.getDateOfBirth()) +
                        "\nEducational Level: " + selected.getEducationalLevel();

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Student Information")
                        .setMessage(message)
                        .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();
            }
        });
    }

    // Creates 15 fake students
    private ArrayList<Student> createFakeStudents() {
        ArrayList<Student> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            list.add(new Student("Alice", "Johnson", sdf.parse("2010-03-15"), 5));
            list.add(new Student("Bob", "Smith", sdf.parse("2009-07-21"), 6));
            list.add(new Student("Charlie", "Brown", sdf.parse("2011-01-10"), 4));
            list.add(new Student("Diana", "Miller", sdf.parse("2008-11-05"), 7));
            list.add(new Student("Ethan", "Williams", sdf.parse("2012-02-18"), 3));
            list.add(new Student("Fiona", "Davis", sdf.parse("2010-06-30"), 5));
            list.add(new Student("George", "Wilson", sdf.parse("2007-09-25"), 8));
            list.add(new Student("Hannah", "Moore", sdf.parse("2009-12-12"), 6));
            list.add(new Student("Ian", "Taylor", sdf.parse("2011-04-03"), 4));
            list.add(new Student("Julia", "Anderson", sdf.parse("2012-08-17"), 3));
            list.add(new Student("Kevin", "Thomas", sdf.parse("2008-05-14"), 7));
            list.add(new Student("Lily", "Jackson", sdf.parse("2009-10-28"), 6));
            list.add(new Student("Mark", "White", sdf.parse("2010-09-09"), 5));
            list.add(new Student("Nina", "Harris", sdf.parse("2011-12-22"), 4));
            list.add(new Student("Oscar", "Martin", sdf.parse("2007-02-02"), 8));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Extracts full names for display
    private ArrayList<String> getStudentNames(ArrayList<Student> list) {
        ArrayList<String> names = new ArrayList<>();
        for (Student s : list) {
            names.add(s.getFullName());
        }
        return names;
    }

    // Boilerplate wrapper
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
```

