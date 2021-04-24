package net.matrixhome.kino.services

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import net.matrixhome.kino.BuildConfig
import java.io.File

class BroadcastRecieverOnDowloadComplete(val downloadID: Long, val application: Application) : BroadcastReceiver() {

    val TAG = "B_castRecieverLoadComp"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: " + "created")
        var id: Long = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadID == id) {
            try {
                var directory: File? = context!!.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                var file: File = File(directory, "app-armeabi-v7a-release.apk")
                file.setReadable(true, false)
                if (file.exists()) {
                    if (Build.VERSION.SDK_INT > 22) {
                        var fileUri: Uri = Uri.fromFile(file)
                        Log.d(TAG, "onReceive: " + fileUri.toString())
                        if (Build.VERSION.SDK_INT >= 24) {
                            fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file)
                            Log.d(TAG, "onReceive: " + fileUri.toString())
                            var installer = Intent(Intent.ACTION_VIEW, fileUri)
                            installer.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                            installer.setDataAndType(fileUri, "application/vnd.android" + ".package-archive")
                            installer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            context.applicationContext.startActivity(installer)
                        }else{
                            var install: Intent = Intent(Intent.ACTION_VIEW)
                            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.applicationContext.startActivity(install)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "onReceive: error" + e.message)
            }
        }
    }
}