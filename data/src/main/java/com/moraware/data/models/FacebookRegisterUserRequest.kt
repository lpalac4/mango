package com.moraware.data.models

import java.net.URI

class FacebookRegisterUserRequest(email: String, password: String, username: String, photoURI: URI?):
        RegisterUserRequest(email, password, username, photoURI) {
    override fun facebook(): Boolean {
        return true
    }
}