package com.moraware.mango

import android.app.Application
import androidx.annotation.UiThread
import com.google.android.gms.maps.MapsInitializer
import com.google.android.libraries.places.api.Places
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.threetenabp.AndroidThreeTen
import com.moraware.mango.dependencyinjection.application.*
import com.moraware.mango.logger.MangoLogger
import com.moraware.mango.room.RoomMangoDatabase
import com.moraware.mango.room.RoomMangoUser
import com.moraware.mango.util.AndroidLoggingHandler
import dagger.Module
import java.util.logging.Level
import javax.inject.Inject

@Module
open class MangoApplication : Application() {

    private lateinit var mApplicationComponent: IApplicationComponent

    @Inject
    lateinit var mLogger: MangoLogger

    @Inject
    lateinit var mLocalDatabase: RoomMangoDatabase

    companion object {
        private lateinit var sInstance: MangoApplication
        fun getInstance(): MangoApplication {
            return sInstance
        }

        fun getUsersBlocking() : RoomMangoUser? {
            var user = getInstance().mLocalDatabase.getLoggedInUser()

            FirebaseInstanceId.getInstance().token?.let { token ->
                if (user?.notificationTokens?.contains(token) == false) {
                    user.notificationTokens.add(token)
                    getInstance().mLocalDatabase.updateUser(user)
                }
            }

            return user
        }

        fun addMessagingToken(token: String) {
            getInstance().mLocalDatabase.addMessagingToken(token)
        }
    }

    override fun onCreate() {
        // instantiate component that will be used to inject on global scope here
        mApplicationComponent = DaggerIApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .domainDependencyModule(DomainDependencyModule(this))
                .build()

        mApplicationComponent.inject(this)

        super.onCreate()

        initPlacesSDK()
        setupLogger()
        AndroidThreeTen.init(this)
        MapsInitializer.initialize(this)

        sInstance = this
    }

    private fun initPlacesSDK() {
        Places.initialize(applicationContext, "API-KEY")
        Places.createClient(this)
    }

    @UiThread
    fun getApplicationComponent(): IApplicationComponent {
        return mApplicationComponent
    }

    private fun setupLogger() {
        val handler = AndroidLoggingHandler()
        AndroidLoggingHandler.reset(handler)
        mLogger.addHandler(handler)
        mLogger.level = Level.FINEST
    }

}
