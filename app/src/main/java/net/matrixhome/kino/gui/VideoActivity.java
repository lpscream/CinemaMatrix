package net.matrixhome.kino.gui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

import net.matrixhome.kino.R;
import net.matrixhome.kino.data.DataLoaderXML;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class VideoActivity extends AppCompatActivity {

    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";
    private static final String TAG = "videoActivity_log";
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private MediaItem mediaItem;
    private ArrayList<MediaItem> mediaSources;
    private PlayerControlView playerControlView;
    private PlaybackStateListener playbackStateListener;
    private TextView nameTVfromVideoView, episodeNumberFromVideoView;
    private ConstraintLayout nameLayoutDescription, mainLayout;
    private String link;
    private String[] links;
    private int seriesCount, currentSeasonNumber, currentEpisodeNumber;
    private DataLoaderXML dataLoaderXML;
    private WindowManager.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        params = getWindow().getAttributes();
        link = getIntent().getStringExtra("link");
        Log.d(TAG, "onCreate: " + link);
        nameTVfromVideoView = findViewById(R.id.nameTVfromVideoView);
        episodeNumberFromVideoView = findViewById(R.id.episodeNumberFromVideoView);
        mediaSources = new ArrayList<>();
        nameLayoutDescription = findViewById(R.id.nameLayoutDescription);
        mainLayout = findViewById(R.id.MainLayout);

        dataLoaderXML = new DataLoaderXML(3);
        playbackPosition = 0;
        playbackStateListener = new PlaybackStateListener();
        playerView = findViewById(R.id.exoVideoView);
        //initializePlayer();
        setAllListeners();
        Log.d(TAG, "onCreate: finish");
    }


    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        playerControlView = new PlayerControlView(this);
        playerControlView.setPlayer(player);
        Log.d(TAG, "onCreate: " + getIntent().getBooleanExtra("isSerial", false));
        if (getIntent().getBooleanExtra("isSerial", false)) {
            currentSeasonNumber = Integer.parseInt(getIntent().getStringExtra("currentSeasonNumber"));
            currentEpisodeNumber = Integer.parseInt(getIntent().getStringExtra("currentEpisodeNumber"));
            Log.d(TAG, "initializePlayer: currentEpisodeNumber " + currentEpisodeNumber);
            Log.d(TAG, "initializePlayer: currentEpisoneNumber " + currentEpisodeNumber);
            seriesCount = Integer.parseInt(getIntent().getStringExtra("seriesCount"));
            episodeNumberFromVideoView.setText(getResources().getString(R.string.episode) + " " + currentEpisodeNumber);
            links = new String[seriesCount];
            nameTVfromVideoView.setText(getIntent().getStringExtra("name"));
            for (int i = 1; i <= seriesCount; i++) {
                links[i - 1] = dataLoaderXML.getSerialLinkByID(String.valueOf(currentSeasonNumber), String.valueOf(i));
                Log.d(TAG, "onCreate: " + currentSeasonNumber);
                Log.d(TAG, "onCreate: " + links[i - 1]);
                mediaSources.add(MediaItem.fromUri(links[i - 1]));
                //необходимо создать массив с плейлистом
                player.addMediaItem(mediaSources.get(i - 1));
            }
            player.seekTo(currentEpisodeNumber - 1, C.TIME_UNSET);
            Log.d(TAG, "initializePlayer: media item count is " + player.getMediaItemCount());
            Log.d(TAG, "initializePlayer: current media item is " + player.getCurrentWindowIndex());
        } else {
            episodeNumberFromVideoView.setVisibility(View.GONE);
            mediaItem = MediaItem.fromUri(link);
            player.setMediaItem(mediaItem);
            nameTVfromVideoView.setText(getIntent().getStringExtra("name"));
        }
        player.addListener(playbackStateListener);
        player.setPlayWhenReady(true);
        player.prepare();
    }

    private void setAllListeners() {
        mainLayout.setOnClickListener(view -> {
            playerView.showController();
            Log.d(TAG, "onClick: main layout layout listener");
        });

        playerView.setOnClickListener(view -> {
            //playerView.showController();
            Log.d(TAG, "onClick: player layout listener");
        });

        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                switch (visibility) {
                    case PlayerControlView.VISIBLE:
                        nameLayoutDescription.setVisibility(View.VISIBLE);
                        break;
                    case PlayerControlView.GONE:
                        nameLayoutDescription.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        if (Util.SDK_INT < 24) {
            if (playerView != null) {
                //playerView.onPause();
            }
            //releasePlayer();
        }
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.setPlayWhenReady(false);
        if (Util.SDK_INT >= 24) {
            if (playerView != null) {
                //playerView.onPause();
            }
            //releasePlayer();
        }
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        releasePlayer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        if (Util.SDK_INT >= 24) {
            //initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("playWhenReady", player.getPlayWhenReady());
        outState.putLong("playbackPosition", player.getCurrentPosition());
        outState.putInt("currentWindow", player.getCurrentWindowIndex());
        Bundle bundle = new Bundle();
        bundle.putSerializable("array", mediaSources);
        outState.putBundle("mediaItems", bundle);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.removeListener(playbackStateListener);
            player.release();
            player = null;
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: " + savedInstanceState.getLong("playbackPosition"));
        playbackPosition = savedInstanceState.getLong("playbackPosition");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d(TAG, "onSaveInstanceState: current position " + player.getCurrentPosition());
        outState.putLong("currentPosition", player.getCurrentPosition());
        Log.d(TAG, "onSaveInstanceState: play when ready " + player.getPlayWhenReady());
        outState.putBoolean("playWhenReady", player.getPlayWhenReady());
        Log.d(TAG, "onSaveInstanceState: current media item " + player.getCurrentMediaItem());
        outState.putInt("currentMediaItem", player.getCurrentWindowIndex());
        Log.d(TAG, "onSaveInstanceState: current season number " + currentSeasonNumber);
        outState.putInt("currentSeasonNumber", currentSeasonNumber);
        Log.d(TAG, "onSaveInstanceState: is serial " + getIntent().getBooleanExtra("isSerial", false));
        outState.putBoolean("isSerial", getIntent().getBooleanExtra("isSerial", false));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: " + event);
        Log.d(TAG, "onKeyDown: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (player.isPlaying()) {
                    player.setPlayWhenReady(false);
                    player.getPlaybackState();
                } else {
                    player.setPlayWhenReady(true);
                }
                if (!playerView.isControllerVisible()) {
                    playerView.showController();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (nameLayoutDescription.getVisibility() != View.VISIBLE) {
                    player.seekTo(player.getContentPosition() + 15000);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (nameLayoutDescription.getVisibility() != View.VISIBLE) {
                    player.seekTo(player.getContentPosition() - 15000);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (!playerView.isControllerVisible()) {
                    playerView.showController();
                }
            case KeyEvent.KEYCODE_ENTER:
                if (!playerView.isControllerVisible()) {
                    playerView.showController();
                }
                break;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                if (!playerView.isControllerVisible()) {
                    playerView.showController();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                Log.d(TAG, "onKeyDown: KEYCODE_MEDIA_NEXT");
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                Log.d(TAG, "onKeyDown: KEYCODE_MEDIA_PREVIOUS");
            case KeyEvent.KEYCODE_NAVIGATE_NEXT:
                Log.d(TAG, "onKeyDown: KEYCODE_NAVIGATE_NEXT");
                break;
            case KeyEvent.KEYCODE_NAVIGATE_PREVIOUS:
                Log.d(TAG, "onKeyDown: KEYCODE_NAVIGATE_PREVIOUS");
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class PlaybackStateListener implements Player.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            Log.d(TAG, "onTimelineChanged: " + reason);
        }

        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            Log.d(TAG, "onMediaItemTransition: reason " + reason);
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            //change episode number in top of activity
            episodeNumberFromVideoView.setText(getResources().getString(R.string.episode) + " " + (player.getCurrentWindowIndex() + 1));
            //when next item is start - then show controller
            if (!playerView.isControllerVisible()) {
                playerView.showController();
            }
            Log.d(TAG, "onTracksChanged: track has changed on num " + player.getCurrentWindowIndex());
            Log.d(TAG, "onTracksChanged: " + player.getCurrentMediaItem().mediaId);
        }

        @Override
        public void onIsLoadingChanged(boolean isLoading) {
            Log.d(TAG, "onIsLoadingChanged: " + isLoading);
        }

        @Override
        public void onPlaybackStateChanged(int state) {
            switch (state) {
                case SimpleExoPlayer.STATE_BUFFERING:
                    Log.d(TAG, "onPlaybackStateChanged: STATE_BUFFERING");
                    break;
                case SimpleExoPlayer.STATE_ENDED:
                    if (getIntent().getBooleanExtra("isSerial", false)) {
                        Log.d(TAG, "onPlaybackStateChanged: serial, check media item count");
                        if (player.getCurrentWindowIndex() + 1 == seriesCount) {
                            Log.d(TAG, "onPlaybackStateChanged: media iten count is end, finish activity");
                            onBackPressed();
                        }
                    } else {
                        Log.d(TAG, "onPlaybackStateChanged: film, finish activity");
                        onBackPressed();
                    }
                    Log.d(TAG, "onPlaybackStateChanged: STATE_ENDED");
                    break;
                case SimpleExoPlayer.STATE_IDLE:
                    Log.d(TAG, "onPlaybackStateChanged: STATE_IDLE");
                    break;
                case SimpleExoPlayer.STATE_READY:
                    Log.d(TAG, "onPlaybackStateChanged: STATE_READY");
                    break;
            }
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            Log.d(TAG, "onPlayWhenReadyChanged: playWhenReady " + playWhenReady);
            Log.d(TAG, "onPlayWhenReadyChanged: reason " + reason);
        }

        @Override
        public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {
            Log.d(TAG, "onPlaybackSuppressionReasonChanged: " + playbackSuppressionReason);
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            Log.d(TAG, "onIsPlayingChanged: isPlaying " + isPlaying);
            if (player.getPlayWhenReady()) {
                //screen lock is off/screen can lock
                Log.d(TAG, "onIsPlayingChanged: keeping screen on");
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                //screen lock is on/screen can't lock
                Log.d(TAG, "onIsPlayingChanged: screen can be locked");

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.d(TAG, "onPlayerError: " + error);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            Log.d(TAG, "onPositionDiscontinuity: reason " + reason);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Log.d(TAG, "onPlaybackParametersChanged: " + playbackParameters);
        }

        @Override
        public void onExperimentalOffloadSchedulingEnabledChanged(boolean offloadSchedulingEnabled) {
            Log.d(TAG, "onExperimentalOffloadSchedulingEnabledChanged: " + offloadSchedulingEnabled);
        }
    }
}



