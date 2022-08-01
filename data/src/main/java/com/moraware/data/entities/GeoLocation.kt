package com.moraware.data.entities

data class GeoLocation(val latitude: Double = 0.0, val longitude: Double = 0.0) {
    companion object {
        val NONE = GeoLocation(0.0,0.0)
    }
}