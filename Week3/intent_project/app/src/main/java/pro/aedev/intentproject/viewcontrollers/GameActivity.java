package pro.aedev.intentproject.viewcontrollers;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pro.aedev.intentproject.R;
import pro.aedev.intentproject.models.GameState;
import pro.aedev.intentproject.models.Player;
import pro.aedev.intentproject.models.Statistics;
import pro.aedev.intentproject.services.GameService;
import pro.aedev.intentproject.utils.IntentHelper;

public class GameActivity extends AppCompatActivity {

    // --- UI Elements ---
    private GridLayout gridChoices;
    private Button btnReset, btnStats;
    private TextView tvFeedback, tvInstruction, txtHello;

    // --- Game Logic ---
    private GameService gameController;

    // --- Animations ---
    private ValueAnimator highlightPulse;

    // ============================================================
    // Lifecycle
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();

        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState);
        } else {
            initGameFromIntent();
        }

        setupPulseAnimator();
        setupEventHandlers();
        resetUI();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (gameController != null) {
            outState.putParcelable("gameState", gameController.toGameState());
            outState.putParcelable("statistics", gameController.getStatistics());
            outState.putString("mode", gameController.getMode());
            outState.putString("playerName", gameController.getPlayer().getName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (highlightPulse != null && highlightPulse.isRunning()) {
            highlightPulse.cancel();
        }
        highlightPulse = null;
    }

    // ============================================================
    // Initialization
    // ============================================================

    private void initViews() {
        txtHello = findViewById(R.id.txtHello);
        gridChoices = findViewById(R.id.gridChoices);
        btnReset = findViewById(R.id.btnReset);
        btnStats = findViewById(R.id.btnStats);
        tvFeedback = findViewById(R.id.tvFeedback);
        tvInstruction = findViewById(R.id.tvInstruction);
    }

    private void initGameFromIntent() {
        IntentHelper.GameData data = IntentHelper.extract(getIntent());

        gameController = new GameService(data.player, data.statistics, data.mode);

        if (data.gameState != null &&
                data.mode != null &&
                data.mode.equalsIgnoreCase(data.gameState.getMode())) {
            gameController.restoreFromState(data.gameState);
        }

        txtHello.setText(getString(R.string.welcome_message, data.player.getName()));
    }

    private void restoreFromBundle(@NonNull Bundle savedInstanceState) {
        String mode = savedInstanceState.getString("mode", "letters");
        String playerName = savedInstanceState.getString("playerName", "Guest");
        Statistics stats = savedInstanceState.getParcelable("statistics");
        GameState state = savedInstanceState.getParcelable("gameState");

        gameController = new GameService(new Player(playerName), stats, mode);
        if (state != null && mode.equalsIgnoreCase(state.getMode())) {
            gameController.restoreFromState(state);
        }

        txtHello.setText(getString(R.string.welcome_message, playerName));
    }

    private void setupPulseAnimator() {
        highlightPulse = ValueAnimator.ofObject(
                new ArgbEvaluator(),
                getColor(R.color.btn_normal),
                getColor(R.color.btn_pressed)
        );
        highlightPulse.setDuration(800);
        highlightPulse.setRepeatMode(ValueAnimator.REVERSE);
        highlightPulse.setRepeatCount(ValueAnimator.INFINITE);
    }

    // ============================================================
    // Event Handlers
    // ============================================================

    private void setupEventHandlers() {
        btnReset.setOnClickListener(v -> handleReset());
        btnStats.setOnClickListener(v -> handleStats());
    }

    private void handleChoice(String guess, Button clickedButton) {
        if (gameController.isGameOver()) return;

        String result = gameController.processGuess(guess);
        tvFeedback.setText(result);
        clickedButton.setEnabled(false);

        if (gameController.isGameOver()) {
            disableAllChoices();
            updateInstruction(); // force final update
            highlightCorrectAnswer();
        } else {
            updateInstruction();
            applyRangeHints(guess, result);
        }
    }

    private void handleReset() {
        if (!gameController.isGameOver()) {
            gameController.forceLoss();
        }
        gameController.startNewGame();
        resetUI();
    }

    private void handleStats() {
        IntentHelper.GameData data = new IntentHelper.GameData();
        data.statistics = gameController.getStatistics();
        data.playerName = gameController.getPlayer().getName();
        data.mode = gameController.getMode();
        data.gameState = gameController.toGameState();

        startActivity(IntentHelper.createStatisticsIntent(this, data));
    }

    // ============================================================
    // UI Updates
    // ============================================================

    private void resetUI() {
        tvFeedback.setText("");
        updateInstruction();
        setupChoiceGrid();

        if (gameController.isGameOver()) {
            disableAllChoices();
            highlightCorrectAnswer();

            String result = (gameController.getMode().equals("numbers"))
                    ? "Game over! The number was " + gameController.toGameState().getTargetNumber()
                    : "Game over! The letter was '" + gameController.toGameState().getTargetLetter() + "'";
            tvFeedback.setText(result);
        }
    }

    private void updateInstruction() {
        if (gameController.isGameOver()) {
            tvInstruction.setText(R.string.game_over_message);
            return;
        }

        String mode = gameController.getMode();
        int remaining = gameController.getRemainingGuesses();

        if ("numbers".equalsIgnoreCase(mode)) {
            tvInstruction.setText(
                    getString(R.string.guess_a_number_1_26_attempts_left, remaining)
            );
        } else {
            tvInstruction.setText(
                    getString(R.string.guess_a_letter_a_z_attempts_left, String.valueOf(remaining))
            );
        }
    }

    private void setupChoiceGrid() {
        gridChoices.removeAllViews();

        if ("letters".equalsIgnoreCase(gameController.getMode())) {
            for (char c = 'a'; c <= 'z'; c++) {
                addChoiceButton(String.valueOf(c));
            }
        } else {
            for (int i = 1; i <= 26; i++) {
                addChoiceButton(String.valueOf(i));
            }
        }
    }

    private void addChoiceButton(String label) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setBackgroundResource(R.drawable.button_3state);
        btn.setTextColor(getResources().getColor(android.R.color.white));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        btn.setLayoutParams(params);

        btn.setOnClickListener(v -> handleChoice(label, btn));
        gridChoices.addView(btn);
    }

    // ============================================================
    // Helpers
    // ============================================================

    private void disableAllChoices() {
        for (int i = 0; i < gridChoices.getChildCount(); i++) {
            gridChoices.getChildAt(i).setEnabled(false);
        }
    }

    private void applyRangeHints(String guess, String result) {
        if ("letters".equalsIgnoreCase(gameController.getMode())) {
            char g = guess.charAt(0);
            if (result.contains("ABOVE")) {
                disableLetterRange('a', g);
            } else if (result.contains("BELOW")) {
                disableLetterRange(g, 'z');
            }
        } else {
            try {
                int g = Integer.parseInt(guess);
                if (result.contains("higher")) {
                    disableNumberRange(1, g);
                } else if (result.contains("lower")) {
                    disableNumberRange(g, 26);
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private void disableLetterRange(char start, char end) {
        for (int i = 0; i < gridChoices.getChildCount(); i++) {
            Button btn = (Button) gridChoices.getChildAt(i);
            String text = btn.getText().toString();
            if (text.length() == 1) {
                char c = text.charAt(0);
                if (c >= start && c <= end) {
                    btn.setEnabled(false);
                }
            }
        }
    }

    private void disableNumberRange(int start, int end) {
        for (int i = 0; i < gridChoices.getChildCount(); i++) {
            Button btn = (Button) gridChoices.getChildAt(i);
            try {
                int value = Integer.parseInt(btn.getText().toString());
                if (value >= start && value <= end) {
                    btn.setEnabled(false);
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private void highlightCorrectAnswer() {
        String correct = gameController.getMode().equals("numbers")
                ? String.valueOf(gameController.toGameState().getTargetNumber())
                : String.valueOf(gameController.toGameState().getTargetLetter());

        for (int i = 0; i < gridChoices.getChildCount(); i++) {
            Button btn = (Button) gridChoices.getChildAt(i);
            if (btn.getText().toString().equalsIgnoreCase(correct)) {
                if (highlightPulse != null) {
                    highlightPulse.addUpdateListener(animator -> {
                        int color = (int) animator.getAnimatedValue();
                        btn.setBackgroundTintList(ColorStateList.valueOf(color));
                    });
                    highlightPulse.start();
                }
                break;
            }
        }
    }
}
