package com.agrifarm.app.data.api

import com.agrifarm.app.data.model.SensorData
import retrofit2.http.GET

interface SensorApiService {
    @GET(".")
    suspend fun getSensorData(): SensorDataResponse
}

data class SensorDataResponse(
    val device: String = "AgriFarm-Device",
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val soilMoisture: Int = 0,
    val rainDetected: Boolean = false,
    val gasLevel: Int = 0
)

fun SensorDataResponse.toSensorData() = SensorData(
    temperature = temperature,
    humidity = humidity,
    soilMoisture = soilMoisture,
    rainDetected = rainDetected,
    gasLevel = gasLevel
)
