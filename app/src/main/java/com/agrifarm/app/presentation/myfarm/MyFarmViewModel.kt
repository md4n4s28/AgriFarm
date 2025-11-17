package com.agrifarm.app.presentation.myfarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrifarm.app.data.local.UserPreferences
import com.agrifarm.app.data.model.DeviceStatus
import com.agrifarm.app.data.model.SensorTrend
import com.agrifarm.app.data.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFarmViewModel @Inject constructor(
    private val sensorRepository: SensorRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _deviceStatus = MutableStateFlow(DeviceStatus())
    val deviceStatus: StateFlow<DeviceStatus> = _deviceStatus.asStateFlow()

    private val _trends = MutableStateFlow<List<SensorTrend>>(emptyList())
    val trends: StateFlow<List<SensorTrend>> = _trends.asStateFlow()

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        viewModelScope.launch {
            sensorRepository.getSensorDataStream().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _deviceStatus.value = DeviceStatus(
                            isOnline = true,
                            lastSeen = "Just now",
                            latestData = data
                        )
                    },
                    onFailure = { error ->
                        _deviceStatus.value = DeviceStatus(
                            isOnline = false,
                            lastSeen = "Offline",
                            latestData = _deviceStatus.value.latestData
                        )
                        android.util.Log.e("MyFarmViewModel", "Error: ${error.message}")
                    }
                )
            }
        }
    }

    fun loadData() {
        startMonitoring()
    }
    
    fun getEspIp(): String {
        return sensorRepository.getEspIp()
    }
    
    fun updateEspIp(ip: String) {
        sensorRepository.updateEspIp(ip)
    }
}
