package com.moraware.mango.base

import com.moraware.mango.util.MangoLocationManager

abstract class BaseLocationActivity<T: BaseViewModel>: ViewModelActivity<T>() {

    val locationManager = MangoLocationManager(this)

    override fun onResume() {
        super.onResume()
        locationManager.verifyLocationPermission(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}