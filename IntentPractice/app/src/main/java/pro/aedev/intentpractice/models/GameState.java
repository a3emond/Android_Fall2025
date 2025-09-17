package pro.aedev.intentpractice.models;

import java.util.*;

public class GameState {
    private static final int MAX_MISTAKES = 6;

    private String targetWord;
    private boolean[] revealed;
    private Set<Character> guessed = new HashSet<>();
    private int mistakes = 0;

    private int currentPlayerIndex = 0;
    private boolean isGameOver = false;
    private boolean isWin = false;

    private List<String> players = new ArrayList<>();

    public void startNewGame(String word, List<String> players) {
        this.players = new ArrayList<>(players);
        targetWord = word.toUpperCase();
        revealed = new boolean[targetWord.length()];
        for (int i = 0; i < targetWord.length(); i++) {
            char ch = targetWord.charAt(i);
            revealed[i] = (ch == '-' || ch == ' ');
        }
        guessed.clear();
        mistakes = 0;
        currentPlayerIndex = 0;
        isGameOver = false;
        isWin = false;
    }

    public String getCurrentPlayerName() {
        if (currentPlayerIndex < players.size()) return players.get(currentPlayerIndex);
        return "Player";
    }

    public String getDisplayWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < targetWord.length(); i++) {
            char ch = targetWord.charAt(i);
            if (revealed[i]) sb.append(ch);
            else if (ch == '-' || ch == ' ') sb.append(ch);
            else sb.append("_");
        }
        return sb.toString();
    }

    public boolean canGuess(char letter) {
        return !isGameOver && Character.isLetter(letter) && !guessed.contains(letter);
    }

    public void guess(char raw) {
        char letter = Character.toUpperCase(raw);
        if (!canGuess(letter)) return;

        guessed.add(letter);
        int hits = revealLetter(letter);

        if (hits > 0) {
            if (allRevealed()) {
                isWin = true;
                isGameOver = true;
            }
        } else {
            mistakes++;
            if (mistakes >= MAX_MISTAKES) {
                isGameOver = true;
                isWin = false;
            } else {
                currentPlayerIndex = (currentPlayerIndex + 1) % Math.max(1, players.size());
            }
        }
    }

    private int revealLetter(char letter) {
        int count = 0;
        for (int i = 0; i < targetWord.length(); i++) {
            if (targetWord.charAt(i) == letter && !revealed[i]) {
                revealed[i] = true;
                count++;
            }
        }
        return count;
    }

    private boolean allRevealed() {
        for (boolean b : revealed) {
            if (!b) return false;
        }
        return true;
    }

    // Getters
    public int getMistakes() { return mistakes; }
    public boolean isGameOver() { return isGameOver; }
    public boolean isWin() { return isWin; }
    public Set<Character> getGuessed() { return guessed; }
}
