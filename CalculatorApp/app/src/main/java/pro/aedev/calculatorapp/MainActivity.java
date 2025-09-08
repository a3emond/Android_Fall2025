package pro.aedev.calculatorapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Paint;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private String currentInput = "";
    private double firstOperand = 0;
    private String operator = "";
    private boolean resetOnNextInput = false; // simple flag to reset input after operation

    private static final float MAX_TEXT_SIZE_SP = 72f; // sp = scale-independent pixels
    private static final float MIN_TEXT_SIZE_SP = 20f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check device type (phone vs tablet)
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (!isTablet) {
            // Lock to portrait on phones just for simplicity (don't want to mess with frontend)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDisplay = findViewById(R.id.tvDisplay);

        // Numbers 0–9
        int[] numberButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : numberButtons) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> appendNumber(btn.getText().toString()));
            makeTextResponsive(btn, 0.4f); // text = 40% of button height
        }

        // Decimal point
        Button btnDot = findViewById(R.id.btnDot);
        btnDot.setOnClickListener(v -> appendDot());
        makeTextResponsive(btnDot, 0.4f);

        // Operators
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> setOperator("+"));
        makeTextResponsive(btnAdd, 0.4f);

        Button btnSub = findViewById(R.id.btnSub);
        btnSub.setOnClickListener(v -> setOperator("-"));
        makeTextResponsive(btnSub, 0.4f);

        Button btnMul = findViewById(R.id.btnMul);
        btnMul.setOnClickListener(v -> setOperator("*"));
        makeTextResponsive(btnMul, 0.4f);

        Button btnDiv = findViewById(R.id.btnDiv);
        btnDiv.setOnClickListener(v -> setOperator("/"));
        makeTextResponsive(btnDiv, 0.4f);

        // Equals
        Button btnEq = findViewById(R.id.btnEq);
        btnEq.setOnClickListener(v -> calculate());
        makeTextResponsive(btnEq, 0.4f);

        // Clear
        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> clear());
        makeTextResponsive(btnClear, 0.4f);

        // Initialize display
        setDisplayText("0");
    }

    // --- Display helpers ---
    private static final int MAX_DIGITS = 20; // max digits to display

    private void setDisplayText(String text) {
        if (text.length() > MAX_DIGITS) {
            // Cut it or show error
            text = text.substring(0, MAX_DIGITS);
        }
        tvDisplay.setText(text);
        tvDisplay.post(this::resizeTextToFit); // ensure it runs after layout pass (post point to UI thread queue)
    }

    private void resizeTextToFit() {
        int availableWidth = tvDisplay.getWidth() - tvDisplay.getPaddingLeft() - tvDisplay.getPaddingRight();
        if (availableWidth <= 0) return;

        // Start from current size instead of always max
        float currentSize = tvDisplay.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
        float trySize = Math.min(currentSize, MAX_TEXT_SIZE_SP);

        Paint paint = new Paint();
        paint.set(tvDisplay.getPaint());

        String text = tvDisplay.getText().toString();

        // shrink until it fits or until min size
        while (trySize > MIN_TEXT_SIZE_SP &&
                paint.measureText(text) > availableWidth) {
            trySize -= 2f;
            paint.setTextSize(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, trySize, getResources().getDisplayMetrics()));
        }

        tvDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, trySize);
    }


    // --- Calculator logic ---
    private void appendNumber(String number) {
        if (resetOnNextInput) {
            currentInput = "";
            resetOnNextInput = false;
        }
        currentInput += number;
        setDisplayText(currentInput);
    }

    private void appendDot() {
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) {
                currentInput = "0.";
            } else {
                currentInput += ".";
            }
            setDisplayText(currentInput);
        }
    }

    private void setOperator(String op) {
        if (!currentInput.isEmpty()) {
            firstOperand = Double.parseDouble(currentInput);
            operator = op;
            resetOnNextInput = true;
        }
    }

    private void calculate() {
        if (!currentInput.isEmpty() && !operator.isEmpty()) {
            double secondOperand = Double.parseDouble(currentInput);
            double result = 0;

            switch (operator) {
                case "+": result = firstOperand + secondOperand; break;
                case "-": result = firstOperand - secondOperand; break;
                case "*": result = firstOperand * secondOperand; break;
                case "/":
                    if (secondOperand != 0) result = firstOperand / secondOperand;
                    else {
                        setDisplayText(getString(R.string.error));
                        clear();
                        return;
                    }
                    break;
            }

            currentInput = String.valueOf(result);
            operator = "";
            resetOnNextInput = true;
            setDisplayText(currentInput);
        }
    }

    private void clear() {
        currentInput = "";
        firstOperand = 0;
        operator = "";
        resetOnNextInput = false;
        tvDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, MAX_TEXT_SIZE_SP); // reset to default size
        tvDisplay.setText("0");
    }

    // utility method to make button text size responsive
    private void makeTextResponsive(@NonNull Button btn, float scaleFactor) {
        // Scale factor = fraction of button height for text eg: 0.4 = 40%
        btn.post(() -> {
            int h = btn.getHeight();
            if (h > 0) {
                float newSizePx = h * scaleFactor;
                btn.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, newSizePx);
            }
        });
    }
}
