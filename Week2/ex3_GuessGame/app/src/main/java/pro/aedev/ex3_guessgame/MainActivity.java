package pro.aedev.ex3_guessgame;


import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText etGuess;
    private Button btnGuess, btnReset;
    private TextView tvFeedback, tvInstruction;

    private char targetLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etGuess = findViewById(R.id.etGuess);
        btnGuess = findViewById(R.id.btnGuess);
        btnReset = findViewById(R.id.btnReset);
        tvFeedback = findViewById(R.id.tvFeedback);
        tvInstruction = findViewById(R.id.tvInstruction);

        startNewGame();

        btnGuess.setOnClickListener(v -> handleGuess());
        btnReset.setOnClickListener(v -> startNewGame());
    }


    // Dismiss keyboard when touching outside EditText for better UX
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    hideKeyboard(v);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void startNewGame() {
        targetLetter = (char) ('a' + new Random().nextInt(26));
        tvFeedback.setText("");
        tvInstruction.setText("Guess a letter (a–z)");
        etGuess.setText("");
    }

    private void handleGuess() {
        String input = etGuess.getText().toString().trim().toLowerCase();
        if (input.isEmpty() || input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            tvFeedback.setText("Please enter a single letter a–z.");
            return;
        }

        char guess = input.charAt(0);

        if (guess == targetLetter) {
            tvFeedback.setText("Correct! The letter was '" + targetLetter + "'.");
        } else if (guess < targetLetter) {
            tvFeedback.setText("The target letter is ABOVE '" + guess + "'.");
        } else {
            tvFeedback.setText("The target letter is BELOW '" + guess + "'.");
        }

        etGuess.setText("");
    }
}