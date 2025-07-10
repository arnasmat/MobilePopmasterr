package com.example.mobilepopmasterr.ui

import com.google.android.gms.maps.model.LatLng

data class Rectangle(val pos1: LatLng, val pos2: LatLng) {
    fun getRectangle(): List<LatLng> {
        val north = maxOf(pos1.latitude, pos2.latitude)
        val south = minOf(pos1.latitude, pos2.latitude)
        val east = maxOf(pos1.longitude, pos2.longitude)
        val west = minOf(pos1.longitude, pos2.longitude)

        return listOf(
            LatLng(north, west),
            LatLng(north, east),
            LatLng(south, east),
            LatLng(south, west),
            LatLng(north, west)
        )
    }
}