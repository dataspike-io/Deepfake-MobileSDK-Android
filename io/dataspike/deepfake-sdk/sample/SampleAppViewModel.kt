/**
 * SampleAppViewModel.kt
 *
 * This ViewModel contains the core business logic for interacting with the Deepfake SDK.
 * It handles initializing the SDK, managing video playback with ExoPlayer, and processing analysis results.
 */
package com.example.yourapp

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import io.dataspike.deepfake.sdk.AnalysisCallback
import io.dataspike.deepfake.sdk.AnalysisResult
import io.dataspike.deepfake.sdk.DeepfakeSDK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// A simple data class to hold the UI state related to the analysis.
data class AnalysisState(
    val statusText: String = "Not Initialized",
    val score: Float = 0.0f,
    val scoreColor: Color = Color.Black,
    val isSdkReady: Boolean = false,
    val isPlaying: Boolean = false
)

class SampleAppViewModel : ViewModel(), AnalysisCallback {

    private val deepfakeSDK by lazy { DeepfakeSDK.getInstance() }

    private val _analysisState = MutableStateFlow(AnalysisState())
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _player = MutableStateFlow<Player?>(null)
    val player: StateFlow<Player?> = _player.asStateFlow()

    /**
     * Initializes the DeepfakeSDK. This should be called once with an application context.
     */
    fun initialize(context: Context) {
        if (_analysisState.value.isSdkReady) return

        _analysisState.value = _analysisState.value.copy(statusText = "Initializing SDK...")

        val success = deepfakeSDK.initialize(
            applicationContext = context,
            // IMPORTANT: Replace this with your actual license key.
            license = "YOUR_LICENSE_KEY_HERE",
            deepfakeThreshold = 0.5f
        )

        if (success) {
            _analysisState.value = _analysisState.value.copy(
                statusText = "SDK Ready",
                isSdkReady = true
            )
        } else {
            _analysisState.value = _analysisState.value.copy(
                statusText = "SDK Initialization Failed"
            )
        }
    }

    /**
     * Creates a new ExoPlayer instance when a video is selected.
     */
    fun onVideoSelected(uri: Uri, context: Context) {
        // Release any previous player instance
        _player.value?.release()

        // Create and prepare a new player
        val newPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true // Start playing automatically
        }
        _player.value = newPlayer
    }

    /**
     * Starts the deepfake analysis on the video rendering view and sets up player listeners.
     */
    fun startAnalysis(playerView: PlayerView) {
        val currentPlayer = _player.value ?: return

        // Set up listeners for playback state
        currentPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val status = if (isPlaying) "Analyzing..." else "Paused"
                _analysisState.value = _analysisState.value.copy(
                    statusText = status,
                    isPlaying = isPlaying
                )
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    _analysisState.value = _analysisState.value.copy(statusText = "Finished")
                    deepfakeSDK.stopAnalysis()
                }
            }
        })

        // Start the SDK analysis
        val success = deepfakeSDK.startAnalysis(
            view = playerView,
            callback = this
        )

        if (!success) {
            _analysisState.value = _analysisState.value.copy(statusText = "Analysis Start Failed")
        }
    }

    /**
     * Toggles the playback state of the current video.
     */
    fun playPause() {
        val currentPlayer = _player.value ?: return
        if (currentPlayer.isPlaying) {
            currentPlayer.pause()
        } else {
            currentPlayer.play()
        }
    }

    // --- AnalysisCallback Implementation ---

    override fun onFrameAnalysisResult(result: AnalysisResult, message: String?) {
        val scoreColor = if (result.score > 0.5f) Color.Red else Color.Green
        _analysisState.value = _analysisState.value.copy(
            score = result.score,
            scoreColor = scoreColor
        )
    }

    override fun onAnalysisError(error: String) {
        _analysisState.value = _analysisState.value.copy(statusText = "Error: $error")
    }

    override fun onAudioAnalysisResult(result: String) {
        // For this simplified example, we ignore audio results.
    }

    override fun onCleared() {
        super.onCleared()
        _player.value?.release()
        deepfakeSDK.shutdownWithTimeout()
    }
}
