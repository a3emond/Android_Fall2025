package pro.aedev.intentproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Player implements Parcelable {

    private String name;
    private int wins;
    private int losses;
    private int totalGames;
    private int totalGuesses;

    public Player(String name) {
        this.name = name;
        this.wins = 0;
        this.losses = 0;
        this.totalGames = 0;
        this.totalGuesses = 0;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public int getTotalGuesses() {
        return totalGuesses;
    }

    public double getAverageGuessesPerGame() {
        if (totalGames == 0) return 0.0;
        return (double) totalGuesses / totalGames;
    }

    // Update stats
    public void recordWin(int guesses) {
        wins++;
        totalGames++;
        totalGuesses += guesses;
    }

    public void recordLoss(int guesses) {
        losses++;
        totalGames++;
        totalGuesses += guesses;
    }

    // For display in statistics screen
    @NonNull
    @Override
    public String toString() {
        return "Player: " + name +
                " | Wins: " + wins +
                " | Losses: " + losses +
                " | Games: " + totalGames +
                " | Guesses: " + totalGuesses +
                " | Avg: " + String.format("%.2f", getAverageGuessesPerGame());
    }

    // Parcelable implementation
    protected Player(Parcel in) {
        name = in.readString();
        wins = in.readInt();
        losses = in.readInt();
        totalGames = in.readInt();
        totalGuesses = in.readInt();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(wins);
        dest.writeInt(losses);
        dest.writeInt(totalGames);
        dest.writeInt(totalGuesses);
    }
}
