package com.agrifarm.app.presentation.myfarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrifarm.app.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFarmViewModel2 @Inject constructor() : ViewModel() {
    
    private val _cropSession = MutableStateFlow(createDummyCropSession())
    val cropSession: StateFlow<CropSessionDetail> = _cropSession
    
    private val _schedules = MutableStateFlow(createDummySchedules())
    val schedules: StateFlow<List<Schedule>> = _schedules
    
    private val _pumpControl = MutableStateFlow(PumpControl())
    val pumpControl: StateFlow<PumpControl> = _pumpControl
    
    private val _weatherAlerts = MutableStateFlow(createDummyWeatherAlerts())
    val weatherAlerts: StateFlow<List<WeatherAlert>> = _weatherAlerts
    
    private val _landInfo = MutableStateFlow(createDummyLandInfo())
    val landInfo: StateFlow<LandInfo> = _landInfo
    
    private val _cropSuggestions = MutableStateFlow(createDummyCropSuggestions())
    val cropSuggestions: StateFlow<List<CropSuggestion>> = _cropSuggestions
    
    private val _resourceUsage = MutableStateFlow(createDummyResourceUsage())
    val resourceUsage: StateFlow<List<ResourceUsage>> = _resourceUsage
    
    private val _cropComparisons = MutableStateFlow(createDummyCropComparisons())
    val cropComparisons: StateFlow<List<CropComparison>> = _cropComparisons
    
    fun togglePump() {
        viewModelScope.launch {
            _pumpControl.value = _pumpControl.value.copy(isOn = !_pumpControl.value.isOn)
        }
    }
    
    fun setPumpMode(mode: PumpMode) {
        viewModelScope.launch {
            _pumpControl.value = _pumpControl.value.copy(mode = mode)
        }
    }
    
    fun schedulePump(startTime: String, endTime: String) {
        viewModelScope.launch {
            _pumpControl.value = _pumpControl.value.copy(
                mode = PumpMode.SCHEDULED,
                scheduledStartTime = startTime,
                scheduledEndTime = endTime
            )
        }
    }
    
    fun completeSchedule(scheduleId: String) {
        viewModelScope.launch {
            _schedules.value = _schedules.value.map {
                if (it.id == scheduleId) it.copy(isCompleted = true) else it
            }
        }
    }
    
    private fun createDummyCropSession() = CropSessionDetail(
        id = "1",
        cropName = "Wheat",
        cropType = "Rabi",
        variety = "HD-2967",
        landArea = 5.5f,
        seedingDate = System.currentTimeMillis() - (45L * 24 * 60 * 60 * 1000),
        expectedHarvestDate = System.currentTimeMillis() + (75L * 24 * 60 * 60 * 1000),
        currentStage = CropStage.VEGETATIVE,
        daysElapsed = 45,
        daysRemaining = 75,
        healthStatus = "Excellent",
        expectedYield = 3500f,
        currentYield = 0f
    )
    
    private fun createDummySchedules() = listOf(
        Schedule("1", ScheduleType.WATERING, "Morning Irrigation", "Water for 2 hours", System.currentTimeMillis() + (2 * 60 * 60 * 1000), false, true, listOf(1,3,5)),
        Schedule("2", ScheduleType.FERTILIZER, "Apply Urea", "50kg urea application", System.currentTimeMillis() + (24 * 60 * 60 * 1000), false, false),
        Schedule("3", ScheduleType.INSPECTION, "Field Inspection", "Check for pests", System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000), false, false),
        Schedule("4", ScheduleType.PESTICIDE, "Spray Pesticide", "Organic pesticide spray", System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000), false, false)
    )
    
    private fun createDummyWeatherAlerts() = listOf(
        WeatherAlert("1", AlertType.HEAVY_RAIN, AlertSeverity.MEDIUM, "Monsoon Expected", "Heavy rainfall expected in 3 days. Prepare drainage.", System.currentTimeMillis(), "Check drainage systems"),
        WeatherAlert("2", AlertType.HEATWAVE, AlertSeverity.LOW, "Temperature Rise", "Temperature may reach 38°C tomorrow", System.currentTimeMillis(), "Increase watering frequency")
    )
    
    private fun createDummyLandInfo() = LandInfo(
        totalArea = 10f,
        usedArea = 5.5f,
        availableArea = 4.5f,
        soilType = "Loamy",
        phLevel = 6.8f,
        location = "Punjab, India",
        coordinates = Pair(30.7333, 76.7794)
    )
    
    private fun createDummyCropSuggestions() = listOf(
        CropSuggestion("Rice", 92f, 4500f, 15000f, 250f, 8000f, 45000f, 180000f, 135000f, 120, "Medium", "High"),
        CropSuggestion("Maize", 88f, 3800f, 8000f, 180f, 6000f, 35000f, 140000f, 105000f, 90, "Easy", "High"),
        CropSuggestion("Cotton", 85f, 2200f, 12000f, 300f, 10000f, 55000f, 200000f, 145000f, 150, "Hard", "Medium"),
        CropSuggestion("Sugarcane", 90f, 65000f, 25000f, 400f, 15000f, 80000f, 280000f, 200000f, 365, "Medium", "High")
    )
    
    private fun createDummyResourceUsage() = listOf(
        ResourceUsage(ResourceType.WATER, 125000f, 200000f, 75000f, 2500f, "Liters"),
        ResourceUsage(ResourceType.SEEDS, 45f, 50f, 5f, 8000f, "kg"),
        ResourceUsage(ResourceType.FERTILIZER, 180f, 250f, 70f, 12000f, "kg"),
        ResourceUsage(ResourceType.PESTICIDE, 15f, 20f, 5f, 3500f, "Liters"),
        ResourceUsage(ResourceType.LABOR, 120f, 200f, 80f, 24000f, "Hours"),
        ResourceUsage(ResourceType.ELECTRICITY, 450f, 600f, 150f, 4500f, "kWh")
    )
    
    private fun createDummyCropComparisons() = listOf(
        CropComparison("Wheat", 200000f, 250f, 8000f, 20000f, 45000f, 165000f, 120000f, 267f),
        CropComparison("Rice", 300000f, 350f, 10000f, 25000f, 60000f, 220000f, 160000f, 267f),
        CropComparison("Maize", 150000f, 180f, 6000f, 15000f, 35000f, 140000f, 105000f, 300f),
        CropComparison("Cotton", 250000f, 300f, 12000f, 30000f, 65000f, 240000f, 175000f, 269f)
    )
}
