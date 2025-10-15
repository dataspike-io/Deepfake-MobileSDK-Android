/**
 * SampleAppScreen.kt
 *
 * This Composable function defines the user interface of the sample app.
 * It provides a simple layout to initialize the SDK, select a video, and see the analysis results.
 */
package com.example.yourapp

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import androidx.media3.common.Player

@Composable
fun SampleAppScreen(viewModel: SampleAppViewModel) {
    val analysisState by viewModel.analysisState.collectAsState()
    val player by viewModel.player.collectAsState()
    val context = LocalContext.current

    // Remember the PlayerView instance to pass to the SDK
    var playerView: PlayerView? by remember { mutableStateOf(null) }

    // Initialize the SDK when the Composable enters the composition
    LaunchedEffect(Unit) {
        viewModel.initialize(context.applicationContext)
    }

    // Media picker launcher to select a video
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.onVideoSelected(it, context) }
        }
    )

    // Start analysis automatically when a new player is ready
    LaunchedEffect(player) {
        player?.let { p ->
            p.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY && playerView != null) {
                        viewModel.startAnalysis(playerView!!)
                    }
                }
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Video player view
        Card(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).also { pv -> playerView = pv }
                },
                update = { view ->
                    view.player = player
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Status and Score display
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Text("Status: ${analysisState.statusText}", fontWeight = FontWeight.Bold)
            Text("Score: ${String.format("%.2f", analysisState.score)}", color = analysisState.scoreColor)
        }

        // Control buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { mediaPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) },
                enabled = analysisState.isSdkReady,
                modifier = Modifier.weight(1f)
            ) {
                Text("Select Video")
            }
            Button(
                onClick = { viewModel.playPause() },
                enabled = player != null,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (analysisState.isPlaying) "Pause" else "Play")
            }
        }
    }
}
