package com.agrifarm.app.presentation.auth

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrifarm.app.data.auth.GoogleAuthHelper
import com.agrifarm.app.data.model.User
import com.agrifarm.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleAuthHelper: GoogleAuthHelper,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState
    
    fun getSignInIntent(context: Context): Intent {
        return googleAuthHelper.getSignInIntent(context)
    }
    
    fun handleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            googleAuthHelper.handleSignInResult(data).fold(
                onSuccess = { account ->
                    val user = User(
                        id = account.id ?: "",
                        email = account.email ?: "",
                        name = account.displayName ?: "User"
                    )
                    userRepository.saveUserToSupabase(user)
                    _uiState.value = LoginUiState.Success(
                        userId = user.id,
                        email = user.email,
                        name = user.name
                    )
                },
                onFailure = { error ->
                    _uiState.value = LoginUiState.Error(
                        error.message ?: "Login failed"
                    )
                }
            )
        }
    }
    
    fun loginAsGuest(name: String, email: String) {
        viewModelScope.launch {
            val user = User(
                id = "guest_${System.currentTimeMillis()}",
                email = email.ifBlank { "guest@agrifarm.com" },
                name = name
            )
            userRepository.saveUserToSupabase(user)
        }
    }
}
