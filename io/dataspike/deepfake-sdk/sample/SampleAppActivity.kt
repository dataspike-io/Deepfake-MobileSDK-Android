/**
 * SampleAppActivity.kt
 *
 * This is the main entry point of the sample application.
 * It sets up the Jetpack Compose content and initializes the ViewModel.
 */
package com.example.yourapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

class SampleAppActivity : ComponentActivity() {

    private val viewModel: SampleAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YourAppTheme {
                SampleAppScreen(viewModel = viewModel)
            }
        }
    }
}
