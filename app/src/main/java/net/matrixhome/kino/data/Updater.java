package net.matrixhome.kino.data;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import net.matrixhome.kino.BuildConfig;
import net.matrixhome.kino.R;
import net.matrixhome.kino.gui.FilmCatalogueActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import static android.content.Context.DOWNLOAD_SERVICE;

public class Updater{
    private final String TAG = "update_log";
    private Context context;
    public long downloadID;

    public Updater(FilmCatalogueActivity activity){
        this.context = activity;
    }

    private int getLastAppVersion() {
        try {
            // Create a URL for the desired page
            //file with version number
            URL url = new URL(Constants.FILE_WITH_VERSION_NUMBER);
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                int f = str.indexOf("releaseVersionCode");
                if (f != -1) {
                    str = str.substring(f + ("releaseVersionCode").length()).trim();
                    Log.d(TAG, "Last release version: " + str);
                    Log.d(TAG, "Current release version: " + BuildConfig.VERSION_CODE);
                    return Integer.valueOf(str);
                }
            }
            in.close();
            Log.d(TAG, "Failed to get last release version!");
        } catch (Exception e) {
            Log.d(TAG, "Failed to get last release version: " + e);
            e.printStackTrace();
        }
        return 0;
    }

    public void checkUpdates() {
        final Integer lastAppVersion = getLastAppVersion();
        Log.d(TAG, "checkUpdates: " + lastAppVersion);
        if (lastAppVersion == 0)
            return;
        if (lastAppVersion <= BuildConfig.VERSION_CODE) {
            Log.d(TAG, "App version is okay, skipping update");
            return;
        }
        String li = SettingsManager.get(context, "LastIgnoredUpdateVersion");
        if (li != null) {
            Integer liInt = Integer.parseInt(li);
            if (liInt >= lastAppVersion)
                return;
        }
        //UpdateApplication(lastAppVersion);
    }

    private void UpdateApplication(final Integer lastAppVersion) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.alertForUpdate)
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                                String apkUrl = "https://drive.google.com/uc?export=download&id=1ae2_zIRsPc3-k4jAYnUp4oamhLpwBHp5";
                                //intent.setDataAndType(Uri.parse(apkUrl), "application/vnd.android.package-archive");
                                intent.setData(Uri.parse(apkUrl));

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);*/


                                //file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "app-armeabi-v7a-release.apk");
                                //Uri apk = FileProvider.getUriForFile(getApplicationContext(),BuildConfig.APPLICATION_ID, file);
                                File directory = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                                File file = new File(directory, "app-armeabi-v7a-release.apk");
                                file.setReadable(true, false);
                                if (file.exists()) {
                                    file.delete();
                                }
                                Uri fileUri = Uri.fromFile(file);
                                if (Build.VERSION.SDK_INT >= 24) {
                                    fileUri = FileProvider.getUriForFile(context.getApplicationContext(),
                                            BuildConfig.APPLICATION_ID + ".fileprovider",
                                            file);
                                }
                                DownloadManager.Request request =
                                        new DownloadManager.Request(Uri.parse(Constants.FILE_WITH_APK_UPDATE))
                                                .setTitle("Обновление Кинозал")
                                                .setDescription("Загрузка")
                                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                .setDestinationUri(Uri.fromFile(file))
                                                .setAllowedOverMetered(true)
                                                .setAllowedOverRoaming(true);
                                DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                                downloadID = downloadManager.enqueue(request);
                                context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /*if (!getScreenOrientation()) {
                                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                                }*/
                                SettingsManager.put(context, "LastIgnoredUpdateVersion", lastAppVersion.toString());
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                try {
                    File directory = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(directory, "app-armeabi-v7a-release.apk");
                    file.setReadable(true, false);
                    if (file.exists()) {

                        if (Build.VERSION.SDK_INT > 22) {
                            Uri fileUri = Uri.fromFile(file);
                            Log.d(TAG, fileUri.getPath());
                            if (Build.VERSION.SDK_INT >= 24) {
                                fileUri = FileProvider.getUriForFile(context.getApplicationContext()
                                        , BuildConfig.APPLICATION_ID + ".fileprovider"
                                        , file);
                                Log.d(TAG, fileUri.toString());
                            }
                            Intent installer = new Intent(Intent.ACTION_VIEW, fileUri);
                            installer.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                            installer.setDataAndType(fileUri, "application/vnd.android" + ".package-archive");
                            installer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            context.getApplicationContext().startActivity(installer);
                        } else {
                            Intent install = new Intent(Intent.ACTION_VIEW);
                            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.getApplicationContext().startActivity(install);
                        }
                    } else {
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());

                }
            }
              /*File directory = getApplication().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(directory, "app-armeabi-v7a-release.apk");
                file.setReadable(true, false);
                Uri fileUri = Uri.fromFile(file);
                if (Build.VERSION.SDK_INT >= 24){
                    fileUri = FileProvider.getUriForFile(getApplication().getApplicationContext(),
                            BuildConfig.APPLICATION_ID + ".fileprovider",
                            file);
                }
                Intent installer = new Intent();
                installer.setAction(Intent.ACTION_INSTALL_PACKAGE);
                installer.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                installer.setDataAndType(fileUri, "application/vnd.android.package-archive");
                installer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                installer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                getApplicationContext().startActivity(installer);*/
        }
    };
}



