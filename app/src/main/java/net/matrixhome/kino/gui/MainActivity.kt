package net.matrixhome.kino.gui

import android.app.AlertDialog
import android.app.DownloadManager
import android.app.UiModeManager
import android.content.*
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.matrixhome.kino.BuildConfig
import net.matrixhome.kino.R
import net.matrixhome.kino.data.*
import net.matrixhome.kino.fragments.FilmCatalogueFragment
import net.matrixhome.kino.services.BroadcastRecieverOnDowloadComplete
import net.matrixhome.kino.viewmodel.FilmViewModel
import net.matrixhome.kino.viewmodel.MainViewModelFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.util.*





class MainActivity : AppCompatActivity() {

    private val TAG: String = "FilmCatalogueActivity_log"
    private var downloadID: Long = 0

    private var updateStatus = true

    private lateinit var settingsManager: SettingsManager
    private lateinit var checkService: CheckService

    private lateinit var filmViewModel: FilmViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        if (!Connection.hasConnection(applicationContext)) {
            val intent = Intent(application, ConnectionActivity::class.java)
            startActivity(intent)
        }

        checkService = CheckService()
        if (!checkService.check()) {
            finish()
        }
        if(savedInstanceState == null){
            startFilmCatalougeFragment()
        }
        filmViewModel = ViewModelProvider(this, MainViewModelFactory())
            .get(FilmViewModel::class.java)


        val uiModeManager: UiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager

        Log.d(TAG, "onCreate: is TV? : " + (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION))
        Log.d(TAG, "onCreate: is normal? : " + (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_NORMAL))
    }

    private fun startFilmCatalougeFragment() {
        val filmCatalougeFragment = FilmCatalogueFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.container, filmCatalougeFragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        //TODO updater needs to make in coroutines cause of asynctask is depricated
        //вкл/выкл обновление
        checkUpdates()
    }

    fun checkUpdates() {
        getLastAppVersion()
        filmViewModel.lastAppVersion.observe(this, Observer {
            if (updateStatus) {
                if (it == 0) {
                    return@Observer
                }
                if (it <= BuildConfig.VERSION_CODE) {
                    return@Observer
                }
                val lastVersionNumber: String? = SettingsManager.get(applicationContext, "LastIgnoredUpdateVersion")
                if (lastVersionNumber != null) {
                    val liInt: Int = lastVersionNumber.toInt()
                    if (liInt >= filmViewModel.lastAppVersion.value!!)
                        return@Observer
                }
                updateApplication(it)
            }
            updateStatus = false
        })

    }


    private fun getLastAppVersion() {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            try {
                val url = URL(Constants.FILE_WITH_VERSION_NUMBER)
                val inStream = BufferedReader(InputStreamReader(url.openStream()))
                var str: String
                inStream.forEachLine {
                    val strNum: Int = it.indexOf("releaseVersionCode")
                    str = it.substring(strNum + ("releaseVersionCode").length).trim()
                    filmViewModel.lastAppVersion.postValue(str.toInt())
                }
                inStream.close()
            } catch (e: Exception) {
                Log.d(TAG, "getLastAppVersion error: " + e.message)
            }
        }
    }


    private fun updateApplication(lastAppVersion: Int) {
        Log.d(TAG, "updateApplication: " + lastAppVersion)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.alertForUpdate)
                .setCancelable(true)
                .setPositiveButton("Да", DialogInterface.OnClickListener { dialogInterface, i ->
                    Log.d(TAG, "updateApplication: click yes button")
                    val directory: File? = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(directory, "app-armeabi-v7a-release.apk")
                    if (file.exists()) {
                        file.delete()
                    }
                    var fileUri: Uri
                    if (Build.VERSION.SDK_INT < 24) {
                        fileUri = Uri.fromFile(file)
                        Log.d(TAG, "sdk version is < 24")
                        Log.d(TAG, "fileURi: " + fileUri.path)
                    }
                    if (Build.VERSION.SDK_INT >= 24) {
                        fileUri = FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".fileprovider", file)
                        Log.d(TAG, "sdk version is >= 24")
                        Log.d(TAG, "fileURi: " + fileUri.path)
                    }
                    val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(Constants.FILE_WITH_APK_UPDATE))
                            .setTitle("Обновление Кинозал")
                            .setDescription("Загрузка")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setDestinationUri(Uri.fromFile(file))
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)
                    Log.d(TAG, "file: " + file.toString())
                    val downloadManager: DownloadManager = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    Log.d(TAG, "create download manager")
                    downloadID = downloadManager.enqueue(request)
                    Log.d(TAG, "downloadID is " + downloadID)
                    application.registerReceiver(BroadcastRecieverOnDowloadComplete(downloadID, application), IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                    dialogInterface.dismiss()
                })
                .setNegativeButton("Нет", DialogInterface.OnClickListener { dialogInterface, i ->
                    Log.d(TAG, "updateApplication: click no button")

                    //WindowCompat.setDecorFitsSystemWindows(window, false)
                    SettingsManager.put(applicationContext, "LastIgnoredUpdateVersion", lastAppVersion.toString())
                })
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}