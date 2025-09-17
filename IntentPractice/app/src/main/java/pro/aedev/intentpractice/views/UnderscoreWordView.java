package pro.aedev.intentpractice.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import pro.aedev.intentpractice.R;

public class UnderscoreWordView extends LinearLayout {
    public UnderscoreWordView(Context context) {
        super(context);
        init();
    }

    public UnderscoreWordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    /**
     * Sets the word to display with underscores, dashes, letters, and spaces.
     */
    public void setDisplay(String display) {
        removeAllViews();

        for (char ch : display.toCharArray()) {
            TextView tv = new TextView(getContext());
            tv.setText(String.valueOf(ch));
            tv.setTextSize(20);
            tv.setTypeface(Typeface.MONOSPACE);
            tv.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));

            LayoutParams lp;
            if (ch == ' ') {
                // fixed width gap
                tv.setText(" ");
                lp = new LayoutParams(12, LayoutParams.WRAP_CONTENT);
            } else {
                lp = new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );
                lp.setMargins(8, 0, 8, 0);
            }

            addView(tv, lp);
        }
    }
}
