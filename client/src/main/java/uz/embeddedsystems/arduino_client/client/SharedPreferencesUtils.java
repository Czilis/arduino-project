package uz.embeddedsystems.arduino_client.client;

import android.content.SharedPreferences;
import android.util.Pair;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesUtils {
    private static final String IP = "ip";
    private static final String PORT = "port";

    private SharedPreferencesUtils() {
    }

    public static Pair<Set, Set> getSavedPair(final SharedPreferences sharedPref) {
        final Set<String> ipSet = sharedPref.getStringSet(IP, new HashSet<String>());
        final Set<String> portSet = sharedPref.getStringSet(PORT, new HashSet<String>());

        return new Pair<Set, Set>(ipSet, portSet);
    }

    public static void savePair(final SharedPreferences sharedPref, final Pair<Set<String>, Set<String>> pair){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(IP, pair.first);
        editor.putStringSet(PORT, pair.second);
        editor.commit();
    }

}
