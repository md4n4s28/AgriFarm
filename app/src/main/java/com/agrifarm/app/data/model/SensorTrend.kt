package com.agrifarm.app.data.model

data class SensorTrend(
    val hourAgo: Int = 0,
    val avgTemperature: Float = 0f,
    val avgHumidity: Float = 0f,
    val avgSoilMoisturePct: Int = 0,
    val avgGasLevel: Int = 0
)

data class DeviceStatus(
    val isOnline: Boolean = false,
    val lastSeen: String = "",
    val latestData: SensorData = SensorData()
)
