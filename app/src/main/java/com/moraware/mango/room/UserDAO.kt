package com.moraware.mango.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM RoomMangoUser WHERE dbId IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<RoomMangoUser>

    @Query("SELECT * FROM RoomMangoUser WHERE email LIKE :username LIMIT 1")
    suspend fun findByName(username: String): RoomMangoUser

    @Insert
    suspend fun insertAll(vararg users: RoomMangoUser)

    @Delete
    suspend fun delete(user: RoomMangoUser)

    @Query("SELECT * FROM RoomMangoUser LIMIT 1")
    suspend fun getLoggedInUser(): RoomMangoUser?
}