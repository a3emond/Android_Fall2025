package pro.aedev.intentproject.utils;

import android.content.Context;
import android.content.Intent;

import pro.aedev.intentproject.models.GameState;
import pro.aedev.intentproject.models.Player;
import pro.aedev.intentproject.models.Statistics;
import pro.aedev.intentproject.viewcontrollers.GameActivity;
import pro.aedev.intentproject.viewcontrollers.MainActivity;
import pro.aedev.intentproject.viewcontrollers.StatisticsActivity;

public class IntentHelper {

    // Data carrier between activities (inner class for convenience)
    public static class GameData {
        public Statistics statistics;
        public String playerName;
        public String mode;
        public GameState gameState;
        public Player player;
    }

    // --- Extract from incoming Intent ---
    public static GameData extract(Intent intent) {
        GameData data = new GameData();

        if (intent == null) {
            // Provide safe defaults if intent is null
            data.statistics = new Statistics();
            data.playerName = "Guest";
            data.mode = "letters";
            data.player = new Player("Guest");
            return data;
        }

        // Extract extras
        data.statistics = intent.getParcelableExtra("statistics");
        data.playerName = intent.getStringExtra("playerName");
        data.mode = intent.getStringExtra("mode");
        data.gameState = intent.getParcelableExtra("gameState");
        data.player = intent.getParcelableExtra("player");

        // Fallbacks
        if (data.statistics == null) data.statistics = new Statistics();
        if (data.mode == null ||
                (!data.mode.equalsIgnoreCase("letters") && !data.mode.equalsIgnoreCase("numbers"))) {
            data.mode = "letters";
        }
        if (data.playerName == null) data.playerName = "Guest";
        if (data.player == null) data.player = new Player(data.playerName);

        return data;
    }

    // --- Store into outgoing Intent ---
    public static void fillIntent(Intent intent, GameData data) {
        if (intent == null || data == null) return;

        if (data.statistics != null) intent.putExtra("statistics", data.statistics);
        if (data.playerName != null) intent.putExtra("playerName", data.playerName);
        if (data.mode != null) intent.putExtra("mode", data.mode);
        if (data.gameState != null) intent.putExtra("gameState", data.gameState);
        if (data.player != null) intent.putExtra("player", data.player);
    }

    // --- Convenience factories ---

    public static Intent createMainIntent(Context ctx, GameData data) {
        Intent intent = new Intent(ctx, MainActivity.class);
        fillIntent(intent, data);
        return intent;
    }

    public static Intent createGameIntent(Context ctx, GameData data) {
        Intent intent = new Intent(ctx, GameActivity.class);
        fillIntent(intent, data);
        return intent;
    }

    public static Intent createStatisticsIntent(Context ctx, GameData data) {
        Intent intent = new Intent(ctx, StatisticsActivity.class);
        fillIntent(intent, data);
        return intent;
    }
}
