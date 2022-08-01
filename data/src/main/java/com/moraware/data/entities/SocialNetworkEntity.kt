package com.moraware.data.entities

data class SocialNetworkEntity(val originator: String = "", val recipient: String = "", val photoUrl: String = "", val username: String = "") {

    companion object {
        fun fromMap(map: HashMap<String, String>): SocialNetworkEntity {
            return SocialNetworkEntity(map["originator"] ?: "", map["recipient"]
                    ?: "", map["photoUrl"] ?: "", map["username"] ?: "")
        }
    }
}
