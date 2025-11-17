package com.agrifarm.app.data.repository

import com.agrifarm.app.data.api.SensorApi
import com.agrifarm.app.data.model.SensorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorRepository @Inject constructor(
    private val sensorApi: SensorApi
) {
    private val client = OkHttpClient()
    private var esp32Ip = "10.115.22.233" // Default IP
    
    private var lastSavedData: SensorData? = null
    
    fun getSensorDataStream(): Flow<Result<SensorData>> = flow {
        while (true) {
            try {
                val data = fetchFromESP32()
                lastSavedData = data // Cache the data
                emit(Result.success(data))
            } catch (e: Exception) {
                android.util.Log.e("SensorRepository", "Error fetching data", e)
                // Use last saved data if available, otherwise emit error
                if (lastSavedData != null) {
                    android.util.Log.d("SensorRepository", "Using last saved data")
                    emit(Result.success(lastSavedData!!))
                } else {
                    emit(Result.failure(Exception("Device offline or unreachable: ${e.message}")))
                }
            }
            kotlinx.coroutines.delay(3000)
        }
    }
    
    private suspend fun fetchFromESP32(): SensorData = withContext(Dispatchers.IO) {
        android.util.Log.d("SensorRepository", "Connecting to ESP32 at: http://$esp32Ip/sensor")
        val request = Request.Builder()
            .url("http://$esp32Ip/sensor")
            .build()
        
        val response = client.newCall(request).execute()
        android.util.Log.d("SensorRepository", "Response code: ${response.code}")
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code}")
        }
        
        val jsonString = response.body?.string() ?: "{}"
        android.util.Log.d("SensorRepository", "Received JSON: $jsonString")
        val json = JSONObject(jsonString)
        
        // Parse multi-sensor arrays
        val dhtSensors = mutableListOf<com.agrifarm.app.data.model.DHTReading>()
        val soilSensors = mutableListOf<com.agrifarm.app.data.model.SoilReading>()
        val gasSensors = mutableListOf<com.agrifarm.app.data.model.GasReading>()
        val rainSensors = mutableListOf<com.agrifarm.app.data.model.RainReading>()
        
        if (json.has("dht_sensors")) {
            val dhtArray = json.getJSONArray("dht_sensors")
            for (i in 0 until dhtArray.length()) {
                val sensor = dhtArray.getJSONObject(i)
                dhtSensors.add(
                    com.agrifarm.app.data.model.DHTReading(
                        id = sensor.optInt("id", i + 1),
                        temperature = sensor.optDouble("temperature", 0.0).toFloat(),
                        humidity = sensor.optDouble("humidity", 0.0).toFloat(),
                        isOnline = sensor.optBoolean("online", true)
                    )
                )
            }
        }
        
        if (json.has("soil_sensors")) {
            val soilArray = json.getJSONArray("soil_sensors")
            for (i in 0 until soilArray.length()) {
                val sensor = soilArray.getJSONObject(i)
                soilSensors.add(
                    com.agrifarm.app.data.model.SoilReading(
                        id = sensor.optInt("id", i + 1),
                        moisture = sensor.optInt("moisture", 0),
                        moisturePct = sensor.optInt("moisture_pct", 0),
                        isOnline = sensor.optBoolean("online", true)
                    )
                )
            }
        }
        
        if (json.has("gas_sensors")) {
            val gasArray = json.getJSONArray("gas_sensors")
            for (i in 0 until gasArray.length()) {
                val sensor = gasArray.getJSONObject(i)
                gasSensors.add(
                    com.agrifarm.app.data.model.GasReading(
                        id = sensor.optInt("id", i + 1),
                        level = sensor.optInt("level", 0),
                        isOnline = sensor.optBoolean("online", true)
                    )
                )
            }
        }
        
        if (json.has("rain_sensors")) {
            val rainArray = json.getJSONArray("rain_sensors")
            for (i in 0 until rainArray.length()) {
                val sensor = rainArray.getJSONObject(i)
                rainSensors.add(
                    com.agrifarm.app.data.model.RainReading(
                        id = sensor.optInt("id", i + 1),
                        detected = sensor.optBoolean("detected", false),
                        analog = sensor.optInt("analog", 0),
                        isOnline = sensor.optBoolean("online", true)
                    )
                )
            }
        }
        
        SensorData(
            temperature = json.optDouble("temperature", -999.0).toFloat(),
            humidity = json.optDouble("humidity", -999.0).toFloat(),
            soilMoisture = json.optInt("soil_moisture", -999),
            soilMoisturePct = json.optInt("soil_moisture_pct", -999),
            gasLevel = json.optInt("gas_level", -999),
            rainDetected = json.optBoolean("rain_detected", false),
            rainAnalog = json.optInt("rain_analog", -999),
            dhtOk = json.optBoolean("dht_ok", false),
            soilOk = json.optBoolean("soil_ok", false),
            gasOk = json.optBoolean("gas_ok", false),
            rainOk = json.optBoolean("rain_ok", false),
            ledSoilHealth = json.optBoolean("led_soil_health", false).also { 
                android.util.Log.d("SensorRepository", "Parsed ledSoilHealth: $it") 
            },
            ledDhtHealth = json.optBoolean("led_dht_health", false),
            ledGasHealth = json.optBoolean("led_gas_health", false),
            ledRainHealth = json.optBoolean("led_rain_health", false),
            ledWarning = json.optBoolean("led_warning", false),
            ledCritical = json.optBoolean("led_critical", false),
            ledSoilDry = json.optBoolean("led_soil_dry", false),
            ledSystemOk = json.optBoolean("led_system_ok", false).also { 
                android.util.Log.d("SensorRepository", "Parsed ledSystemOk: $it") 
            },
            dhtSensors = dhtSensors,
            soilSensors = soilSensors,
            gasSensors = gasSensors,
            rainSensors = rainSensors
        )
    }
    
    fun getEspIp(): String = esp32Ip
    
    fun updateEspIp(ip: String) {
        esp32Ip = ip
        android.util.Log.d("SensorRepository", "ESP32 IP updated to: $ip")
    }
    
    suspend fun getDeviceStatus(): Result<com.agrifarm.app.data.model.DeviceStatus> {
        return try {
            val data = fetchFromESP32()
            Result.success(
                com.agrifarm.app.data.model.DeviceStatus(
                    isOnline = true,
                    lastSeen = "Just now",
                    latestData = data
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun get24HourTrends(): Result<List<com.agrifarm.app.data.model.SensorTrend>> {
        return Result.success(emptyList())
    }
    
}
