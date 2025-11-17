package com.agrifarm.app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    fun saveUser(id: String, email: String, name: String) {
        prefs.edit().apply {
            putString("user_id", id)
            putString("user_email", email)
            putString("user_name", name)
            apply()
        }
    }
    
    fun getUserId(): String = prefs.getString("user_id", "") ?: ""
    fun getUserEmail(): String = prefs.getString("user_email", "") ?: ""
    fun getUserName(): String = prefs.getString("user_name", "") ?: ""
    
    fun isLoggedIn(): Boolean = getUserId().isNotEmpty()
    
    fun clearUser() {
        prefs.edit().clear().apply()
    }
}
