package pro.aedev.intentproject.viewcontrollers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pro.aedev.intentproject.R;
import pro.aedev.intentproject.models.GameState;
import pro.aedev.intentproject.models.Statistics;
import pro.aedev.intentproject.utils.IntentHelper;

public class StatisticsActivity extends AppCompatActivity {

    // --- Data ---
    private Statistics statistics;
    private GameState gameState;
    private String playerName;
    private String mode;

    // --- UI ---
    private TextView tvStats;
    private Button btnBackMenu, btnBackGame;

    // ============================================================
    // Lifecycle
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initViews();

        if (savedInstanceState != null) {
            restoreFromBundle(savedInstanceState);
        } else {
            loadIntentData();
        }

        displayStatistics();
        setupEventHandlers();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (statistics != null) outState.putParcelable("statistics", statistics);
        if (gameState != null) outState.putParcelable("gameState", gameState);
        if (playerName != null) outState.putString("playerName", playerName);
        if (mode != null) outState.putString("mode", mode);
    }

    // ============================================================
    // Initialization
    // ============================================================

    private void initViews() {
        tvStats = findViewById(R.id.tvStats);
        btnBackMenu = findViewById(R.id.btnBackMenu);
        btnBackGame = findViewById(R.id.btnBackGame);
    }

    private void loadIntentData() {
        IntentHelper.GameData data = IntentHelper.extract(getIntent());
        statistics = data.statistics;
        playerName = data.playerName;
        mode = data.mode;
        gameState = data.gameState;
    }

    private void restoreFromBundle(@NonNull Bundle savedInstanceState) {
        statistics = savedInstanceState.getParcelable("statistics");
        gameState = savedInstanceState.getParcelable("gameState");
        playerName = savedInstanceState.getString("playerName", "Guest");
        mode = savedInstanceState.getString("mode", "letters");
    }

    // ============================================================
    // UI Updates
    // ============================================================

    private void displayStatistics() {
        if (statistics != null) {
            tvStats.setText(statistics.getStatisticsSummary());
        } else {
            tvStats.setText(R.string.no_stats_available);
        }

        // Only allow going back to game if a game state exists and isn’t empty
        boolean canReturnToGame = gameState != null && !gameState.isGameOver();
        btnBackGame.setVisibility(canReturnToGame ? View.VISIBLE : View.GONE);
    }

    // ============================================================
    // Event Handlers
    // ============================================================

    private void setupEventHandlers() {
        btnBackMenu.setOnClickListener(v -> handleBackToMenu());

        if (btnBackGame.getVisibility() == View.VISIBLE) {
            btnBackGame.setOnClickListener(v -> handleBackToGame());
        }
    }

    private void handleBackToMenu() {
        startActivity(IntentHelper.createMainIntent(this, buildGameData()));
        finish();
    }

    private void handleBackToGame() {
        startActivity(IntentHelper.createGameIntent(this, buildGameData()));
        finish();
    }

    // ============================================================
    // Helpers
    // ============================================================

    private IntentHelper.GameData buildGameData() {
        IntentHelper.GameData data = new IntentHelper.GameData();
        data.statistics = statistics;
        data.playerName = playerName;
        data.mode = mode;
        data.gameState = gameState;
        return data;
    }
}
