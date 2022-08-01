package com.moraware.mango.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class MangoLocationManager(val context: Context) {

    private val PERMISSIONS_LOCATION_CODE: Int = 1114

    private var mLocation: Location? = null
    private lateinit var mLocationListener: ILocationListener
    private var addressString : String = ""

    interface ILocationListener {
        fun onNewLocation(location: Location, addressString: String)
    }

    fun setListener(listener: ILocationListener) {
        mLocationListener = listener
    }

    @SuppressLint("MissingPermission")
    fun queryLastLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if(mLocation == null) mLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        runBlocking { computeAddress() }
        mLocation?.let { mLocationListener.onNewLocation(it, addressString) }
    }

    fun getLastLocation() : Location? {
        return mLocation
    }

    fun verifyLocationPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_LOCATION_CODE)
        } else {
            queryLastLocation()
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_LOCATION_CODE && grantResults.size == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            queryLastLocation()
        }
    }

    suspend fun computeAddress() = withContext(Dispatchers.IO) {
        mLocation?.let {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: MutableList<Address> = try {
                geocoder.getFromLocation(it.latitude, it.longitude, 1)
            } catch (e: IOException) {
                mutableListOf()
            }

            addressString = if(addresses.isNotEmpty()) addresses[0].locality else ""
        }
    }

    suspend fun computeAddress(latitude: Double, longitude: Double) : Address = withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: MutableList<Address> = try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: Exception) {
            mutableListOf()
        }

        if(addresses.isNotEmpty()) addresses[0] else Address(Locale.getDefault())
    }
}