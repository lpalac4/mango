package com.moraware.data.base

data class WebServiceException(val exception: Exception? = null,
                               val errorType: ErrorType? = null)
