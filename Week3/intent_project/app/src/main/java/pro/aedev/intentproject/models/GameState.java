package pro.aedev.intentproject.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GameState implements Parcelable {

    private String mode;
    private char targetLetter;
    private int targetNumber;
    private int guessCount;
    private boolean gameOver;

    public GameState(String mode, char targetLetter, int targetNumber, int guessCount, boolean gameOver) {
        this.mode = mode;
        this.targetLetter = targetLetter;
        this.targetNumber = targetNumber;
        this.guessCount = guessCount;
        this.gameOver = gameOver;
    }

    protected GameState(Parcel in) {
        mode = in.readString();
        targetLetter = (char) in.readInt();
        targetNumber = in.readInt();
        guessCount = in.readInt();
        gameOver = in.readByte() != 0;
    }

    public static final Creator<GameState> CREATOR = new Creator<GameState>() {
        @Override
        public GameState createFromParcel(Parcel in) {
            return new GameState(in);
        }

        @Override
        public GameState[] newArray(int size) {
            return new GameState[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mode);
        dest.writeInt((int) targetLetter);
        dest.writeInt(targetNumber);
        dest.writeInt(guessCount);
        dest.writeByte((byte) (gameOver ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getMode() { return mode; }
    public char getTargetLetter() { return targetLetter; }
    public int getTargetNumber() { return targetNumber; }
    public int getGuessCount() { return guessCount; }
    public boolean isGameOver() { return gameOver; }
}
