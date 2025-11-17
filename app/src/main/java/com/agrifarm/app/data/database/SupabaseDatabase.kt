package com.agrifarm.app.data.database

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseDatabase @Inject constructor(
    private val supabase: SupabaseClient
) {
    // Users
    suspend fun insertUser(user: UserEntity) = supabase.from("users").insert(user)
    suspend fun updateUser(userId: String, user: UserEntity) = supabase.from("users").update(user) { filter { eq("id", userId) } }
    suspend fun getUser(userId: String) = supabase.from("users").select { filter { eq("id", userId) } }.decodeSingle<UserEntity>()
    
    // Sensor Data
    suspend fun insertSensorData(data: SensorDataEntity) = supabase.from("sensor_data").insert(data)
    suspend fun getSensorData(userId: String, limit: Int = 100) = supabase.from("sensor_data")
        .select { filter { eq("user_id", userId) } }
        .decodeList<SensorDataEntity>()
        .take(limit)
    
    fun observeSensorData(userId: String): Flow<SensorDataEntity> {
        return supabase.channel("sensor_data_$userId")
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "sensor_data"
                filter = "user_id=eq.$userId"
            }
            .map { getSensorData(userId, 1).first() }
    }
    
    // Crop Sessions
    suspend fun insertCropSession(session: CropSessionEntity) = supabase.from("crop_sessions").insert(session)
    suspend fun updateCropSession(sessionId: String, session: CropSessionEntity) = supabase.from("crop_sessions").update(session) { filter { eq("id", sessionId) } }
    suspend fun getCropSessions(userId: String) = supabase.from("crop_sessions")
        .select { filter { eq("user_id", userId); eq("is_active", true) } }
        .decodeList<CropSessionEntity>()
    
    fun observeCropSessions(userId: String): Flow<List<CropSessionEntity>> {
        return supabase.channel("crop_sessions_$userId")
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "crop_sessions"
                filter = "user_id=eq.$userId"
            }
            .map { getCropSessions(userId) }
    }
    
    // Schedules
    suspend fun insertSchedule(schedule: ScheduleEntity) = supabase.from("schedules").insert(schedule)
    suspend fun updateSchedule(scheduleId: String, schedule: ScheduleEntity) = supabase.from("schedules").update(schedule) { filter { eq("id", scheduleId) } }
    suspend fun getSchedules(userId: String) = supabase.from("schedules")
        .select { filter { eq("user_id", userId) } }
        .decodeList<ScheduleEntity>()
    
    fun observeSchedules(userId: String): Flow<List<ScheduleEntity>> {
        return supabase.channel("schedules_$userId")
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "schedules"
                filter = "user_id=eq.$userId"
            }
            .map { getSchedules(userId) }
    }
    
    // Pump Control
    suspend fun getPumpControl(userId: String) = supabase.from("pump_control")
        .select { filter { eq("user_id", userId) } }
        .decodeSingleOrNull<PumpControlEntity>()
    
    suspend fun updatePumpControl(userId: String, control: PumpControlEntity) = supabase.from("pump_control")
        .upsert(control.copy(userId = userId, lastUpdated = System.currentTimeMillis()))
    
    fun observePumpControl(userId: String): Flow<PumpControlEntity?> {
        return supabase.channel("pump_control_$userId")
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "pump_control"
                filter = "user_id=eq.$userId"
            }
            .map { getPumpControl(userId) }
    }
    
    // Weather Alerts
    suspend fun insertWeatherAlert(alert: WeatherAlertEntity) = supabase.from("weather_alerts").insert(alert)
    suspend fun getWeatherAlerts(userId: String) = supabase.from("weather_alerts")
        .select { filter { eq("user_id", userId) } }
        .decodeList<WeatherAlertEntity>()
    
    fun observeWeatherAlerts(userId: String): Flow<List<WeatherAlertEntity>> {
        return supabase.channel("weather_alerts_$userId")
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "weather_alerts"
                filter = "user_id=eq.$userId"
            }
            .map { getWeatherAlerts(userId) }
    }
    
    // Land Info
    suspend fun getLandInfo(userId: String) = supabase.from("land_info")
        .select { filter { eq("user_id", userId) } }
        .decodeSingleOrNull<LandInfoEntity>()
    
    suspend fun updateLandInfo(userId: String, info: LandInfoEntity) = supabase.from("land_info")
        .upsert(info.copy(userId = userId))
    
    // Resource Usage
    suspend fun insertResourceUsage(usage: ResourceUsageEntity) = supabase.from("resource_usage").insert(usage)
    suspend fun getResourceUsage(userId: String, sessionId: String) = supabase.from("resource_usage")
        .select { filter { eq("user_id", userId); eq("session_id", sessionId) } }
        .decodeList<ResourceUsageEntity>()
    
    // Crop Suggestions
    suspend fun getCropSuggestions(soilType: String) = supabase.from("crop_suggestions")
        .select { filter { eq("soil_type", soilType) } }
        .decodeList<CropSuggestionEntity>()
    
    // Crop Comparisons
    suspend fun getCropComparisons() = supabase.from("crop_comparisons")
        .select()
        .decodeList<CropComparisonEntity>()
    
    // Auth
    suspend fun registerUser(user: AuthUserEntity) = supabase.from("auth_users").insert(user)
    
    suspend fun getUserByPhone(phone: String) = supabase.from("auth_users")
        .select { filter { eq("phone", phone) } }
        .decodeSingleOrNull<AuthUserEntity>()
    
    suspend fun updatePassword(phone: String, newPassword: String) = supabase.from("auth_users")
        .update(mapOf("password" to newPassword)) { filter { eq("phone", phone) } }
    
    suspend fun verifyUser(phone: String) = supabase.from("auth_users")
        .update(mapOf("is_verified" to true)) { filter { eq("phone", phone) } }
    
    // OTP
    suspend fun insertOtp(otp: OtpEntity) = supabase.from("otp_codes").insert(otp)
    
    suspend fun getValidOtp(phone: String, otp: String) = supabase.from("otp_codes")
        .select { 
            filter { 
                eq("phone", phone)
                eq("otp", otp)
                eq("is_used", false)
                gte("expires_at", System.currentTimeMillis())
            } 
        }
        .decodeSingleOrNull<OtpEntity>()
    
    suspend fun markOtpUsed(id: String) = supabase.from("otp_codes")
        .update(mapOf("is_used" to true)) { filter { eq("id", id) } }
}
