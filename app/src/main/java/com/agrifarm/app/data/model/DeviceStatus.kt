package com.agrifarm.app.data.model

data class DeviceStatus(
    val isOnline: Boolean = false,
    val lastSeen: String = "",
    val latestData: SensorData = SensorData()
)
