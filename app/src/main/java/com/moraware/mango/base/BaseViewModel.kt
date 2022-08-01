package com.moraware.mango.base

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moraware.domain.client.base.IUseCaseClient
import com.moraware.mango.BR
import com.moraware.mango.MangoApplication
import com.moraware.mango.R
import com.moraware.mango.logger.MangoLogger
import com.moraware.mango.util.SingleLiveEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import javax.inject.Inject

abstract class BaseViewModel : ViewModel(), Observable {

    @Inject
    lateinit var mUseCaseClient: IUseCaseClient
    @Inject
    lateinit var mLogger: MangoLogger

    val loadingIndicator = R.raw.loading_indicator

    var mJob: Job

    init {
        MangoApplication.getInstance().getApplicationComponent().inject(this)
        mJob = Job()
    }

    override fun onCleared() {
        super.onCleared()
        try {
            if(mJob.isActive) mJob.cancel()
        } catch (e: CancellationException) {
            mLogger.log("A job was cancelled, ignoring for now..")
        }
    }

    @Transient private val mCallbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        mCallbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        mCallbacks.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange() {
        mCallbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR uid for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        mCallbacks.notifyCallbacks(this, fieldId, null)
    }

    private var mIsProcessing: Boolean = false

    @Bindable
    fun isProcessing() : Boolean {
        return mIsProcessing
    }

    fun setProcessing(value : Boolean) {
        if(mIsProcessing != value)
        {
            mIsProcessing = value
            notifyPropertyChanged(BR.processing)
        }
    }

    private var mHasError: Boolean = false
    private var mErrorMessage: SingleLiveEvent<Int> = SingleLiveEvent()

    @Bindable
    fun getHasError() : Boolean {
        return mHasError
    }

    fun setHasError(value : Boolean) {
        if(mHasError != value)
        {
            mHasError = value
            notifyPropertyChanged(BR.hasError)
        }
    }

    fun getErrorMessage() : MutableLiveData<Int> {
        return mErrorMessage
    }

    fun setErrorMessage(value : Int) {
        mErrorMessage.value = value
    }

    fun setErrorMessage(params: String, value: Int) {
        mErrorMessage.value = value
    }

    abstract fun loadData()
    open fun loadNextPage(layoutIdForPosition: Int) {

    }


}