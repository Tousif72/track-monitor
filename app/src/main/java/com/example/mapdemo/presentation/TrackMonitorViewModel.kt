package com.example.mapdemo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapdemo.domain.usecase.FilterTrackingInfoUseCase
import com.example.mapdemo.domain.usecase.GetTrackingInfoUseCase
import com.example.mapdemo.domain.usecase.SortOrder
import com.example.mapdemo.domain.usecase.SortTrackingInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackMonitorViewModel(
    private val getTrackingInfoUseCase: GetTrackingInfoUseCase,
    private val filterTrackingInfoUseCase: FilterTrackingInfoUseCase,
    private val sortTrackingInfoUseCase: SortTrackingInfoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackMonitorUiState())
    val uiState: StateFlow<TrackMonitorUiState> = _uiState

    fun load() {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = getTrackingInfoUseCase()
            result
                .onSuccess { list ->
                    _uiState.update {
                        val nextItems = sortTrackingInfoUseCase(list, it.sortOrder)
                        val nextState = it.copy(
                            isLoading = false,
                            items = nextItems,
                            errorMessage = null,
                        )

                        if (nextState.viewMode == ViewMode.MAP && nextState.selectedOnMap == null) {
                            nextState.copy(selectedOnMap = visibleItems(nextState).firstOrNull())
                        } else {
                            nextState
                        }
                    }
                }
                .onFailure { t ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = t.message ?: "Something went wrong",
                        )
                    }
                }
        }
    }

    fun onToggleViewMode(viewMode: ViewMode) {
        _uiState.update {
            val nextState = it.copy(viewMode = viewMode)
            if (viewMode == ViewMode.MAP && nextState.selectedOnMap == null) {
                nextState.copy(selectedOnMap = visibleItems(nextState).firstOrNull())
            } else {
                nextState
            }
        }
    }

    fun onToggleSearch() {
        _uiState.update { it.copy(isSearchVisible = !it.isSearchVisible) }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onToggleSort() {
        _uiState.update {
            val nextOrder = if (it.sortOrder == SortOrder.DESC) SortOrder.ASC else SortOrder.DESC
            it.copy(sortOrder = nextOrder, items = sortTrackingInfoUseCase(it.items, nextOrder))
        }
    }

    fun onSelectOnMap(item: com.example.mapdemo.domain.model.TrackingInfo?) {
        _uiState.update { it.copy(selectedOnMap = item) }
    }

    fun visibleItems(state: TrackMonitorUiState = _uiState.value): List<com.example.mapdemo.domain.model.TrackingInfo> {
        val filtered = filterTrackingInfoUseCase(state.items, state.query)
        return sortTrackingInfoUseCase(filtered, state.sortOrder)
    }
}
