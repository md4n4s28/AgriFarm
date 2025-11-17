package com.agrifarm.app.data.repository

import com.agrifarm.app.data.local.UserPreferences
import com.agrifarm.app.data.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val userPreferences: UserPreferences
) {
    suspend fun saveUserToSupabase(user: User): Result<Unit> {
        return try {
            supabase.from("users").upsert(user)
            userPreferences.saveUser(user.id, user.email, user.name)
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to save user", e)
            Result.failure(e)
        }
    }
    
    fun getLocalUser(): User {
        return User(
            id = userPreferences.getUserId(),
            email = userPreferences.getUserEmail(),
            name = userPreferences.getUserName()
        )
    }
    
    fun clearUser() {
        userPreferences.clearUser()
    }
}
