package pro.aedev.intentproject.viewcontrollers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pro.aedev.intentproject.R;
import pro.aedev.intentproject.models.GameState;
import pro.aedev.intentproject.models.Statistics;
import pro.aedev.intentproject.utils.IntentHelper;

public class StatisticsActivity extends AppCompatActivity {

    private Statistics statistics;
    private GameState gameState;
    private String playerName;
    private String mode;

    private TextView tvStats;
    private Button btnBackMenu, btnBackGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initViews();
        loadIntentData();
        displayStatistics();
        setupEventHandlers();
    }

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

    private void displayStatistics() {
        tvStats.setText(statistics.getStatisticsSummary());
        btnBackGame.setVisibility(gameState != null ? View.VISIBLE : View.GONE);
    }

    private void setupEventHandlers() {
        btnBackMenu.setOnClickListener(v -> handleBackToMenu());

        if (gameState != null) {
            btnBackGame.setOnClickListener(v -> handleBackToGame());
        }
    }

    private void handleBackToMenu() {
        IntentHelper.GameData data = new IntentHelper.GameData();
        data.statistics = statistics;
        data.playerName = playerName;
        data.mode = mode;
        data.gameState = gameState;

        startActivity(IntentHelper.createMainIntent(this, data));
        finish();
    }

    private void handleBackToGame() {
        IntentHelper.GameData data = new IntentHelper.GameData();
        data.statistics = statistics;
        data.playerName = playerName;
        data.mode = mode;
        data.gameState = gameState;

        startActivity(IntentHelper.createGameIntent(this, data));
        finish();
    }
}
