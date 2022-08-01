package com.moraware.mango.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MealDao {

    @Insert
    suspend fun insertAll(vararg meals: RoomMangoMeal)

    @Delete
    suspend fun delete(meal: RoomMangoMeal)

    @Query("SELECT * FROM RoomMangoMeal WHERE dbId IN (:mealIds)")
    suspend fun loadAllByIds(mealIds: IntArray): List<RoomMangoMeal>

    @Query("SELECT * FROM RoomMangoMeal")
    suspend fun getLoggedInUser(): RoomMangoMeal?
}