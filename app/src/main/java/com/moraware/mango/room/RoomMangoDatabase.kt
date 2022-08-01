package com.moraware.mango.room

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.runBlocking


@Database(entities = [RoomMangoUser::class, RoomMangoMeal::class], version = 1)
@TypeConverters(Converters::class)
abstract class RoomMangoDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_DB = "mangodatabase.db"
        private var appDatabase: RoomMangoDatabase? = null

        fun getInstance(context: Context): RoomMangoDatabase {
            if (appDatabase == null) {
                appDatabase = Room.databaseBuilder(context, RoomMangoDatabase::class.java, DATABASE_DB).allowMainThreadQueries().build()
            }

            return appDatabase!!
        }
    }

    abstract fun userDao(): UserDao

    abstract fun mealDao(): MealDao

    fun addUserBlocking(mangoUser: RoomMangoUser) {
        runBlocking {
            userDao().insertAll(mangoUser)
        }
    }

    fun addMessagingToken(token: String) {
        runBlocking {
            val user = userDao().getLoggedInUser()
            if(user?.notificationTokens?.contains(token) == false) {
                userDao().delete(user)

                user.notificationTokens.add(token)
                userDao().insertAll(user)
            }
        }
    }

    fun updateUser(mangoUser: RoomMangoUser) {
        runBlocking {
            userDao().delete(mangoUser)
            userDao().insertAll(mangoUser)
        }
    }

    fun deleteUserBlocking(mangoUser: RoomMangoUser) {
        runBlocking {
            userDao().delete(mangoUser)
        }
    }

    fun getLoggedInUser(): RoomMangoUser? {
        return runBlocking {
            userDao().getLoggedInUser()
        }
    }

    fun deleteAllUserDataBlocking() {
        runBlocking {
            clearAllTables()
        }
    }

    fun addMealBlocking(meal: RoomMangoMeal) {
        runBlocking {
            mealDao().insertAll(meal)
        }
    }
}