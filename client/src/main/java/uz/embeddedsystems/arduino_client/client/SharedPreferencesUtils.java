package uz.embeddedsystems.arduino_client.client;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class SharedPreferencesUtils {
    private static final String IP = "ip";
    private static final String PORT = "port";
    private static final String EMPTY_STRING_VALUE = "";
    private static final int MAX_VALUE_OF_SAVED_PAIRS = 5;

    private SharedPreferencesUtils() {
    }

    public static Pair<Set, Set> getSavedPair(final Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);

        final Set<String> ipSet = sharedPref.getStringSet(IP, new HashSet<String>());
        final Set<String> portSet = sharedPref.getStringSet(PORT, new HashSet<String>());

        return new Pair<Set, Set>(ipSet, portSet);
    }

    public static void savePair(final Activity activity, final Pair<Set<String>, Set<String>> pair){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(IP, pair.first);
        editor.putStringSet(PORT, pair.second);
        editor.commit();
    }

}
