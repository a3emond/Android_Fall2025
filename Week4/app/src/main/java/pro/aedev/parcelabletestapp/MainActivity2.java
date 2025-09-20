package pro.aedev.parcelabletestapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get testObject from MainActivity via parcelable

        // 1- test if intent is not null
        if (getIntent() != null) {
            // 2- get Parcelable object from intent
            TestObject testObject = getIntent().getParcelableExtra("testObjectKey");
            // 3- test if testObject is not null
            if (testObject != null) {
                // 4- get height and width from testObject
                int height = testObject.getHeight();
                int width = testObject.getWidth();
                // 5- print height and width to logcat
                System.out.println("Height: " + height + ", Width: " + width);
            } else {
                System.out.println("testObject is null");
            }
        }
    }
}