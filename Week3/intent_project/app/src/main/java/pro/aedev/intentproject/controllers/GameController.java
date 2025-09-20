package pro.aedev.intentproject.controllers;

import java.util.Random;

import pro.aedev.intentproject.models.GameState;
import pro.aedev.intentproject.models.Player;
import pro.aedev.intentproject.models.Statistics;

public class GameController {

    private final Player player;
    private final Statistics statistics;

    private final String mode; // "letters" or "numbers"
    private char targetLetter;
    private int targetNumber;
    private int guessCount;
    private boolean gameOver;

    public static final int MAX_GUESSES = 6;

    public GameController(Player player, Statistics statistics, String mode) {
        this.player = player;
        this.statistics = statistics != null ? statistics : new Statistics();
        this.mode = mode != null ? mode : "letters";
        startNewGame();
    }

    // --- Game lifecycle ---
    public void startNewGame() {
        guessCount = 0;
        gameOver = false;
        if ("numbers".equalsIgnoreCase(mode)) {
            targetNumber = new Random().nextInt(26) + 1; // 1–26
        } else {
            targetLetter = (char) ('a' + new Random().nextInt(26));
        }

    }

    // --- Process a guess ---
    public String processGuess(String input) {
        if (gameOver) {
            return "Game is already over. Please reset to start a new one.";
        }

        if ("numbers".equalsIgnoreCase(mode)) {
            return processNumberGuess(input);
        } else {
            return processLetterGuess(input);
        }
    }

    private String processLetterGuess(String input) {
        if (input == null || input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            return "Please enter a single letter a–z.";
        }

        char guess = Character.toLowerCase(input.charAt(0));
        guessCount++;

        if (guess == targetLetter) {
            recordGameResult(true, guessCount);
            gameOver = true;
            return "Correct! The letter was '" + targetLetter + "'.";
        }

        if (guessCount >= MAX_GUESSES) {
            recordGameResult(false, guessCount);
            gameOver = true;
            return "Out of guesses! The letter was '" + targetLetter + "'.";
        }

        if (guess < targetLetter) {
            return "The target letter is ABOVE '" + guess + "'.";
        } else {
            return "The target letter is BELOW '" + guess + "'.";
        }
    }

    private String processNumberGuess(String input) {
        int guess;
        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return "Please enter a valid number (1–26).";
        }

        guessCount++;

        if (guess == targetNumber) {
            recordGameResult(true, guessCount);
            gameOver = true;
            return "Correct! The number was " + targetNumber + ".";
        }

        if (guessCount >= MAX_GUESSES) {
            recordGameResult(false, guessCount);
            gameOver = true;
            return "Out of guesses! The number was " + targetNumber + ".";
        }

        if (guess < targetNumber) {
            return "The target number is higher than " + guess + ".";
        } else {
            return "The target number is lower than " + guess + ".";
        }
    }


    // --- Record result into statistics ---
    public void recordGameResult(boolean won, int guesses) {
        if (won) {
            statistics.recordWin(player.getName(), guesses);
        } else {
            statistics.recordLoss(player.getName(), guesses);
        }
    }

    // --- Game state export/import ---
    public GameState toGameState() {
        return new GameState(mode, targetLetter, targetNumber, guessCount, gameOver);
    }

    public void restoreFromState(GameState state) {
        if (state == null) return;
        this.guessCount = state.getGuessCount();
        this.gameOver = state.isGameOver();
        if ("numbers".equalsIgnoreCase(state.getMode())) {
            this.targetNumber = state.getTargetNumber();
        } else {
            this.targetLetter = state.getTargetLetter();
        }
    }

    // --- Accessors ---
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
        return MAX_GUESSES - guessCount;
    }

    public int getGuessCount() {
        return guessCount;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
