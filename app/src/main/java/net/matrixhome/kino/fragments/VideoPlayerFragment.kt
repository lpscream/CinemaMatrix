package net.matrixhome.kino.fragments


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.util.Util
import net.matrixhome.kino.R
import net.matrixhome.kino.databinding.CustomPlaybackControlBinding
import net.matrixhome.kino.databinding.VideoPlayerFragmentBinding

class VideoPlayerFragment : Fragment() {

    private var _binding: VideoPlayerFragmentBinding? = null
    private var _customControlBinding: CustomPlaybackControlBinding? = null
    private val binding get() = _binding!!
    private val customControlBinding get() = _customControlBinding!!
    private val TAG = "VideoPlayerFragment_log"
    private var playerState = false

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = C.TIME_UNSET

    private var isSerial: Boolean = false

    private var player: SimpleExoPlayer? = null
    private lateinit var mediaSources: ArrayList<MediaItem>
    private lateinit var playerControlView: PlayerControlView
    private var playbackStateListener: VideoPlayerFragment.PlaybackStateListener? = null

    private var seriesCount: Int = 0
    private var currentEpisodeNumber: Int = 0

    private lateinit var viewModel: VideoPlayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mediaSources = ArrayList()
        _binding = VideoPlayerFragmentBinding.inflate(inflater, container, false)
        _customControlBinding = CustomPlaybackControlBinding.inflate(inflater, container, false)
        //Toast.makeText(requireContext(), "episode number is " + arguments?.getString("episode_number"), Toast.LENGTH_SHORT).show()
        //Toast.makeText(requireContext(), "serial id is " + arguments?.getString("serial_id"), Toast.LENGTH_SHORT).show()
        //Toast.makeText(requireContext(), "movie id is " + arguments?.getString("id"), Toast.LENGTH_SHORT).show()
        val view = binding.root

        if (arguments?.getString("serial_id") != "") {
            isSerial = true
            Log.d(TAG, "onCreateView: this is serial")
        } else
            Log.d(TAG, "onCreateView: this is not serial")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModel()
        setObservers()
        if (viewModel.state){
            Log.d(TAG, "onActivityCreated: init new repo")
            viewModel.getRepositoryByID(arguments?.getString("id").toString())
        }
    }

    private fun initPlayer() {
        playbackStateListener = PlaybackStateListener()
        player = SimpleExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player
        playerControlView = PlayerControlView(requireContext())
        playerControlView.player = player
        Log.d(TAG, "initPlayer: " + arguments?.getString("serial_id"))
        if (arguments?.getString("serial_id") != "") {
            currentEpisodeNumber = arguments?.getString("episode_number")!!.toInt()
            seriesCount = viewModel.getFilmRepository().value?.series?.size!!
            binding.episodeNumberFromVideoView.text =
                resources.getString(R.string.episode) + " " + currentEpisodeNumber
            binding.nameTVfromVideoView.text = viewModel.getFilmRepository().value?.serial_name
            for (i in viewModel.getLinksRepo().value?.indices!!) {
                mediaSources.add(MediaItem.fromUri(viewModel.getLinksRepo().value!![i]))
                player?.addMediaItem(mediaSources.get(i))
            }
            Log.d(TAG, "initPlayer: mediaItemCount " + player?.mediaItemCount)
            player?.seekTo(currentEpisodeNumber - 1, playbackPosition)
        } else {
                binding.episodeNumberFromVideoView.visibility = View.GONE
                binding.nameTVfromVideoView.text = viewModel.getFilmRepository().value?.name + " (" + viewModel.getFilmRepository().value?.original_name + ")"
            Log.d(TAG, "initPlayer: " + viewModel.getLinksRepo().value!![0])
            mediaSources.add(MediaItem.fromUri(viewModel.getLinksRepo().value!![0]))
            player?.addMediaItem(mediaSources.get(0))
        }
        player?.addListener(playbackStateListener!!)
        player?.playWhenReady = true
        player?.prepare()
        playerState = true
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player?.playWhenReady!!
            player?.stop()
            player?.playWhenReady = false
            playbackPosition = player?.currentPosition!!
            viewModel.playbackPosition = player?.currentPosition!!
            currentWindow = player?.currentWindowIndex!!
            player?.removeListener(playbackStateListener!!)
            player?.release()
        }
    }

    private fun hideSystemUI() {
        binding.playerView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(requireActivity()).get(VideoPlayerViewModel::class.java)
    }

    private fun setObservers(){
        viewModel.getFilmRepository().observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "setObservers: film repo created")
            viewModel.initVideoLinks()
            binding.nameTVfromVideoView.text = viewModel.getFilmRepository().value?.name
        })
        viewModel.getLinksRepo().observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "setObservers: " + viewModel.getLinksRepo().value?.size)
            if (viewModel.getLinksRepo().value?.size == viewModel.getFilmRepository().value?.series?.size) {
                initPlayer()
                setAllLIsteners()
            }else{
                if (viewModel.getFilmRepository().value?.series?.size == 0){
                    initPlayer()
                    setAllLIsteners()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        hideSystemUI()
        if (player != null) {
            if (Util.SDK_INT < 24 || player is SimpleExoPlayer) {
                //initPlayer()
                binding.playerView.onResume()
                player?.playWhenReady = true
                player?.prepare()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
        player?.playWhenReady = false
        if (Util.SDK_INT < 24) {
            if (binding.playerView != null) {
                binding.playerView.onPause()
            }
        }
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        player?.stop()
        player?.playWhenReady = false
        if (Util.SDK_INT >= 24) {
            if (binding.playerView != null) {
                binding.playerView.onPause()
            }
        }
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        releasePlayer()
        super.onDestroy()
    }

    fun setAllLIsteners() {
        binding.MainLayout.setOnClickListener(View.OnClickListener {
            binding.playerView.showController()
        })
        binding.playerView.setControllerVisibilityListener {
            when (it) {
                PlayerControlView.VISIBLE -> {
                    binding.nameLayoutDescription.visibility = View.VISIBLE
                }
                PlayerControlView.GONE -> {
                    binding.nameLayoutDescription.visibility = View.GONE
                }
                PlayerControlView.INVISIBLE -> {
                    binding.nameLayoutDescription.visibility = View.INVISIBLE
                }


            }
        }
    }

    inner class PlaybackStateListener() : Player.EventListener {

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
            binding.episodeNumberFromVideoView.text =
                requireContext().resources.getString(R.string.episode) + " " + (player?.currentWindowIndex?.plus(
                    1
                ))
            if (!binding.playerView.isControllerVisible) {
                binding.playerView.showController()
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                SimpleExoPlayer.STATE_BUFFERING -> {
                    Log.d(TAG, "onPlaybackStateChanged: STATE_BUFFERING")
                }
                SimpleExoPlayer.STATE_ENDED -> {
                    if (isSerial) {
                        if (player?.currentWindowIndex == seriesCount - 1) {
                            Log.d(
                                TAG,
                                "onPlaybackStateChanged: media item count is end, move back"
                            )
                        }
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        binding.playerView.showController()
                        requireActivity().onBackPressed()
                    } else {
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        binding.playerView.showController()
                        requireActivity().onBackPressed()
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
            Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_LONG).show()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (player!!.playWhenReady) kotlin.run {
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

