package com.example.mapdemo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mapdemo.data.remote.NetworkModule
import com.example.mapdemo.data.repository.TrackingRepositoryImpl
import com.example.mapdemo.domain.usecase.FilterTrackingInfoUseCase
import com.example.mapdemo.domain.usecase.GetTrackingInfoUseCase
import com.example.mapdemo.domain.usecase.SortTrackingInfoUseCase
import com.example.mapdemo.presentation.TrackMonitorViewModel

class TrackMonitorViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(TrackMonitorViewModel::class.java)) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }

        val api = NetworkModule.provideApi()
        val repo = TrackingRepositoryImpl(api)

        @Suppress("UNCHECKED_CAST")
        return TrackMonitorViewModel(
            getTrackingInfoUseCase = GetTrackingInfoUseCase(repo),
            filterTrackingInfoUseCase = FilterTrackingInfoUseCase(),
            sortTrackingInfoUseCase = SortTrackingInfoUseCase(),
        ) as T
    }
}
