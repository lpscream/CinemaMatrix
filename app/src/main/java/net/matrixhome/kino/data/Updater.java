package net.matrixhome.kino.data;


import android.os.AsyncTask;
import android.util.Log;

import net.matrixhome.kino.BuildConfig;
import net.matrixhome.kino.gui.MainActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Updater extends AsyncTask<MainActivity, Void, Void> {
    private final String TAG = "update_log";

    protected Void doInBackground(MainActivity... activity) {
        checkUpdates(activity[0]);
        return null;
    }

    private int getLastAppVersion() {
        try {
            // Create a URL for the desired page
            //file with version number
            URL url = new URL("https://drive.google.com/uc?export=download&id=1aYgzapW-boED_DiLtIQaV6NXJ3eP75Qh");
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                int f = str.indexOf("releaseVersionCode");
                if (f != -1) {
                    str = str.substring(f + ("releaseVersionCode").length()).trim();
                    Log.d("matrix_logs", "Last release version: " + str);
                    Log.d("matrix_logs", "Current release version: " + BuildConfig.VERSION_CODE);
                    return Integer.valueOf(str);
                }
            }
            in.close();
            Log.d("matrix_logs", "Failed to get last release version!");
        } catch (Exception e) {
            Log.d("matrix_logs", "Failed to get last release version: " + e);
            e.printStackTrace();
        }
        return 0;
    }

    private void checkUpdates(final MainActivity activity) {
        final Integer lastAppVersion = getLastAppVersion();

        Log.d(TAG, "checkUpdates: " + lastAppVersion);
        if (lastAppVersion == 0)
            return;
        if (lastAppVersion <= BuildConfig.VERSION_CODE) {
            Log.d("matrix_logs", "App version is okay, skipping update");
            return;
        }
        String li = SettingsManager.get(activity, "LastIgnoredUpdateVersion");
        if (li != null) {
            Integer liInt = Integer.parseInt(li);
            if (liInt >= lastAppVersion)
                return;
        }

        activity.UpdateApplication(lastAppVersion);
    }
}

