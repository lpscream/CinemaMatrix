package net.matrixhome.kino.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SettingsManager {

    public static String get(Context mContext, String key) {
        SharedPreferences settings = mContext.getSharedPreferences("net.matrixhome.kino", Context.MODE_PRIVATE);

        //SharedPreferences settings = PreferenceManager
        //      .getDefaultSharedPreferences(mContext);
        String data = settings.getString(key, null);
        if (data == null)
            Log.d("SettingsManager", "No settings " + key + " is stored! ");
        else
            Log.d("SettingsManager", "Got settings " + key + " equal to " + data);
        return data;
    }


    public static void put(Context mContext, String key, String value) {
        SharedPreferences settings = mContext.getSharedPreferences("net.matrixhome.kino", Context.MODE_PRIVATE);

        // SharedPreferences settings = PreferenceManager
        //      .getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        Log.d("SettingsManager", "Saved setting " + key + " equal to " + value);
        editor.commit();
    }

    public void putPref(Context mContext, String key, int value) {
        SharedPreferences settings = mContext.getSharedPreferences("net.matrixhome.kino", Context.MODE_PRIVATE);
        Log.d("settings_manager", "putPref: " + key + " " + value + " " + mContext);

        SharedPreferences.Editor editor = settings.edit();
        settings.edit();
        editor.putInt(key, value);
        editor.apply();
        //editor.commit();
    }

    public int getPref(Context mContext, String key, int def) {
        SharedPreferences settings = mContext.getSharedPreferences("net.matrixhome.kino", Context.MODE_PRIVATE);
        Log.d("settings_manager", "getPref: " + key + " " + def + " " + mContext + " " + settings.getInt(key, def));
        if (settings.contains(key)) {
            Log.d("settings_manager", "getPref: " + key + " contains true\n" + settings.getInt(key, def));
            return settings.getInt(key, def);
        } else {
            Log.d("settings_manager", "getPref: " + key + " contains false");
            return def;
        }
    }


}