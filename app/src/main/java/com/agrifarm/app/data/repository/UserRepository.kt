package com.agrifarm.app.data.repository

import com.agrifarm.app.data.database.SupabaseDatabase
import com.agrifarm.app.data.database.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val database: SupabaseDatabase
) {
    suspend fun saveUser(user: UserEntity): Result<Unit> {
        return try {
            database.insertUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(userId: String, user: UserEntity): Result<Unit> {
        return try {
            database.updateUser(userId, user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUser(userId: String): Result<UserEntity> {
        return try {
            val user = database.getUser(userId)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
