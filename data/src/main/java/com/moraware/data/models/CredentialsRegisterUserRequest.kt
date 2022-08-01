package com.moraware.data.models

import java.net.URI

class CredentialsRegisterUserRequest(userName: String, email: String, password: String,
                                     val firstName: String?, val lastName: String?, val address: String?,
                                     val city: String?, val state: String?, val zipcode: String?, val phone: String?, imageURI: URI?)
    : RegisterUserRequest(email, password, userName, imageURI) {
    override fun credentials(): Boolean {
        return true
    }

    fun isChef(): Boolean {
        return true
    }
}