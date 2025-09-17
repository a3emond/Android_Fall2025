package pro.aedev.intentpractice.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import pro.aedev.intentpractice.R;

public class AlphabetGridView extends GridLayout {
    private Set<Character> tried = new HashSet<>();
    private OnLetterClickListener listener;
    private LetterEnabledChecker checker;

    public AlphabetGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setColumnCount(7);
        buildButtons(context);
    }

    private void buildButtons(Context ctx) {
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            Button btn = new Button(ctx);
            btn.setText(String.valueOf(ch));
            btn.setAllCaps(false);
            btn.setTextColor(Color.BLACK);
            btn.setBackgroundResource(android.R.drawable.btn_default);

            LayoutParams lp = new LayoutParams();
            lp.width = 0;  // expand equally
            lp.height = LayoutParams.WRAP_CONTENT;
            lp.columnSpec = GridLayout.spec(UNDEFINED, 1f);
            btn.setLayoutParams(lp);

            char finalCh = ch;
            btn.setOnClickListener(v -> {
                if (listener != null) listener.onLetterClick(finalCh);
            });

            addView(btn);
        }
    }

    // update tried letters + refresh UI
    public void setTried(Set<Character> triedLetters) {
        this.tried = new HashSet<>(triedLetters);
        refresh();
    }

    // callback for enabling logic
    public void setLetterEnabledChecker(LetterEnabledChecker checker) {
        this.checker = checker;
        refresh();
    }

    // set tap listener
    public void setOnLetterClickListener(OnLetterClickListener l) {
        this.listener = l;
    }

    private void refresh() {
        for (int i = 0; i < getChildCount(); i++) {
            Button btn = (Button) getChildAt(i);
            char ch = btn.getText().charAt(0);

            boolean enabled = (checker == null || checker.isEnabled(ch));
            btn.setEnabled(enabled);

            if (tried.contains(ch)) {
                btn.setAlpha(0.35f);
            } else {
                btn.setAlpha(1.0f);
            }
        }
    }

    // --- Interfaces for callbacks ---
    public interface OnLetterClickListener {
        void onLetterClick(char ch);
    }

    public interface LetterEnabledChecker {
        boolean isEnabled(char ch);
    }
}
