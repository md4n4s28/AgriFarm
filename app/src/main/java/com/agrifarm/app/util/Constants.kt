package com.agrifarm.app.util

object Constants {
    
    // Supabase - Will be loaded from BuildConfig after Gradle sync
    const val SUPABASE_URL = "" // TODO: Will be auto-filled from secrets.properties
    const val SUPABASE_KEY = "" // TODO: Will be auto-filled from secrets.properties
    
    // AI Model Endpoints (will be added from .env)
    const val AI_CROP_DETECTION_URL = "" // TODO: Add from .env
    const val AI_PRICE_PREDICTION_URL = "" // TODO: Add from .env
    const val AI_DISEASE_DETECTION_URL = "" // TODO: Add from .env
    
    // Weather API
    const val WEATHER_API_URL = "https://api.openweathermap.org/data/2.5"
    const val WEATHER_API_KEY = "" // TODO: Add from .env
    
    // Market APIs
    const val AGMARKNET_API_URL = "https://api.data.gov.in/resource/9ef84268-d588-465a-a308-a864a43d0070"
    
    // IoT/MQTT
    const val MQTT_BROKER_URL = "broker.hivemq.com"
    const val MQTT_PORT = 1883
    
    // App Constants
    const val APP_NAME = "AgriFarm"
    const val DEFAULT_LANGUAGE = "en"
    
    // Sensor Thresholds (from your ESP32 code)
    const val SOIL_DRY_THRESHOLD = 3500
    const val SOIL_WARNING_THRESHOLD = 3000
    const val GAS_CRITICAL_THRESHOLD = 3600
    const val GAS_WARNING_THRESHOLD = 3000
    
    // Database
    const val SENSOR_DATA_SYNC_INTERVAL = 20000L // 20 seconds (matches ESP32)
    
    // Storage Buckets
    const val DISEASE_IMAGES_BUCKET = "disease-images"
    const val PROFILE_IMAGES_BUCKET = "profile-images"
}
