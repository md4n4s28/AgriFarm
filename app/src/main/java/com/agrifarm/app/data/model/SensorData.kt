package com.agrifarm.app.data.model

data class SensorData(
    val deviceId: String = "",
    val ts: Long = 0L,
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val soilMoisture: Int = 0,
    val soilMoisturePct: Int = 0,
    val gasLevel: Int = 0,
    val rainDetected: Boolean = false,
    val rainAnalog: Int = 0,
    val dhtOk: Boolean = false,
    val soilOk: Boolean = false,
    val gasOk: Boolean = false,
    val rainOk: Boolean = false,
    val ledSoilHealth: Boolean = false,
    val ledDhtHealth: Boolean = false,
    val ledGasHealth: Boolean = false,
    val ledRainHealth: Boolean = false,
    val ledWarning: Boolean = false,
    val ledCritical: Boolean = false,
    val ledSoilDry: Boolean = false,
    val ledSystemOk: Boolean = false,
    
    // Multi-sensor support
    val dhtSensors: List<DHTReading> = emptyList(),
    val soilSensors: List<SoilReading> = emptyList(),
    val gasSensors: List<GasReading> = emptyList(),
    val rainSensors: List<RainReading> = emptyList()
)

data class DHTReading(
    val id: Int,
    val temperature: Float,
    val humidity: Float,
    val isOnline: Boolean
)

data class SoilReading(
    val id: Int,
    val moisture: Int,
    val moisturePct: Int,
    val isOnline: Boolean
)

data class GasReading(
    val id: Int,
    val level: Int,
    val isOnline: Boolean
)

data class RainReading(
    val id: Int,
    val detected: Boolean,
    val analog: Int,
    val isOnline: Boolean
)
