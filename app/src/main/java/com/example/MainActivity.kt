package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.editor.EditorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StudioBackground
import com.example.ui.youtube.YouTubeAutoDubbingScreen

enum class AppDestination {
    EDITOR,
    YOUTUBE_AUTO_DUBBING
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = StudioBackground
                ) {
                    var currentDestination by remember { mutableStateOf(AppDestination.EDITOR) }

                    when (currentDestination) {
                        AppDestination.EDITOR -> {
                            EditorScreen(
                                onNavigateToYouTubeDubbing = {
                                    currentDestination = AppDestination.YOUTUBE_AUTO_DUBBING
                                }
                            )
                        }
                        AppDestination.YOUTUBE_AUTO_DUBBING -> {
                            BackHandler {
                                currentDestination = AppDestination.EDITOR
                            }
                            YouTubeAutoDubbingScreen(
                                onNavigateBack = {
                                    currentDestination = AppDestination.EDITOR
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
