package com.moraware.mango.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moraware.data.entities.MealEntity
import java.io.Serializable

@Entity
class RoomMangoMeal: Serializable {
    @PrimaryKey(autoGenerate = true)
    var dbId: Int = 0

    companion object {
        fun fromEntity(userEntity: MealEntity) : RoomMangoMeal {
            var mangoMeal = RoomMangoMeal()
            return mangoMeal
        }
    }
}