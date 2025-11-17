package com.agrifarm.app.data.repository

import com.agrifarm.app.data.database.AuthUserEntity
import com.agrifarm.app.data.database.OtpEntity
import com.agrifarm.app.data.database.SupabaseDatabase
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AuthRepository @Inject constructor(
    private val database: SupabaseDatabase
) {
    suspend fun register(phone: String, password: String, name: String, email: String?): Result<String> {
        return try {
            val existing = database.getUserByPhone(phone)
            if (existing != null) {
                return Result.failure(Exception("Phone already registered"))
            }
            
            val userId = "user_${System.currentTimeMillis()}"
            val hashedPassword = hashPassword(password)
            
            database.registerUser(
                AuthUserEntity(
                    id = userId,
                    phone = phone,
                    password = hashedPassword,
                    name = name,
                    email = email,
                    isVerified = false
                )
            )
            
            sendOtp(phone)
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(phone: String, password: String): Result<AuthUserEntity> {
        return try {
            val user = database.getUserByPhone(phone)
                ?: return Result.failure(Exception("User not found"))
            
            if (!user.isVerified) {
                return Result.failure(Exception("Phone not verified"))
            }
            
            val hashedPassword = hashPassword(password)
            if (user.password != hashedPassword) {
                return Result.failure(Exception("Invalid password"))
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendOtp(phone: String): Result<String> {
        return try {
            val otp = generateOtp()
            val expiresAt = System.currentTimeMillis() + (5 * 60 * 1000) // 5 minutes
            
            database.insertOtp(
                OtpEntity(
                    phone = phone,
                    otp = otp,
                    expiresAt = expiresAt
                )
            )
            
            // TODO: Integrate SMS API (Twilio, MSG91, etc.)
            android.util.Log.d("AuthRepository", "OTP for $phone: $otp")
            
            Result.success(otp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verifyOtp(phone: String, otp: String): Result<Unit> {
        return try {
            val otpEntity = database.getValidOtp(phone, otp)
                ?: return Result.failure(Exception("Invalid or expired OTP"))
            
            database.markOtpUsed(otpEntity.id!!)
            database.verifyUser(phone)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun forgotPassword(phone: String): Result<Unit> {
        return try {
            val user = database.getUserByPhone(phone)
                ?: return Result.failure(Exception("User not found"))
            
            sendOtp(phone)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resetPassword(phone: String, otp: String, newPassword: String): Result<Unit> {
        return try {
            val otpEntity = database.getValidOtp(phone, otp)
                ?: return Result.failure(Exception("Invalid or expired OTP"))
            
            val hashedPassword = hashPassword(newPassword)
            database.updatePassword(phone, hashedPassword)
            database.markOtpUsed(otpEntity.id!!)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    private fun generateOtp(): String {
        return Random.nextInt(100000, 999999).toString()
    }
}
