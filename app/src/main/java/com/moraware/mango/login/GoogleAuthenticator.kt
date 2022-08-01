package com.moraware.mango.login

import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.moraware.domain.client.base.IUseCaseClient
import com.moraware.domain.usecase.login.Login
import com.moraware.domain.usecase.login.LoginFailure
import com.moraware.domain.usecase.login.ThirdPartyLoginUseCase

/**
 * Created by chocollo on 7/25/17.
 */

class GoogleAuthenticator(private val mActivity: FragmentActivity,
                          private val mConsumer: LoginViewModel) {

    private val mGoogleApiClient: GoogleApiClient
    private val mUseCaseClient: IUseCaseClient = mConsumer.mUseCaseClient

            internal var mConnectionListener: GoogleApiClient.OnConnectionFailedListener = GoogleApiClient.OnConnectionFailedListener { mConsumer.onAuthenticatorInitError() }

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(mActivity)
                .enableAutoManage(mActivity /* FragmentActivity */, mConnectionListener /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    fun signInWithGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleSignIn(data: Intent, location: Location, city: String, isChef: Boolean, zipcode: String) {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
        Log.d(TAG, "handleSignInResult:" + result.isSuccess)

        if (result.isSuccess && result.signInAccount != null) {
            val acct = result.signInAccount

            acct?.let {
                val useCase = ThirdPartyLoginUseCase(it.idToken ?: "",
                        it.givenName + it.familyName,
                        it.displayName ?: "",
                        it.email ?: "",
                        isChef,
                        location.latitude,
                        location.longitude,
                        city,
                        zipcode,
                        it.photoUrl.toString())

                mUseCaseClient.execute({ it.either(::onSignInFailure, ::onSignIn) }, useCase)

            }

        } else {
            mConsumer.onAuthenticatorInitError()
        }
    }

    fun onSignIn(response: Login) {
        mConsumer.onAuthenticationSuccessfullyLinked()
    }

    fun onSignInFailure(error: LoginFailure) {
        mConsumer.onAuthenticatorInitError()
    }

    companion object {

        val TAG = GoogleAuthenticator::class.java.simpleName
        val RC_SIGN_IN = 2017
    }
}
