package pro.aedev.ex1_simpeauth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spinnerDay, spinnerMonth, spinnerYear;
    private Button btnSubmit;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvResult = findViewById(R.id.tvResult);

        setupSpinners();

        // Enable button only if name is not empty
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFormValid();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void setupSpinners() {
        // Days 1–31
        List<String> days = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            days.add(String.valueOf(i));
        }
        spinnerDay.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days));

        // Months 1–12
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.valueOf(i));
        }
        spinnerMonth.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months));

        // Years (current year down to current year - 100)
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 100; i--) {
            years.add(String.valueOf(i));
        }
        spinnerYear.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years));
    }

    private void checkFormValid() {
        String name = etName.getText().toString().trim();
        btnSubmit.setEnabled(!name.isEmpty());
    }

    @SuppressLint("SetTextI18n")
    private void handleSubmit() {
        String name = etName.getText().toString().trim();
        int day = Integer.parseInt(spinnerDay.getSelectedItem().toString());
        int month = Integer.parseInt(spinnerMonth.getSelectedItem().toString()) - 1; // Calendar months are 0-based
        int year = Integer.parseInt(spinnerYear.getSelectedItem().toString());

        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);

        // Validation
        if (dob.after(today)) {
            tvResult.setText(R.string.birth_date_cannot_be_in_the_future);
            return;
        }

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age < 0) {
            tvResult.setText(R.string.invalid_birth_date);
            return;
        }

        if (age > 100) {
            tvResult.setText(R.string.age_cannot_be_over_100);
            return;
        }

        // Message
        String status = (age >= 18) ? getString(R.string.an_adult) : getString(R.string.a_teenager);
        tvResult.setText(getString(R.string.welcome) + " " + name + "! " +
                getString(R.string.you_are) + " " + age + " " + getString(R.string.years_old) + ", " + status + ".");

        // Reset form
        etName.setText("");
        spinnerDay.setSelection(0);
        spinnerMonth.setSelection(0);
        spinnerYear.setSelection(0);
        btnSubmit.setEnabled(false);
    }
}
