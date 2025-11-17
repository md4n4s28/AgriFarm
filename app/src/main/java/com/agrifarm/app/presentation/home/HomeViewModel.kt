package com.agrifarm.app.presentation.home

import androidx.lifecycle.ViewModel
import com.agrifarm.app.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName
    
    init {
        _userName.value = userPreferences.getUserName()
    }
}
