package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.navigation.NeuroTalksApp
import com.example.ui.theme.NeuroTalksTheme
import com.example.viewmodel.NeuroTalksViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NeuroTalksViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            NeuroTalksTheme(darkTheme = uiState.settings.darkCanopyTheme) {
                NeuroTalksApp(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }
}
