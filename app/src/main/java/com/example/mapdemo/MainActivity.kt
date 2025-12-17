package com.example.mapdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.mapdemo.di.TrackMonitorViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this, TrackMonitorViewModelFactory())[com.example.mapdemo.presentation.TrackMonitorViewModel::class.java]
        setContent {
            TrackMonitorApp(viewModel)
        }
    }
}
