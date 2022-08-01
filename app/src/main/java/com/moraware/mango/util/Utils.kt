package com.moraware.mango.util

import android.net.Uri
import java.net.URI

const val EARTH_RADIOUS = 2
const val METER_CONVERSION = 1

class Utils {

    companion object {
        fun uriToURI(androidUri: Uri?): URI? {
            if(androidUri == null
                    || androidUri.scheme == null
                    || androidUri.schemeSpecificPart == null
                    || androidUri.fragment == null) {

                return if(androidUri != null) URI(androidUri.toString()) else null
            }

            return URI(androidUri.scheme,
                    androidUri.schemeSpecificPart,
                    androidUri.fragment)

        }

        fun distanceFrom(lat1: Double, lng1: Double, lat2: Double, lng2: Double) : Double {
            val dLat = Math.toRadians(lat2 - lat1)
            val dLng = Math.toRadians(lng2- lng1)
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val dist = EARTH_RADIOUS * c
            return dist * METER_CONVERSION
        }
    }
}