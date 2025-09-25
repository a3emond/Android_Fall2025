package pro.aedev.intentproject.services;

import android.util.Log;
import java.util.Random;

import pro.aedev.intentproject.models.GameState;
import pro.aedev.intentproject.models.Player;
import pro.aedev.intentproject.models.Statistics;

public class GameService {

    private static final String TAG = "GameService";
    public static final int MAX_GUESSES = 6;
    private static final Random RNG = new Random();

    // --- Core Data ---
    private final Player player;
    private final Statistics statistics;
    private final String mode; // "letters" or "numbers"

    private char targetLetter;
    private int targetNumber;
    private int guessCount;
    private boolean gameOver;

    // ============================================================
    // Constructor
    // ============================================================

    public GameService(Player player, Statistics statistics, String mode) {
        this.player = (player != null) ? player : new Player("Guest");
        this.statistics = (statistics != null) ? statistics : new Statistics();
        this.mode = ("numbers".equalsIgnoreCase(mode) || "letters".equalsIgnoreCase(mode))
                ? mode.toLowerCase()
                : "letters"; // default fallback
        startNewGame();
    }

    // ============================================================
    // Game Lifecycle
    // ============================================================

    public void startNewGame() {
        guessCount = 0;
        gameOver = false;

        if ("numbers".equalsIgnoreCase(mode)) {
            targetNumber = RNG.nextInt(26) + 1; // 1–26
            Log.d(TAG, "New target number chosen.");
        } else {
            targetLetter = (char) ('a' + RNG.nextInt(26));
            Log.d(TAG, "New target letter chosen.");
        }
    }

    public void forceLoss() {
        safeRecordGameResult(false, guessCount);
        gameOver = true;
    }

    // ============================================================
    // Guess Handling
    // ============================================================

    public String processGuess(String input) {
        if (gameOver) {
            return "Game is already over. Please reset to start a new one.";
        }

        if (input == null || input.trim().isEmpty()) {
            return "Please enter a guess.";
        }

        if ("numbers".equalsIgnoreCase(mode)) {
            return processNumberGuess(input.trim());
        } else {
            return processLetterGuess(input.trim());
        }
    }

    private String processLetterGuess(String input) {
        if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            return "Please enter a single letter a–z.";
        }

        char guess = Character.toLowerCase(input.charAt(0));
        if (guess < 'a' || guess > 'z') {
            return "Please enter a valid letter between a and z.";
        }

        guessCount++;

        if (guess == targetLetter) {
            safeRecordGameResult(true, guessCount);
            gameOver = true;
            return "Correct! The letter was '" + targetLetter + "'.";
        }

        if (guessCount >= MAX_GUESSES) {
            safeRecordGameResult(false, guessCount);
            gameOver = true;
            return "Out of guesses! The letter was '" + targetLetter + "'.";
        }

        return (guess < targetLetter)
                ? "The target letter is ABOVE '" + guess + "'."
                : "The target letter is BELOW '" + guess + "'.";
    }

    private String processNumberGuess(String input) {
        int guess;
        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return "Please enter a valid number (1–26).";
        }

        if (guess < 1 || guess > 26) {
            return "Number must be between 1 and 26.";
        }

        guessCount++;

        if (guess == targetNumber) {
            safeRecordGameResult(true, guessCount);
            gameOver = true;
            return "Correct! The number was " + targetNumber + ".";
        }

        if (guessCount >= MAX_GUESSES) {
            safeRecordGameResult(false, guessCount);
            gameOver = true;
            return "Out of guesses! The number was " + targetNumber + ".";
        }

        return (guess < targetNumber)
                ? "The target number is higher than " + guess + "."
                : "The target number is lower than " + guess + ".";
    }

    // ============================================================
    // Statistics Recording
    // ============================================================

    private void safeRecordGameResult(boolean won, int guesses) {
        try {
            if (player != null && player.getName() != null) {
                if (won) {
                    statistics.recordWin(player.getName(), guesses);
                } else {
                    statistics.recordLoss(player.getName(), guesses);
                }
            } else {
                Log.w(TAG, "Skipping stats recording: player or name is null.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to record game result", e);
        }
    }

    // ============================================================
    // State Export/Import
    // ============================================================

    public GameState toGameState() {
        return new GameState(mode, targetLetter, targetNumber, guessCount, gameOver);
    }

    public void restoreFromState(GameState state) {
        if (state == null) {
            Log.w(TAG, "restoreFromState called with null");
            return;
        }
        this.guessCount = Math.max(0, state.getGuessCount());
        this.gameOver = state.isGameOver();
        if ("numbers".equalsIgnoreCase(state.getMode())) {
            this.targetNumber = state.getTargetNumber();
        } else {
            this.targetLetter = state.getTargetLetter();
        }
    }

    // ============================================================
    // Accessors
    // ============================================================

    public Player getPlayer() {
        return player;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public String getMode() {
        return mode;
    }

    public int getRemainingGuesses() {
        return Math.max(0, MAX_GUESSES - guessCount);
    }

    public int getGuessCount() {
        return guessCount;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
