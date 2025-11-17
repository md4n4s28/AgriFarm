package com.agrifarm.app.data.database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthUserEntity(
    val id: String? = null,
    val phone: String,
    val password: String,
    val name: String,
    val email: String? = null,
    @SerialName("is_verified") val isVerified: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class OtpEntity(
    val id: String? = null,
    val phone: String,
    val otp: String,
    @SerialName("expires_at") val expiresAt: Long,
    @SerialName("is_used") val isUsed: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)
