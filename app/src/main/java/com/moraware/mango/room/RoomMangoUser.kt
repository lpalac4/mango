package com.moraware.mango.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moraware.data.entities.UserEntity
import java.io.Serializable

@Entity
class RoomMangoUser : Serializable {
    @PrimaryKey(autoGenerate = true)
    var dbId: Int = 0

    @ColumnInfo(name = "uid")
    var uid: String = ""

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "username")
    var username: String = ""

    @ColumnInfo(name = "email")
    var email: String = ""

    @ColumnInfo(name = "thirdPartyToken")
    var thirdPartyToken: String = ""

    @ColumnInfo(name = "cookedMeals")
    var cookedMeals: List<String> = emptyList()

    @ColumnInfo(name = "orderedMeals")
    var orderedMeals: List<String> = emptyList()

    @ColumnInfo(name = "photoUrl")
    var photoUrl: String = ""

    @ColumnInfo(name = "isChef")
    var isChef: Boolean = false

    @ColumnInfo(name = "notificationTokens")
    var notificationTokens: MutableList<String> = mutableListOf()

    companion object {
        fun fromEntity(userEntity: UserEntity) : RoomMangoUser {
            var mangoUser = RoomMangoUser()
            mangoUser.uid = userEntity.id
            mangoUser.name = userEntity.name
            mangoUser.username = userEntity.username
            mangoUser.email = userEntity.email
            mangoUser.isChef = userEntity.chef

            mangoUser.thirdPartyToken = userEntity.thirdPartyToken
            mangoUser.cookedMeals = userEntity.cookedMeals
            mangoUser.orderedMeals = userEntity.orderedMeals
            mangoUser.photoUrl = userEntity.photoUrl
            mangoUser.notificationTokens = userEntity.notificationTokens
            return mangoUser
        }
    }
}