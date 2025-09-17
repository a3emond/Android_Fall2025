package pro.aedev.intentpractice.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import pro.aedev.intentpractice.R;

public class HangmanView extends FrameLayout {
    private FrameLayout ropeGroup;
    private ImageView head, headEyes, headEyesBlood1, headEyesBlood2, headEyesDead;
    private ImageView body, armLeft, armRight, legLeft, legRight;

    // Physics state
    private double swingAngle = 0;   // radians
    private double velocity = 0;     // angular velocity
    private final double damping = 0.995;
    private final double frameRate = 16; // ms (~60 fps)
    private long lastUpdate = System.currentTimeMillis();

    private boolean isGameOver = false;
    private int mistakes = 0;
    private int lastMistakes = 0;

    private final Handler handler = new Handler();

    public HangmanView(Context ctx, @Nullable AttributeSet attrs) {
        super(ctx, attrs);
        LayoutInflater.from(ctx).inflate(R.layout.view_hangman, this, true);

        ropeGroup = findViewById(R.id.ropeGroup);

        head = findViewById(R.id.head);
        headEyes = findViewById(R.id.headEyes);
        headEyesBlood1 = findViewById(R.id.headEyesBlood1);
        headEyesBlood2 = findViewById(R.id.headEyesBlood2);
        headEyesDead = findViewById(R.id.headEyesDead);

        body = findViewById(R.id.body);
        armLeft = findViewById(R.id.armLeft);
        armRight = findViewById(R.id.armRight);
        legLeft = findViewById(R.id.legLeft);
        legRight = findViewById(R.id.legRight);

        // set pivot (rope anchor in Swift: x=368/1024, y=185/1024)
        post(() -> {
            float px = getWidth() * (368f / 1024f);
            float py = getHeight() * (185f / 1024f);
            ropeGroup.setPivotX(px);
            ropeGroup.setPivotY(py);
        });

        startPhysicsLoop();
        startBloodBlink();


        //TODO: fix accessibility permission
        // drag gesture for manual swing
        setOnTouchListener((v, ev) -> {
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                float dx = ev.getX() - (getWidth() / 2f);
                velocity = dx * 0.002; // scale drag into velocity
            }
            return true;
        });
    }

    // Physics loop
    private void startPhysicsLoop() {
        lastUpdate = System.currentTimeMillis();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                double dt = (now - lastUpdate) / 1000.0;
                lastUpdate = now;

                double restoringForce = -swingAngle * 0.05;
                velocity += restoringForce * dt * 60.0;
                velocity *= Math.pow(damping, dt * 60.0);
                swingAngle += velocity * dt;

                // update rotation
                ropeGroup.setRotation((float) Math.toDegrees(swingAngle));

                // counter-rotations like SwiftUI
                if (mistakes >= 1) {
                    head.setRotation((float) (-Math.toDegrees(swingAngle) * 0.1));
                }
                if (mistakes >= 3) {
                    armLeft.setRotation((float) (-Math.toDegrees(swingAngle) * 2));
                }
                if (mistakes >= 4) {
                    armRight.setRotation((float) (-Math.toDegrees(swingAngle) * 2));
                }
                if (mistakes >= 5) {
                    legLeft.setRotation((float) (-Math.toDegrees(swingAngle) * 2));
                }
                if (mistakes >= 6) {
                    legRight.setRotation((float) (-Math.toDegrees(swingAngle) * 2));
                }

                handler.postDelayed(this, (long) frameRate);
            }
        });
    }

    // Blink blood
    private void startBloodBlink() {
        postDelayed(new Runnable() {
            boolean toggle = true;
            @Override
            public void run() {
                if (!isGameOver && mistakes >= 1) {
                    headEyesBlood1.setVisibility(toggle ? VISIBLE : GONE);
                    headEyesBlood2.setVisibility(toggle ? GONE : VISIBLE);
                    toggle = !toggle;
                    postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    // Update mistakes and state
    public void setMistakes(int m, boolean gameOver) {
        this.isGameOver = gameOver;

        // Apply impulse when a new part is added
        if (m > lastMistakes) {
            int diff = m;
            double impulse = impulseForPart(diff);
            double direction = (diff % 2 == 0) ? 1.0 : -1.0;
            applyImpulse(direction * impulse);
        }
        lastMistakes = m;
        mistakes = m;

        // Show/hide parts
        head.setVisibility(m >= 1 ? VISIBLE : GONE);
        headEyes.setVisibility((m >= 1 && !gameOver) ? VISIBLE : GONE);
        body.setVisibility(m >= 2 ? VISIBLE : GONE);
        armLeft.setVisibility(m >= 3 ? VISIBLE : GONE);
        armRight.setVisibility(m >= 4 ? VISIBLE : GONE);
        legLeft.setVisibility(m >= 5 ? VISIBLE : GONE);
        legRight.setVisibility(m >= 6 ? VISIBLE : GONE);

        if (gameOver && m >= 1) {
            headEyes.setVisibility(GONE);
            headEyesDead.setVisibility(VISIBLE);

            // collapse parts animation
            collapsePart(head);
            collapsePart(body);
            collapsePart(armLeft);
            collapsePart(armRight);
            collapsePart(legLeft);
            collapsePart(legRight);
        }
    }

    // Impulse strength per part (like Swift)
    private double impulseForPart(int count) {
        switch (count) {
            case 1: return 4.0;
            case 2: return 8.0;
            case 3:
            case 4: return 6.0;
            case 5:
            case 6: return 6.0;
            default: return 4.0;
        }
    }

    // Collapse part animation
    private void collapsePart(ImageView v) {
        if (v.getVisibility() != VISIBLE) return;
        ObjectAnimator fall = ObjectAnimator.ofFloat(v, "translationY", 0f, 200f);
        ObjectAnimator rot = ObjectAnimator.ofFloat(v, "rotation",
                v.getRotation(), v.getRotation() + (float) (Math.random() * 120 - 60));
        fall.setDuration(1000);
        rot.setDuration(1000);
        fall.start();
        rot.start();
    }

    // Add external impulse
    public void applyImpulse(double impulse) {
        velocity += impulse;
    }
}
