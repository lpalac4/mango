package com.moraware.domain.usecase.base

import com.moraware.data.IDataRepository
import com.moraware.domain.client.DomainDependencyProvider
import com.moraware.domain.interactors.DomainResponse
import com.moraware.domain.interactors.Either
import com.moraware.domain.interactors.Failure
import kotlinx.coroutines.*
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

abstract class BaseUseCase<T, E> where T: DomainResponse, E: Failure {

    private var mRepository: IDataRepository = DomainDependencyProvider.getDataRepository()
    @Synchronized fun getRepository(): IDataRepository { return mRepository }
    protected var mLogger: Logger? = DomainDependencyProvider.getLogger()

    protected val id: UUID = UUID.randomUUID()

    abstract suspend fun run()

    private var mCallback: ((Either<E, T>) -> Unit)? = null
    fun setCallback(onResult: (Either<E, T>) -> Unit) {
        mCallback = onResult
    }

    private var mContext: CoroutineContext? = null
    @Synchronized
    fun getMainLooper(): CoroutineContext? {
        return mContext
    }
    fun setMainLooper(context: CoroutineContext) {
        mContext = context
    }

    fun postToMainThread(result: Either.Left<E>) {
        mLogger?.log(Level.FINE, "${this.javaClass.simpleName} Posting to main thread: failure.")
        mContext?.let {
            CoroutineScope(it).launch {
                mCallback?.invoke(result)
            }
        }
    }

    fun postToMainThread(result: Either.Right<T>) {
        mLogger?.log(Level.FINE, "${this.javaClass.simpleName} Posting to main thread: success.")
        mContext?.let {
            CoroutineScope(it).launch {
                mCallback?.invoke(result)
            }
        }
    }
}