package com.moraware.data.interactors

import com.moraware.data.base.BaseResponse
import com.moraware.data.base.WebServiceException

interface Channel<T> where T: BaseResponse {
    fun onUpdate(response: T)
    fun onFailure(exception: WebServiceException) {}
}