package com.moraware.mango.utilities

class PermissionsHelper() {

    interface IPermissionsDelegate
    {
        fun onSuccess(result : Int)
        fun onFailure(result : Int)
    }

    fun getLocationPermission(mPermissionsDelegate: IPermissionsDelegate) {
        mPermissionsDelegate.onSuccess(0)
    }
}