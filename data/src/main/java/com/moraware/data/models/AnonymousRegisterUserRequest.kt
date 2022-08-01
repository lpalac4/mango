package com.moraware.data.models

class AnonymousRegisterUserRequest():
        RegisterUserRequest("","","", null) {

    override fun anonymous(): Boolean {
        return true
    }
}