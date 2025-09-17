package pro.aedev.intentpractice.models;

import android.content.Context;
import java.io.InputStream;
import java.util.*;

import org.json.JSONArray;

public class WordProvider {
    public static List<String> loadBundledWords(Context ctx) {
        try {
            InputStream is = ctx.getAssets().open("words.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONArray arr = new JSONArray(json);
            List<String> words = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                words.add(arr.getString(i));
            }
            return words;
        } catch (Exception e) {
            return Arrays.asList("swift", "hangman", "android", "java", "variable", "function", "developer");
        }
    }

    public static String randomWord(Difficulty diff, List<String> words) {
        Random rnd = new Random();
        List<String> filtered = new ArrayList<>();
        for (String w : words) {
            if (w.length() >= diff.getMin() && w.length() <= diff.getMax()) {
                filtered.add(w);
            }
        }
        if (filtered.isEmpty()) filtered = words;
        return filtered.get(rnd.nextInt(filtered.size())).toUpperCase();
    }
}
