package com.agrifarm.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrifarm.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState
    
    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            authRepository.login(phone, password).fold(
                onSuccess = { user ->
                    _uiState.value = LoginUiState.Success(
                        userId = user.id ?: "",
                        email = user.email ?: "",
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
}
