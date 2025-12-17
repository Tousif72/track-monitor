package com.example.mapdemo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mapdemo.R
import com.example.mapdemo.domain.model.TrackingInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackMonitorScreen(
    viewModel: TrackMonitorViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val visibleItems = viewModel.visibleItems(uiState)

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6FB)),
    ) {
        CenterAlignedTopAppBar(
            title = { Text(text = "Track Monitor") },
            navigationIcon = {
                IconButton(onClick = { viewModel.onToggleSort() }) {
                    Icon(imageVector = Icons.Filled.SwapVert, contentDescription = "Sort")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.onToggleSearch() }) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFF0A3D91),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White,
            ),
        )

        if (uiState.isSearchVisible) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                singleLine = true,
                placeholder = { Text(text = "Search by plate, driver, location") },
            )
        }

        SegmentedToggle(
            viewMode = uiState.viewMode,
            onModeSelected = viewModel::onToggleViewMode,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Loading…")
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = uiState.errorMessage ?: "Error")
                }
            }

            uiState.viewMode == ViewMode.LIST -> {
                TrackingList(
                    items = visibleItems,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }

            else -> {
                val mapsKey = stringResource(id = R.string.google_maps_key)
                if (mapsKey.isBlank() || mapsKey.contains("YOUR_API_KEY_HERE", ignoreCase = true)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Google Maps API key is missing. Set google_maps_key in res/values/strings.xml")
                    }
                } else {
                    TrackingMap(
                        items = visibleItems,
                        selected = uiState.selectedOnMap,
                        onSelect = viewModel::onSelectOnMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                }
            }
        }
    }
}

private const val LIGHT_MAP_STYLE_JSON = """
[
  {"featureType":"poi","stylers":[{"visibility":"off"}]},
  {"featureType":"transit","stylers":[{"visibility":"off"}]},
  {"featureType":"road","elementType":"labels.icon","stylers":[{"visibility":"off"}]},
  {"stylers":[{"saturation":-60},{"lightness":20}]}
]
"""

@Composable
private fun SegmentedToggle(
    viewMode: ViewMode,
    onModeSelected: (ViewMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .height(42.dp)
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Color(0xFFD6E0F2), shape)
            .padding(3.dp),
    ) {
        SegmentButton(
            text = "List",
            selected = viewMode == ViewMode.LIST,
            onClick = { onModeSelected(ViewMode.LIST) },
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
        )
        SegmentButton(
            text = "Map",
            selected = viewMode == ViewMode.MAP,
            onClick = { onModeSelected(ViewMode.MAP) },
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
        )
    }
}

@Composable
private fun SegmentButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (selected) Color(0xFF0A3D91) else Color.Transparent
    val fg = if (selected) Color.White else Color(0xFF0A3D91)

    TextButton(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg),
        contentPadding = PaddingValues(vertical = 0.dp),
    ) {
        Text(
            text = text,
            color = fg,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun TrackingList(
    items: List<TrackingInfo>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .background(Color(0xFFF3F6FB)),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items) { item ->
            TrackingCard(item = item)
        }
    }
}

@Composable
private fun TrackingCard(
    item: TrackingInfo,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = "Driver photo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                LabelValue(label = "Plate No:", value = item.plateNo)
                LabelValue(label = "Driver Name:", value = item.driverName)
                LabelValue(label = "Location:", value = item.location)
                LabelValue(label = "Last updated:", value = formatRelativeTime(item.lastUpdated))
            }
        }
    }
}

@Composable
private fun LabelValue(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0A3D91),
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666),
            maxLines = 1,
        )
    }
}

@Composable
private fun TrackingMap(
    items: List<TrackingInfo>,
    selected: TrackingInfo?,
    onSelect: (TrackingInfo?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val mapUiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            mapToolbarEnabled = false,
        )

        val mapProperties = MapProperties(
            mapStyleOptions = MapStyleOptions(LIGHT_MAP_STYLE_JSON),
        )

        val first = items.firstOrNull()
        val start = if (first != null) LatLng(first.lat, first.lng) else LatLng(25.2048, 55.2708)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(start, 12f)
        }

        val listState = rememberLazyListState()
        var isProgrammaticScroll by remember { mutableStateOf(false) }

        LaunchedEffect(selected) {
            val selectedItem = selected ?: return@LaunchedEffect
            val target = LatLng(selectedItem.lat, selectedItem.lng)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(target, 13f),
            )

            val selectedIndex = items.indexOfFirst { it.plateNo == selectedItem.plateNo }
            if (selectedIndex >= 0) {
                isProgrammaticScroll = true
                try {
                    listState.animateScrollToItem(selectedIndex)
                } finally {
                    isProgrammaticScroll = false
                }
            }
        }

        LaunchedEffect(items) {
            snapshotFlow { listState.isScrollInProgress }
                .collect { isScrolling ->
                    if (isProgrammaticScroll) return@collect
                    if (!isScrolling) {
                        val idx = listState.firstVisibleItemIndex
                        val item = items.getOrNull(idx) ?: return@collect
                        if (selected?.plateNo != item.plateNo) {
                            onSelect(item)
                        }
                    }
                }
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            onMapClick = { },
        ) {
            items.forEach { item ->
                val isSelected = selected?.plateNo == item.plateNo
                Marker(
                    state = com.google.maps.android.compose.rememberMarkerState(
                        position = LatLng(item.lat, item.lng),
                    ),
                    icon = BitmapDescriptorFactory.defaultMarker(
                        if (isSelected) BitmapDescriptorFactory.HUE_BLUE else BitmapDescriptorFactory.HUE_AZURE,
                    ),
                    title = item.plateNo,
                    snippet = item.driverName,
                    onClick = {
                        onSelect(item)
                        true
                    },
                )
            }
        }

        if (items.isNotEmpty()) {
            LazyRow(
                state = listState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(items) { _, item ->
                    MapCarouselCard(
                        item = item,
                        selected = selected?.plateNo == item.plateNo,
                        onClick = { onSelect(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MapCarouselCard(
    item: TrackingInfo,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Color(0xFF0A3D91) else Color(0x00000000)
    Card(
        modifier = Modifier
            .size(width = 320.dp, height = 110.dp)
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = "Driver photo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                LabelValue(label = "Plate No:", value = item.plateNo)
                LabelValue(label = "Driver Name:", value = item.driverName)
                LabelValue(label = "Location:", value = item.location)
                LabelValue(label = "Last updated:", value = formatRelativeTime(item.lastUpdated))
            }
        }
    }
}
