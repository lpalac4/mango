package com.moraware.mango.dependencyinjection

import com.moraware.mango.dependencyinjection.application.ApplicationScope
import com.moraware.mango.settings.SettingsManager
import dagger.Module
import dagger.Provides

@Module
class TestSettingsModule {

    @Provides
    @ApplicationScope
    fun provideSettingsManager() : SettingsManager {
        return MockSettingsManager()
    }
}