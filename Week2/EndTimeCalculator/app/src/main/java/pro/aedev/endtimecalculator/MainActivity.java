package pro.aedev.endtimecalculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etCourseTitle;
    private TimePicker timePicker;
    private Spinner spinnerDuration;
    private Button btnSubmit;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCourseTitle = findViewById(R.id.etCourseTitle);
        timePicker = findViewById(R.id.timePicker);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvResult = findViewById(R.id.tvResult);

        setupDurationSpinner();

        // Enable submit only if course title is not empty
        etCourseTitle.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSubmit.setEnabled(!etCourseTitle.getText().toString().trim().isEmpty());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void setupDurationSpinner() {
        List<String> durations = new ArrayList<>();
        for (int i = 1; i <= 8; i++) { // 1 to 8 hours
            durations.add(i + " hour(s)");
        }
        spinnerDuration.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, durations));
    }

    private void handleSubmit() {
        String course = etCourseTitle.getText().toString().trim();
        int startHour = timePicker.getHour();
        int startMinute = timePicker.getMinute();
        int blocks = spinnerDuration.getSelectedItemPosition() + 1;

        int baseMinutes = blocks * 60;
        int totalBreak = blocks * 10;

        // Teaching time = base - all breaks
        int teachingTime = baseMinutes - totalBreak;

        // End time = base - half of the breaks
        int durationWithBreakRule = baseMinutes - (totalBreak / 2);

        // Compute end time
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, startHour);
        endTime.set(Calendar.MINUTE, startMinute);
        endTime.add(Calendar.MINUTE, durationWithBreakRule);

        int endHour = endTime.get(Calendar.HOUR_OF_DAY);
        int endMinute = endTime.get(Calendar.MINUTE);

        String endStr = String.format("%02d:%02d", endHour, endMinute);

        tvResult.setText(
                "The course \"" + course + "\" will end at " + endStr +
                        " (teaching time: " + (teachingTime / 60) + "h" +
                        String.format("%02d", teachingTime % 60) + ")"
        );
    }



}
