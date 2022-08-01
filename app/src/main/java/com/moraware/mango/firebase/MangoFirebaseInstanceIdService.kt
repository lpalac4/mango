package com.moraware.mango.firebase

import android.content.ContentValues
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.moraware.mango.MangoApplication

/**
 * Created by chocollo on 7/25/17.
 */
class MangoFirebaseInstanceIdService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(ContentValues.TAG, "Refreshed thirdPartyId: $refreshedToken")

        MangoApplication.addMessagingToken(token)
    }
}