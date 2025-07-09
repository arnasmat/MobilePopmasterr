package com.example.mobilepopmasterr.ui.screens.unused

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mobilepopmasterr.ui.Rectangle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapWithRectangle(
    rectangle: Rectangle,
    firstMarkerState: MarkerState,
    secondMarkerState: MarkerState,
    modifier: Modifier = Modifier.Companion
) {
    val defaultLocation = LatLng(0.0, 0.0)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 1f)
    }

    val uiSettings = MapUiSettings(
        zoomControlsEnabled = true,
        scrollGesturesEnabled = true,
        zoomGesturesEnabled = true,
        tiltGesturesEnabled = false,
    )

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
    ) {
        Polygon(
            points = rectangle.getRectangle(),
            fillColor = Color(0x5500FF00),
            strokeColor = Color.Companion.Green,
            strokeWidth = 4f
        )
        Marker(
            state = firstMarkerState,
            draggable = true,
        )
        Marker(
            state = secondMarkerState,
            draggable = true,
        )
    }
    }