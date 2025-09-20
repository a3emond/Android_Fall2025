package pro.aedev.intentproject.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pro.aedev.intentproject.R;
import pro.aedev.intentproject.controllers.GameController;
import pro.aedev.intentproject.utils.IntentHelper;

public class GameActivity extends AppCompatActivity {

    private GridLayout gridChoices;
    private Button btnReset, btnStats;
    private TextView tvFeedback, tvInstruction, txtHello;

    private GameController gameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        initGameFromIntent();
        setupEventHandlers();
        resetUI();
    }

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

        // Always create controller with the requested mode
        gameController = new GameController(data.player, data.statistics, data.mode);

        // Only restore if the saved state matches the requested mode
        if (data.gameState != null &&
                data.mode != null &&
                data.mode.equalsIgnoreCase(data.gameState.getMode())) {
            gameController.restoreFromState(data.gameState);
        }

        txtHello.setText(getString(R.string.welcome_message, data.player.getName()));
    }


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
        } else {
            updateInstruction();

            if ("letters".equalsIgnoreCase(gameController.getMode())) {
                char g = guess.charAt(0);
                if (result.contains("ABOVE")) {
                    disableLetterRange('a', g);
                } else if (result.contains("BELOW")) {
                    disableLetterRange(g, 'z');
                }
            } else {
                int g = Integer.parseInt(guess);
                if (result.contains("higher")) {
                    disableNumberRange(1, g);
                } else if (result.contains("lower")) {
                    disableNumberRange(g, 26);
                }
            }
        }
    }

    private void handleReset() {
        if (!gameController.isGameOver()) {
            gameController.recordGameResult(false, gameController.getGuessCount());
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

    private void resetUI() {
        tvFeedback.setText("");
        updateInstruction();
        setupChoiceGrid();
    }

    private void updateInstruction() {
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

    private void disableAllChoices() {
        for (int i = 0; i < gridChoices.getChildCount(); i++) {
            gridChoices.getChildAt(i).setEnabled(false);
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
}
