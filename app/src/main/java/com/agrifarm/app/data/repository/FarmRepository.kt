package com.agrifarm.app.data.repository

import com.agrifarm.app.data.database.*
import com.agrifarm.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FarmRepository @Inject constructor(
    private val database: SupabaseDatabase
) {
    
    fun observeCropSessions(userId: String): Flow<List<CropSessionDetail>> {
        return database.observeCropSessions(userId).map { entities ->
            entities.map { entity -> entity.toDomain() }
        }
    }
    
    suspend fun getCropSessions(userId: String): Result<List<CropSessionDetail>> {
        return try {
            val entities = database.getCropSessions(userId)
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun observeSchedules(userId: String): Flow<List<Schedule>> {
        return database.observeSchedules(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getSchedules(userId: String): Result<List<Schedule>> {
        return try {
            val entities = database.getSchedules(userId)
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun observePumpControl(userId: String): Flow<PumpControl?> {
        return database.observePumpControl(userId).map { it?.toDomain() }
    }
    
    suspend fun getPumpControl(userId: String): Result<PumpControl?> {
        return try {
            val entity = database.getPumpControl(userId)
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updatePumpControl(userId: String, control: PumpControl): Result<Unit> {
        return try {
            database.updatePumpControl(userId, control.toEntity(userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun observeWeatherAlerts(userId: String): Flow<List<WeatherAlert>> {
        return database.observeWeatherAlerts(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getWeatherAlerts(userId: String): Result<List<WeatherAlert>> {
        return try {
            val entities = database.getWeatherAlerts(userId)
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getLandInfo(userId: String): Result<LandInfo?> {
        return try {
            val entity = database.getLandInfo(userId)
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateLandInfo(userId: String, info: LandInfo): Result<Unit> {
        return try {
            database.updateLandInfo(userId, info.toEntity(userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCropSuggestions(soilType: String): Result<List<CropSuggestion>> {
        return try {
            val entities = database.getCropSuggestions(soilType)
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getResourceUsage(userId: String, sessionId: String): Result<List<ResourceUsage>> {
        return try {
            val entities = database.getResourceUsage(userId, sessionId)
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCropComparisons(): Result<List<CropComparison>> {
        return try {
            val entities = database.getCropComparisons()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun insertCropSession(userId: String, session: CropSessionDetail): Result<Unit> {
        return try {
            database.insertCropSession(session.toEntity(userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun insertSchedule(userId: String, schedule: Schedule): Result<Unit> {
        return try {
            database.insertSchedule(schedule.toEntity(userId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
