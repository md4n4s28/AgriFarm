package com.agrifarm.app.data.api

import com.agrifarm.app.data.model.SensorData
import retrofit2.http.GET
import retrofit2.http.Query

interface SensorApi {
    @GET("rest/v1/rpc/get_latest_sensor_data")
    suspend fun getLatestSensorData(
        @Query("p_device_id") deviceId: String = "ESP32_FARM_001"
    ): SensorData

    @GET("rest/v1/rpc/get_24h_sensor_trends")
    suspend fun get24HourTrends(
        @Query("p_device_id") deviceId: String = "ESP32_FARM_001"
    ): List<com.agrifarm.app.data.model.SensorTrend>

    @GET("rest/v1/rpc/is_device_online")
    suspend fun isDeviceOnline(
        @Query("p_device_id") deviceId: String = "ESP32_FARM_001"
    ): Boolean

    @retrofit2.http.POST("rest/v1/sensor_data")
    suspend fun insertSensorData(
        @retrofit2.http.Body data: SensorData
    )
}
