package pro.aedev.spinnerslearning;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> firstAdapter, lastAdapter;
    Spinner firstNameSpinner, lastNameSpinner;
    Button showNameButton;
    Switch sourceSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // References
        firstNameSpinner = findViewById(R.id.firstNameSpinner);
        lastNameSpinner = findViewById(R.id.lastNameSpinner);
        showNameButton = findViewById(R.id.showNameButton);
        sourceSwitch = findViewById(R.id.sourceSwitch);

        // Initial population with XML arrays
        loadFromXml();

        // Switch listener
        sourceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadFromList();
            } else {
                loadFromXml();
            }
        });

        // Button action
        showNameButton.setOnClickListener(v -> {
            String first = (String) firstNameSpinner.getSelectedItem();
            String last = (String) lastNameSpinner.getSelectedItem();
            String fullName = first + " " + last;

            Toast.makeText(MainActivity.this, "Nom complet : " + fullName, Toast.LENGTH_LONG).show();
        });
    }

    //---------------------------------------------------------------------------
    // Helper methods pour load les data dans les Spinners from XML or Java lists
    //---------------------------------------------------------------------------

    private void loadFromXml() {
        // First names
        firstAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.first_names)
        );
        firstAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstNameSpinner.setAdapter(firstAdapter);

        // Last names
        lastAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.last_names)
        );
        lastAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lastNameSpinner.setAdapter(lastAdapter);
    }

    private void loadFromList() {
        // hardcoded Java lists
        List<String> firstNames = Arrays.asList("Luc", "Amélie", "Mathieu", "Chantal", "François");
        List<String> lastNames = Arrays.asList("Pelletier", "Roy", "Girard", "Desjardins", "Bélanger");

        // First names
        firstAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                firstNames
        );
        firstAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstNameSpinner.setAdapter(firstAdapter);

        // Last names
        lastAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                lastNames
        );
        lastAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lastNameSpinner.setAdapter(lastAdapter);
    }
}
