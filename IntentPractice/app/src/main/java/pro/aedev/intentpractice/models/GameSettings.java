package pro.aedev.intentpractice.models;

import java.util.ArrayList;
import java.util.List;

public class GameSettings {
    private int playerCount = 1;
    private Difficulty difficulty = Difficulty.EASY;
    private List<String> playerNames = new ArrayList<>();

    public GameSettings() {
        playerNames.add("Player 1");
    }

    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int count) {
        this.playerCount = count;
        normalizeNames();
    }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty d) { this.difficulty = d; }

    public List<String> getPlayerNames() { return playerNames; }

    public void normalizeNames() {
        while (playerNames.size() < playerCount) {
            playerNames.add("Player " + (playerNames.size() + 1));
        }
        if (playerNames.size() > playerCount) {
            playerNames = new ArrayList<>(playerNames.subList(0, playerCount));
        }
    }
}
