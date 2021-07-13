package net.matrixhome.kino.gui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.EventListener
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import net.matrixhome.kino.R
import net.matrixhome.kino.data.DataLoaderXML


private lateinit var episodeNumberFromVideoView: TextView

private var playWhenReady = true
private var currentWindow = 0
private var playbackPosition: Long = 0
private lateinit var main: Context
private lateinit var playerView: PlayerView
private var isSerial: Boolean = false

private lateinit var win: Window

private lateinit var player: SimpleExoPlayer
private lateinit var mediaItem: MediaItem
private lateinit var mediaSources: ArrayList<MediaItem>
private lateinit var playerControlView: PlayerControlView
private var playbackStateListener: VideoPlayer.PlaybackStateListener =
    VideoPlayer.PlaybackStateListener()

private lateinit var nameTVfromVideoView: TextView
private lateinit var nameLayoutDescription: ConstraintLayout
private lateinit var mainLayout: ConstraintLayout

private lateinit var link: String
private lateinit var links: Array<String>
private var seriesCount: Int = 0
private var currentSeasonNumber: Int = 0
private var currentEpisodeNumber: Int = 0
private lateinit var dataLoaderXml: DataLoaderXML
private val TAG = "VideoPlayer_log"


class VideoPlayer : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_player_fragment)
        win = window
        isSerial = intent.getBooleanExtra("isSerial", false)
        main = this
        //params = window.attributes
        link = intent.getStringExtra("link")
        Log.d(TAG, "onCreate: " + link)
        nameTVfromVideoView = findViewById(R.id.nameTVfromVideoView)
        episodeNumberFromVideoView = findViewById(R.id.episodeNumberFromVideoView)
        mediaSources = ArrayList()
        nameLayoutDescription = findViewById(R.id.nameLayoutDescription)
        mainLayout = findViewById(R.id.MainLayout)
        dataLoaderXml = DataLoaderXML(3)

        //playbackStateListener = PlaybackStateListener()
        playerView = findViewById(R.id.player_view)

        initializePlayer()
        setAllListeners()
    }

    fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        //TODO need to check next string
        playerView.player = player
        playerControlView = PlayerControlView(this)
        //TODO next string too needs checking
        playerControlView.player = player
        Log.d(TAG, "initializePlayer: " + intent.getBooleanExtra("isSerial", false))
        if (intent.getBooleanExtra("isSerial", false)) {
            currentSeasonNumber = intent.getStringExtra("currentSeasonNumber")!!.toInt()
            currentEpisodeNumber = intent.getStringExtra("currentEpisodeNumber")!!.toInt()
            Log.d(TAG, "initializePlayer: " + currentSeasonNumber)
            Log.d(TAG, "initializePlayer: " + currentEpisodeNumber)
            seriesCount = intent.getStringExtra("seriesCount")!!.toInt()
            Log.d(TAG, "initializePlayer: seriesCount " + seriesCount)
            episodeNumberFromVideoView.text =
                resources.getString(R.string.episode) + " " + currentEpisodeNumber
            nameTVfromVideoView.text = intent.getStringExtra("name")
            for (i in 1..seriesCount) {
                //links.set(i - 1, dataLoaderXml.getSerialLinkByID(currentSeasonNumber.toString(), i.toString()))
                Log.d(TAG, "onCreate: currentSeasonNumber")
                Log.d(TAG, "initializePlayer:  i = " + i)
                //Log.d(TAG, "onCreate: " + links[i - 1])
                mediaSources.add(
                    MediaItem.fromUri(
                        dataLoaderXml.getSerialLinkByID(
                            currentSeasonNumber.toString(),
                            i.toString()
                        )
                    )
                )
                player.addMediaItem(mediaSources.get(i - 1))
            }
            player.seekTo(currentEpisodeNumber - 1, C.TIME_UNSET)
            Log.d(TAG, "initializePlayer: " + player.mediaItemCount)
            Log.d(TAG, "initializePlayer: " + player.currentWindowIndex)
        } else {
            episodeNumberFromVideoView.visibility = View.GONE
            mediaItem = MediaItem.fromUri(link)
            player.setMediaItem(mediaItem)
            nameTVfromVideoView.text = intent.getStringExtra("name")
            playerView.setShowNextButton(false)
            playerView.setShowPreviousButton(false)

        }
        player.addListener(playbackStateListener)
        player.playWhenReady = true
        player.prepare()
    }

    fun setAllListeners() {
        mainLayout.setOnClickListener(View.OnClickListener {
            playerView.showController()
            Log.d(TAG, "setAllListeners: main layout layout listener")
        })

        playerView.setOnClickListener {
            Log.d(TAG, "onClick: player layout listener")
        }

        playerView.setControllerVisibilityListener(PlayerControlView.VisibilityListener {
            when (it) {
                PlayerControlView.VISIBLE -> {
                    nameLayoutDescription.visibility = View.VISIBLE
                }
                PlayerControlView.GONE -> {
                    nameLayoutDescription.visibility = View.GONE
                }
                PlayerControlView.INVISIBLE -> {
                    nameLayoutDescription.visibility = View.INVISIBLE
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        player.pause()
        player.playWhenReady = false
        if (Util.SDK_INT < 24) {
            if (playerView != null) {
                playerView.onPause()
            }
        }
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        player.stop()
        player.playWhenReady = false
        if (Util.SDK_INT >= 24) {
            if (playerView != null) {
                playerView.onPause()
            }
        }
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        if (Util.SDK_INT >= 24) {
            //initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        hideSystemUI()
        if (Util.SDK_INT < 24 || player is SimpleExoPlayer) {
            //initializePlayer()
            if (playerView != null) {
                playerView.onResume()
            }
        }
    }

    private fun hideSystemUI() {
        playerView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    fun backPressed() {
        onBackPressed()
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player.playWhenReady
            player.stop()
            player.playWhenReady = false
            playbackPosition = player.currentPosition
            currentWindow = player.currentWindowIndex
            player.removeListener(playbackStateListener)
            player.release()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                player.playWhenReady = !player.isPlaying
                if (!playerView.isControllerVisible) {
                    playerView.showController()
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (nameLayoutDescription.visibility != View.VISIBLE) {
                    player.seekTo(player.contentPosition + 15000)
                }
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (nameLayoutDescription.visibility != View.VISIBLE) {
                    player.seekTo(player.contentPosition + 15000)
                }
            }
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (!playerView.isControllerVisible) {
                    playerView.showController()
                }
            }
            KeyEvent.KEYCODE_ENTER -> {
                if (playerView.isControllerVisible) {
                    playerView.showController()
                }
            }
            KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                if (!playerView.isControllerVisible) {
                    playerView.showController()
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    class PlaybackStateListener : EventListener {
        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            episodeNumberFromVideoView.text =
                main.resources.getString(R.string.episode) + " " + (player.currentWindowIndex + 1)
            if (!playerView.isControllerVisible) {
                playerView.showController()
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                SimpleExoPlayer.STATE_BUFFERING -> {
                    Log.d(TAG, "onPlaybackStateChanged: STATE_BUFFERING")
                }
                SimpleExoPlayer.STATE_ENDED -> {
                    if (isSerial) {
                        Log.d(TAG, "onPlaybackStateChanged: serial, check media item count")
                        if (player.currentWindowIndex + 1 == seriesCount)
                            Log.d(
                                TAG,
                                "onPlaybackStateChanged: media iten count is end, finish activity"
                            )
                        //TODO needs onBackPressed
                        win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        playerView.showController()
                    } else {
                        Log.d(TAG, "onPlaybackStateChanged: film, finish activity")
                        //TODO needs onBackPressed
                        win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        playerView.showController()
                    }
                }
                SimpleExoPlayer.STATE_IDLE -> {
                    Log.d(TAG, "onPlaybackStateChanged: STATE_IDLE")
                }
                SimpleExoPlayer.STATE_READY -> {
                    Log.d(TAG, "onPlaybackStateChanged: STATE_READY")
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            //TODO remove in release
            Toast.makeText(main, "Something went wrong " + error, Toast.LENGTH_LONG).show()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (player.playWhenReady) run {
                Log.d(TAG, "onIsPlayingChanged: keeping screen on")
                win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                Log.d(TAG, "onIsPlayingChanged: screen can be locked")
                win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

}







