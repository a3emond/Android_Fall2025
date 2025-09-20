package pro.aedev.intentproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Statistics implements Parcelable {

    private final List<Player> players;

    public Statistics() {
        players = new ArrayList<>();
    }

    protected Statistics(Parcel in) {
        players = new ArrayList<>();
        in.readTypedList(players, Player.CREATOR);
    }

    public static final Creator<Statistics> CREATOR = new Creator<Statistics>() {
        @Override
        public Statistics createFromParcel(Parcel in) {
            return new Statistics(in);
        }

        @Override
        public Statistics[] newArray(int size) {
            return new Statistics[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(players);
    }

    // Core logic
    public Player addPlayer(String name) {
        Player existing = getPlayerByName(name);
        if (existing != null) return existing;
        Player p = new Player(name);
        players.add(p);
        return p;
    }

    public Player getPlayerByName(String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public void recordWin(String name, int guesses) {
        addPlayer(name).recordWin(guesses);
    }

    public void recordLoss(String name, int guesses) {
        addPlayer(name).recordLoss(guesses);
    }

    public List<Player> getAllPlayers() {
        return players;
    }

    public String getStatisticsSummary() {
        if (players.isEmpty()) return "No players yet.";
        StringBuilder sb = new StringBuilder("Session Statistics:\n\n");
        for (Player p : players) {
            sb.append(p.toString()).append("\n");
        }
        return sb.toString();
    }
}
