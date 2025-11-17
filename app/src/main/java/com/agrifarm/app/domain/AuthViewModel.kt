package com.agrifarm.app.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrifarm.app.data.database.AuthUserEntity
import com.agrifarm.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: AuthUserEntity) : AuthState()
    data class OtpSent(val phone: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    
    fun register(phone: String, password: String, name: String, email: String?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.register(phone, password, name, email).fold(
                onSuccess = {
                    _authState.value = AuthState.OtpSent(phone)
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Registration failed")
                }
            )
        }
    }
    
    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.login(phone, password).fold(
                onSuccess = { user ->
                    _authState.value = AuthState.Success(user)
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Login failed")
                }
            )
        }
    }
    
    fun verifyOtp(phone: String, otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.verifyOtp(phone, otp).fold(
                onSuccess = {
                    _authState.value = AuthState.Success(
                        AuthUserEntity(phone = phone, password = "", name = "")
                    )
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "OTP verification failed")
                }
            )
        }
    }
    
    fun forgotPassword(phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.forgotPassword(phone).fold(
                onSuccess = {
                    _authState.value = AuthState.OtpSent(phone)
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Failed to send OTP")
                }
            )
        }
    }
    
    fun resetPassword(phone: String, otp: String, newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.resetPassword(phone, otp, newPassword).fold(
                onSuccess = {
                    _authState.value = AuthState.Success(
                        AuthUserEntity(phone = phone, password = "", name = "")
                    )
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Password reset failed")
                }
            )
        }
    }
    
    fun resendOtp(phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.sendOtp(phone).fold(
                onSuccess = {
                    _authState.value = AuthState.OtpSent(phone)
                },
                onFailure = {
                    _authState.value = AuthState.Error(it.message ?: "Failed to resend OTP")
                }
            )
        }
    }
}
