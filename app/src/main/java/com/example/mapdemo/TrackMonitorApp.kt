package com.example.mapdemo

import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.mapdemo.presentation.TrackMonitorScreen
import com.example.mapdemo.presentation.TrackMonitorViewModel

@Composable
fun TrackMonitorApp(viewModel: TrackMonitorViewModel) {
    MaterialTheme {
        Surface {
            TrackMonitorScreen(viewModel = viewModel)
        }
    }
}
