package com.moraware.mango.dependencyinjection

import com.moraware.mango.MangoApplication
import com.moraware.mango.logger.MangoLogger
import dagger.Module
import dagger.Provides

@Module
class TestModule {

    @Provides
    fun provideApplication(): MangoApplication {
        return MockApplication()
    }

    @Provides
    fun provideLogger(): MangoLogger {
        return MockLogger()
    }
}