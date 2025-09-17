package pro.aedev.intentproject;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText editName;
    private Button btnPlay;
    private ValueAnimator colorPulse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        btnPlay = findViewById(R.id.btnPlay);
        LinearLayout rulesContainer = findViewById(R.id.rulesContainer);

        // Load rules
        String[] rules = getResources().getStringArray(R.array.rules_array);
        for (String rule : rules) {
            TextView tv = new TextView(this);
            tv.setText("• " + rule);
            tv.setTextSize(16);
            rulesContainer.addView(tv);
        }

        // Prepare color pulse animator (blue <-> purple)
        colorPulse = ValueAnimator.ofObject(
                new ArgbEvaluator(),
                getColor(R.color.btn_normal),
                getColor(R.color.btn_pressed)
        );
        colorPulse.setDuration(800);
        colorPulse.setRepeatMode(ValueAnimator.REVERSE);
        colorPulse.setRepeatCount(ValueAnimator.INFINITE);
        colorPulse.addUpdateListener(animator -> {
            int color = (int) animator.getAnimatedValue();
            btnPlay.setBackgroundTintList(ColorStateList.valueOf(color));
        });

        // Enable/disable button + pulse
        editName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // TODO: onTextChanged has a flaw: if the user hit the back button it does not trigger the animation back...
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = !s.toString().trim().isEmpty();
                btnPlay.setEnabled(hasText);

                if (hasText) {
                    if (!colorPulse.isRunning()) colorPulse.start();
                } else {
                    if (colorPulse.isRunning()) colorPulse.cancel();
                    // reset to disabled color
                    btnPlay.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.btn_disabled)));
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Handle click
        btnPlay.setOnClickListener(v -> {
            if (colorPulse.isRunning()) colorPulse.cancel();
            btnPlay.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.btn_normal)));

            // no need to check for null or empty, button is disabled in that case
            String name = editName.getText().toString().trim();
            // new intent to start GameActivity
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("playerName", name);
            startActivity(intent);
        });
    }
}
