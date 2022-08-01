package com.moraware.domain.client.base

import com.moraware.data.IDataRepository
import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.interactors.Failure
import com.moraware.domain.usecase.base.BaseUseCase
import java.io.InputStream
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

/**
 * The IUseCaseClient will only be responsible of executing usecases and calling back with results
 * into the proper thread.
 */
interface IUseCaseClient {

    fun getObservingThread(): CoroutineContext
    fun observeOnThread(coroutineContext: CoroutineContext)

    fun <T : DomainResponse, E : Failure> execute(onResult: (Either<E, T>) -> Unit, useCase: BaseUseCase<T, E>)
    fun addServices(inputStream: InputStream, debug: Boolean)
    fun setRepository(repository: IDataRepository)
    fun setLogger(logger: Logger)
}