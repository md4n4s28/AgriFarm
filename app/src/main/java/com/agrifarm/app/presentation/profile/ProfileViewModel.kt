package com.agrifarm.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrifarm.app.data.database.UserEntity
import com.agrifarm.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user
    
    private val userId = "default_user"
    
    init {
        loadUser()
    }
    
    private fun loadUser() {
        viewModelScope.launch {
            userRepository.getUser(userId).onSuccess { user ->
                _user.value = user
            }
        }
    }
    
    fun logout() {
        _user.value = null
    }
}
