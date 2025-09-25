package pro.aedev.intentproject.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pro.aedev.intentproject.models.GameState;
import pro.aedev.intentproject.models.Player;
import pro.aedev.intentproject.models.Statistics;
import pro.aedev.intentproject.viewcontrollers.GameActivity;
import pro.aedev.intentproject.viewcontrollers.MainActivity;
import pro.aedev.intentproject.viewcontrollers.StatisticsActivity;

public class IntentHelper {

    private static final String TAG = "IntentHelper";

    // ============================================================
    // Data Carrier
    // ============================================================

    public static class GameData {
        public Statistics statistics;
        public String playerName;
        public String mode;
        public GameState gameState;
        public Player player;
    }

    // ============================================================
    // Extract From Intent
    // ============================================================

    public static GameData extract(Intent intent) {
        GameData data = new GameData();

        if (intent == null) {
            Log.w(TAG, "extract() called with null Intent. Using defaults.");
            return createDefaultData();
        }

        try {
            data.statistics = intent.getParcelableExtra("statistics");
        } catch (ClassCastException e) {
            Log.e(TAG, "Invalid type for statistics extra", e);
            data.statistics = null;
        }

        try {
            data.gameState = intent.getParcelableExtra("gameState");
        } catch (ClassCastException e) {
            Log.e(TAG, "Invalid type for gameState extra", e);
            data.gameState = null;
        }

        try {
            data.player = intent.getParcelableExtra("player");
        } catch (ClassCastException e) {
            Log.e(TAG, "Invalid type for player extra", e);
            data.player = null;
        }

        // Strings are safe
        data.playerName = intent.getStringExtra("playerName");
        data.mode = intent.getStringExtra("mode");

        // --- Fallbacks ---
        if (data.statistics == null) {
            data.statistics = new Statistics();
        }

        if (data.mode == null ||
                (!"letters".equalsIgnoreCase(data.mode) && !"numbers".equalsIgnoreCase(data.mode))) {
            data.mode = "letters";
        }

        if (data.playerName == null || data.playerName.trim().isEmpty()) {
            data.playerName = "Guest";
        }

        if (data.player == null) {
            data.player = new Player(data.playerName);
        }

        return data;
    }

    private static GameData createDefaultData() {
        GameData d = new GameData();
        d.statistics = new Statistics();
        d.playerName = "Guest";
        d.mode = "letters";
        d.player = new Player("Guest");
        return d;
    }

    // ============================================================
    // Store Into Intent
    // ============================================================

    public static void fillIntent(Intent intent, GameData data) {
        if (intent == null) {
            Log.e(TAG, "fillIntent() called with null intent. Ignoring.");
            return;
        }
        if (data == null) {
            Log.e(TAG, "fillIntent() called with null data. Ignoring.");
            return;
        }

        try {
            if (data.statistics != null) intent.putExtra("statistics", data.statistics);
            if (data.playerName != null) intent.putExtra("playerName", data.playerName);
            if (data.mode != null) intent.putExtra("mode", data.mode);
            if (data.gameState != null) intent.putExtra("gameState", data.gameState);
            if (data.player != null) intent.putExtra("player", data.player);
        } catch (Exception e) {
            Log.e(TAG, "Failed to put extras into Intent", e);
        }
    }

    // ============================================================
    // Convenience Factories
    // ============================================================

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
