package com.moraware.data.models

import com.moraware.data.base.BaseRequest
import java.net.URI

open class RegisterUserRequest(val email: String = "",
                               val password: String = "",
                               val username: String = "",
                               val imageURI: URI?): BaseRequest(){

    open fun anonymous(): Boolean {
        return false
    }

    open fun facebook(): Boolean {
        return false
    }

    fun getToken(): String {
        return ""
    }

    open fun credentials(): Boolean {
        return true
    }
}