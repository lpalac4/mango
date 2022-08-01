package com.moraware.mango.dependencyinjection.application

import android.content.Context
import com.moraware.mango.security.KeyStoreManager
import com.moraware.mango.MangoApplication
import com.moraware.mango.logger.MangoLogger
import com.moraware.mango.room.RoomMangoDatabase
import com.moraware.mango.util.MangoSharedPrefs
import dagger.Module
import dagger.Provides


/**
 * In this module we define the dependencies that are injected at a global scope
 */
@Module
open class ApplicationModule(val application: MangoApplication = MangoApplication()) {

    @Provides
    fun providesContext(): Context {
        return application
    }

    @Provides
    @ApplicationScope
    open fun provideApplication() : MangoApplication {
        return application
    }

    @Provides
    @ApplicationScope
    open fun provideLogger() : MangoLogger {
        return MangoLogger("MangoLogger", null)
    }

    @Provides
    @ApplicationScope
    open fun provideDatabase() : RoomMangoDatabase {
        return RoomMangoDatabase.getInstance(application)
    }

    @Provides
    @ApplicationScope
    fun provideSettingsManager(keyStoreManager: KeyStoreManager, logger: MangoLogger) : MangoSharedPrefs {
        return MangoSharedPrefs(application, keyStoreManager, logger)
    }

    @Provides
    @ApplicationScope
    fun provideKeystoreManager(logger: MangoLogger) : KeyStoreManager {
        return KeyStoreManager(logger)
    }
}