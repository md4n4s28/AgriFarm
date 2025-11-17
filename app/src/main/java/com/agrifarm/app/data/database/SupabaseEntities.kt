package com.agrifarm.app.data.database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// User Entity
@Serializable
data class UserEntity(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    @SerialName("profile_url") val profileUrl: String = "",
    @SerialName("created_at") val createdAt: String? = null
)

// Sensor Data Entity
@Serializable
data class SensorDataEntity(
    val id: String? = null,
    @SerialName("device_id") val deviceId: String,
    @SerialName("user_id") val userId: String,
    val temperature: Float,
    val humidity: Float,
    @SerialName("soil_moisture") val soilMoisture: Int,
    @SerialName("soil_moisture_pct") val soilMoisturePct: Int,
    @SerialName("gas_level") val gasLevel: Int,
    @SerialName("rain_detected") val rainDetected: Boolean,
    @SerialName("rain_analog") val rainAnalog: Int,
    val timestamp: Long,
    @SerialName("created_at") val createdAt: String? = null
)

// Crop Session Entity
@Serializable
data class CropSessionEntity(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("crop_name") val cropName: String,
    @SerialName("crop_type") val cropType: String,
    val variety: String? = null,
    @SerialName("land_area") val landArea: Float,
    @SerialName("seeding_date") val seedingDate: Long,
    @SerialName("expected_harvest_date") val expectedHarvestDate: Long,
    @SerialName("current_stage") val currentStage: String = "SEEDING",
    @SerialName("health_status") val healthStatus: String = "Good",
    @SerialName("expected_yield") val expectedYield: Float = 0f,
    @SerialName("current_yield") val currentYield: Float = 0f,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)

// Schedule Entity
@Serializable
data class ScheduleEntity(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    val type: String,
    val title: String,
    val description: String? = null,
    @SerialName("scheduled_time") val scheduledTime: Long,
    @SerialName("is_completed") val isCompleted: Boolean = false,
    @SerialName("is_recurring") val isRecurring: Boolean = false,
    @SerialName("recurring_days") val recurringDays: List<Int> = emptyList(),
    @SerialName("completed_at") val completedAt: Long? = null,
    @SerialName("created_at") val createdAt: String? = null
)

// Pump Control Entity
@Serializable
data class PumpControlEntity(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("is_on") val isOn: Boolean = false,
    val mode: String = "MANUAL",
    @SerialName("scheduled_start_time") val scheduledStartTime: String? = null,
    @SerialName("scheduled_end_time") val scheduledEndTime: String? = null,
    val duration: Int = 0,
    @SerialName("water_used") val waterUsed: Float = 0f,
    @SerialName("last_run_time") val lastRunTime: Long? = null,
    @SerialName("last_updated") val lastUpdated: Long? = null
)

// Weather Alert Entity
@Serializable
data class WeatherAlertEntity(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    val type: String,
    val severity: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    @SerialName("action_required") val actionRequired: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

// Land Info Entity
@Serializable
data class LandInfoEntity(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("total_area") val totalArea: Float,
    @SerialName("used_area") val usedArea: Float = 0f,
    @SerialName("available_area") val availableArea: Float? = null,
    @SerialName("soil_type") val soilType: String? = null,
    @SerialName("ph_level") val phLevel: Float? = null,
    val location: String? = null,
    val latitude: Float? = null,
    val longitude: Float? = null,
    @SerialName("created_at") val createdAt: String? = null
)

// Resource Usage Entity
@Serializable
data class ResourceUsageEntity(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("session_id") val sessionId: String,
    @SerialName("resource_type") val resourceType: String,
    val used: Float = 0f,
    val planned: Float = 0f,
    val remaining: Float = 0f,
    val cost: Float = 0f,
    val unit: String,
    @SerialName("created_at") val createdAt: String? = null
)

// Crop Suggestion Entity
@Serializable
data class CropSuggestionEntity(
    val id: String? = null,
    @SerialName("crop_name") val cropName: String,
    @SerialName("soil_type") val soilType: String,
    @SerialName("suitability_score") val suitabilityScore: Float,
    @SerialName("expected_yield") val expectedYield: Float? = null,
    @SerialName("water_requirement") val waterRequirement: Float? = null,
    @SerialName("fertilizer_requirement") val fertilizerRequirement: Float? = null,
    @SerialName("seed_cost") val seedCost: Float? = null,
    @SerialName("total_investment") val totalInvestment: Float? = null,
    @SerialName("expected_revenue") val expectedRevenue: Float? = null,
    @SerialName("profit_margin") val profitMargin: Float? = null,
    @SerialName("growth_duration") val growthDuration: Int? = null,
    val difficulty: String? = null,
    @SerialName("market_demand") val marketDemand: String? = null
)

// Crop Comparison Entity
@Serializable
data class CropComparisonEntity(
    val id: String? = null,
    @SerialName("crop_name") val cropName: String,
    @SerialName("water_usage") val waterUsage: Float? = null,
    @SerialName("fertilizer_usage") val fertilizerUsage: Float? = null,
    @SerialName("seed_cost") val seedCost: Float? = null,
    @SerialName("labor_cost") val laborCost: Float? = null,
    @SerialName("total_cost") val totalCost: Float? = null,
    @SerialName("expected_revenue") val expectedRevenue: Float? = null,
    val profit: Float? = null,
    val roi: Float? = null
)
