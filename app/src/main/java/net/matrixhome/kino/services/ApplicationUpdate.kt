package net.matrixhome.kino.services

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.matrixhome.kino.BuildConfig
import net.matrixhome.kino.data.Constants
import net.matrixhome.kino.data.SettingsManager
import net.matrixhome.kino.gui.FilmCatalogueActivity
import net.matrixhome.kino.viewmodel.FilmViewModel
import net.matrixhome.kino.viewmodel.MainViewModelFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class ApplicationUpdate(val activity: FilmCatalogueActivity) {
    private val TAG = "ApplicationUpdate_log"

    var filmViewModel:FilmViewModel = ViewModelProvider(activity, MainViewModelFactory(activity.application!!)).get(FilmViewModel::class.java)


    fun checkUpdates(){
        val lastAppVersion = getLastAppVersion()
        filmViewModel.lastAppVersion.observe(activity, Observer {
            Log.d(TAG, "checkUpdates: " + "trying check latest app version")
            if (lastAppVersion == 0){
                Log.d(TAG, "checkUpdates: " + lastAppVersion)
                return@Observer
            }
            if (lastAppVersion <= BuildConfig.VERSION_CODE){
                Log.d(TAG, "checkUpdates: " + lastAppVersion)
                Log.d(TAG, "App version is okay, skipping update")
                return@Observer
            }
            var li: String = SettingsManager.get(activity.applicationContext
                    , "LastIgnoredUpdateVersion")

            if (li != null){
                var liInt: Int = li.toInt()
                if (liInt >= lastAppVersion)
                    return@Observer
            }
        })

        //updateApplication
    }


    private fun getLastAppVersion(): Int {
        var numVersion: Int = 0
        var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            var url: URL = URL(Constants.FILE_WITH_VERSION_NUMBER)
            var inStream: BufferedReader = BufferedReader(InputStreamReader(url.openStream()))
            var str: String? = ""
            inStream.forEachLine {
                Log.d(TAG, "getLastAppVersion: lines = " + it)
                var strNum: Int = it.indexOf("releaseVersionCode")
                str = it.substring(strNum + ("releaseVersionCode").length).trim()
                Log.d(TAG, "getLastAppVersion: lines = " + str)
                numVersion = str!!.toInt()
                Log.d(TAG, "getLastAppVersion: " + numVersion)
            }
            inStream.close()
        }

        return numVersion
    }



}