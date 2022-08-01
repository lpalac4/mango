package com.moraware.domain.models

import com.moraware.domain.interactors.DomainResponse

/**
 * Created by chocollo on 7/24/17.
 */

open class User(val name: String,
                val email: String,
                val photoUrl: String,
                val id: String,
                val username: String = "",
                val chef: Boolean = false,
                val rating: Int = 0,
                val address: String = "",
                val city: String = "",
                val state: String = "",
                val zipCode: String = "",
                var thirdPartyToken: String = "",
                val followers: Int = 0,
                val following: Boolean? = null,
                val cookedMeals: List<String> = emptyList(),
                val orderedMeals: List<String> = emptyList(),
                val notificationToken: List<String> = emptyList(),
                val bio: String = "") : DomainResponse() {


    // The user's ID, unique to the Firebase project. Do NOT use this mealEntities to
    // authenticate with your backend server, if you have one. Use
    // FirebaseUser.getThirdPartyId() instead.

}
