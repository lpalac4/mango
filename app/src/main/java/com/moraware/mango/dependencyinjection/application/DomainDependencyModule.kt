package com.moraware.mango.dependencyinjection.application

import com.moraware.domain.client.MangoUseCaseClient
import com.moraware.domain.client.base.IUseCaseClient
import com.moraware.mango.MangoApplication
import com.moraware.mango.firebase.FirebaseRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

/**
 * This module will hold domain project dependencies injected throughout the app
 */
@Module
open class DomainDependencyModule(val application: MangoApplication = MangoApplication()) {

    @Provides
    @ApplicationScope
    open fun providesUseCaseClient(application: MangoApplication) : IUseCaseClient {
        val client = MangoUseCaseClient()
        client.setRepository(FirebaseRepository(application.mLogger, application.mLocalDatabase, Dispatchers.IO))
        client.observeOnThread(Dispatchers.IO)
        client.setLogger(application.mLogger)
//        client.addServices(application.resources.assets.open("applicationsettings.json"), BuildConfig.DEBUG)
        return client
    }
}