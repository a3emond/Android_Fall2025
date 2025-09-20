package pro.aedev.intentproject.views;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import pro.aedev.intentproject.R;
import pro.aedev.intentproject.models.GameState;
import pro.aedev.intentproject.models.Player;
import pro.aedev.intentproject.models.Statistics;
import pro.aedev.intentproject.utils.IntentHelper;

public class MainActivity extends AppCompatActivity {

    private EditText editName;
    private Button btnPlay, btnStats, btnNewPlayer, btnContinue;
    private ValueAnimator colorPulse;
    private Switch modeSwitch;

    private Statistics statistics;
    private String restoredMode;
    private String restoredPlayerName;
    private GameState restoredGameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadIntentData();
        restoreUiState();
        setupEventHandlers();
    }

    private void initViews() {
        editName = findViewById(R.id.editName);
        btnPlay = findViewById(R.id.btnPlay);
        btnStats = findViewById(R.id.btnStats);
        btnNewPlayer = findViewById(R.id.btnNewPlayer);
        btnContinue = findViewById(R.id.btnContinue);
        modeSwitch = findViewById(R.id.modeSwitch);

        LinearLayout rulesContainer = findViewById(R.id.rulesContainer);
        String[] rules = getResources().getStringArray(R.array.rules_array);
        for (String rule : rules) {
            TextView tv = new TextView(this);
            tv.setText("• " + rule);
            tv.setTextSize(16);
            rulesContainer.addView(tv);
        }

        // Setup color animation
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
    }

    private void loadIntentData() {
        IntentHelper.GameData data = IntentHelper.extract(getIntent());

        statistics = data.statistics;
        restoredMode = data.mode;
        restoredPlayerName = data.playerName;
        restoredGameState = data.gameState;
    }

    private void restoreUiState() {
        modeSwitch.setChecked("numbers".equalsIgnoreCase(restoredMode));
        editName.setText(restoredPlayerName);

        boolean canContinue = restoredGameState != null &&
                !restoredGameState.isGameOver() &&
                restoredGameState.getGuessCount() != 0;
        btnContinue.setVisibility(canContinue ? View.VISIBLE : View.GONE);

        boolean hasName = !editName.getText().toString().trim().isEmpty();
        btnPlay.setEnabled(hasName);
    }

    private void setupEventHandlers() {
        editName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = !s.toString().trim().isEmpty();
                btnPlay.setEnabled(hasText);
                if (hasText) {
                    if (!colorPulse.isRunning()) colorPulse.start();
                } else {
                    if (colorPulse.isRunning()) colorPulse.cancel();
                    btnPlay.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.btn_disabled)));
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnPlay.setOnClickListener(v -> handlePlay());
        btnContinue.setOnClickListener(v -> handleContinue());
        btnNewPlayer.setOnClickListener(v -> handleNewPlayer());
        btnStats.setOnClickListener(v -> handleStats());
    }

    private void handlePlay() {
        if (colorPulse.isRunning()) colorPulse.cancel();
        btnPlay.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.btn_normal)));

        String name = editName.getText().toString().trim();
        Player player = statistics.addPlayer(name);
        String selectedMode = modeSwitch.isChecked() ? "numbers" : "letters";

        IntentHelper.GameData data = new IntentHelper.GameData();
        data.player = player;
        data.statistics = statistics;
        data.mode = selectedMode;
        data.playerName = name;

        // force reset if mode changed from previous unfinished game
        if (restoredGameState != null && !restoredGameState.isGameOver()) {
            if (!restoredMode.equalsIgnoreCase(selectedMode)) {
                restoredGameState = null; // discard old game
            }
        }

        startActivity(IntentHelper.createGameIntent(this, data));
    }


    private void handleContinue() {
        if (restoredGameState == null) return;

        String name = editName.getText().toString().trim();
        Player player = statistics.addPlayer(name);
        String selectedMode = restoredMode; // continue uses restored mode by default

        boolean modeMismatch =
                (modeSwitch.isChecked() && !"numbers".equalsIgnoreCase(restoredMode)) ||
                        (!modeSwitch.isChecked() && !"letters".equalsIgnoreCase(restoredMode));

        if (modeMismatch) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Mode Mismatch")
                    .setMessage("You switched modes. Continuing will reset the saved game and start fresh in the new mode. Do you want to proceed?")
                    .setPositiveButton("Yes, reset", (dialog, which) -> {
                        // reset game in the *current* mode selected by the switch
                        String newMode = modeSwitch.isChecked() ? "numbers" : "letters";
                        IntentHelper.GameData data = new IntentHelper.GameData();
                        data.player = player;
                        data.statistics = statistics;
                        data.mode = newMode;
                        data.playerName = name;
                        data.gameState = null; // force fresh game

                        startActivity(IntentHelper.createGameIntent(this, data));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return;
        }

        // Normal continue
        IntentHelper.GameData data = new IntentHelper.GameData();
        data.player = player;
        data.statistics = statistics;
        data.mode = selectedMode;
        data.playerName = name;
        data.gameState = restoredGameState;

        startActivity(IntentHelper.createGameIntent(this, data));
    }



    private void handleNewPlayer() {
        if (restoredGameState != null && !restoredGameState.isGameOver()) {
            statistics.recordLoss(restoredPlayerName, restoredGameState.getGuessCount());
        }
        restoredGameState = null;
        editName.setText("");
        modeSwitch.setChecked(false);
        btnContinue.setVisibility(View.GONE);
    }

    private void handleStats() {
        String name = editName.getText().toString().trim();

        IntentHelper.GameData data = new IntentHelper.GameData();
        data.statistics = statistics;
        data.playerName = name;
        data.mode = modeSwitch.isChecked() ? "numbers" : "letters";
        data.gameState = restoredGameState;

        startActivity(IntentHelper.createStatisticsIntent(this, data));
    }
}
