package ca.sfu.cmpt276projectaluminium.model;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * An interface for reading and writing to the shared preference file
 * used the link below to help use shared preferences with material from the
 * Android Programming Big Nerd Ranch guide page: 533
 * https://stackoverflow.com/questions/7491287/android-how-to-use-sharedpreferences-in-non-ac
 */
public class QueryPreferences {

    public static int getStoredIntQuery(Context context, String query) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(query, 0);
    }

    public static void setStoredIntQuery(Context context,String query, int data) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(query, data)
                .apply();
    }

    public static String getStoredStringQuery(Context context, String query) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(query,"No filter");
    }

    public static void setStoredStringQuery(Context context,String query, String data) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(query, data)
                .apply();
    }
}
