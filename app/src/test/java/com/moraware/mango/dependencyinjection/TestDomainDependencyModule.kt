package com.moraware.mango.dependencyinjection

import com.moraware.domain.client.base.IUseCaseClient
import dagger.Module
import dagger.Provides

@Module
class TestDomainDependencyModule {

    @Provides
    fun providesUseCaseClient() : IUseCaseClient {
        return MockUseCaseClient()
    }
}