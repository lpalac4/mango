package com.moraware.domain.client

import com.moraware.data.MangoRepository
import com.moraware.data.IDataRepository
import com.moraware.data.models.ApplicationServices
import com.moraware.domain.client.base.IUseCaseClient
import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.interactors.Failure
import com.moraware.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

open class MangoUseCaseClient : IUseCaseClient {

    private lateinit var mCoroutineContext: CoroutineContext
    private lateinit var mLogger: Logger

    init {
        DomainDependencyProvider.setDataRepository(MangoRepository())
    }

    override fun setLogger(logger: Logger) {
        mLogger = logger
        DomainDependencyProvider.setLogger(mLogger)
    }

    override fun observeOnThread(coroutineContext: CoroutineContext) {
        mCoroutineContext = coroutineContext
    }

    override fun getObservingThread(): CoroutineContext {
        return mCoroutineContext
    }

    override fun setRepository(repository: IDataRepository){
        DomainDependencyProvider.setDataRepository(repository)
    }

    @Synchronized override fun <T : DomainResponse, E: Failure> execute(onResult: (Either<E, T>) -> Unit, useCase: BaseUseCase<T, E>) {
        runBlocking(mCoroutineContext) {
            try {
                mLogger.log(Level.FINE, "Running use case ${useCase.javaClass.simpleName} on $mCoroutineContext")
                useCase.setCallback(onResult)
                useCase.setMainLooper(Dispatchers.Main)
                useCase.run()
            } catch (e: CancellationException) {
                mLogger.log(Level.FINE, "A Job was cancelled, ignoring...")
            }
        }
    }

    override fun addServices(inputStream: InputStream, debug: Boolean) {
        ApplicationServices.initialize(inputStream)
        DomainDependencyProvider.getDataRepository().initializeServices(ApplicationServices.getInstance(), debug)
    }
}