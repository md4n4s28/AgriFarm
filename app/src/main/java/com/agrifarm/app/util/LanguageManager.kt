package com.agrifarm.app.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object LanguageManager {
    var currentLanguage by mutableStateOf("en")
        private set
    
    fun setLanguage(lang: String) {
        currentLanguage = lang
    }
    
    fun getString(key: String): String {
        return when (currentLanguage) {
            "hi" -> hindiStrings[key] ?: englishStrings[key] ?: key
            else -> englishStrings[key] ?: key
        }
    }
}

private val englishStrings = mapOf(
    // Home Screen
    "good_morning" to "Good Morning",
    "welcome" to "Welcome to AgriFarm",
    "search_placeholder" to "Search crops, prices, services...",
    "quick_actions" to "Quick Actions",
    "iot_monitor" to "IoT Monitor",
    "crop_advice" to "Crop Advice",
    "disease_scan" to "Disease Scan",
    "market_prices" to "Market Prices",
    "today_overview" to "Today's Overview",
    "temperature" to "Temperature",
    "humidity" to "Humidity",
    "soil_health" to "Soil Health",
    "rain" to "Rain",
    "ai_insights" to "AI Insights",
    "recent_updates" to "Recent Updates",
    
    // Marketplace
    "market_title" to "Market Prices",
    "market_subtitle" to "Live crop prices & trends",
    "today" to "Today",
    "week" to "Week",
    "month" to "Month",
    "average_price" to "Average Price",
    "total_volume" to "Total Volume",
    "crop_prices" to "Crop Prices",
    "market_insights" to "Market Insights",
    "best_time_sell" to "Best Time to Sell",
    "price_forecast" to "Price Forecast",
    
    // My Farm
    "my_farm" to "My Farm",
    "iot_monitoring" to "IoT sensor monitoring",
    "device_status" to "ESP32 Device",
    "online" to "Online",
    "offline" to "Offline",
    "sensor_readings" to "Sensor Readings",
    "soil_moisture" to "Soil Moisture",
    "gas_level" to "Gas Level",
    "battery" to "Battery",
    "trends_24h" to "24-Hour Trends",
    "alerts" to "Alerts",
    "good" to "Good",
    "normal" to "Normal",
    "optimal" to "Optimal",
    
    // Services
    "services" to "Services",
    "ai_tools" to "AI-powered farming tools",
    "available_services" to "Available Services",
    "crop_recommendation" to "Crop Recommendation",
    "disease_detection" to "Disease Detection",
    "satellite_monitoring" to "Satellite Monitoring",
    "weather_forecast" to "Weather Forecast",
    "market_intelligence" to "Market Intelligence",
    
    // Profile
    "profile" to "Profile",
    "edit_photo" to "Edit Photo",
    "personal_info" to "Personal Information",
    "full_name" to "Full Name",
    "email" to "Email",
    "contact_number" to "Contact Number",
    "farm_details" to "Farm Details",
    "land_area" to "Land Area",
    "crop_type" to "Crop Type",
    "state" to "State",
    "district" to "District",
    "address" to "Address",
    "settings" to "Settings",
    "notifications" to "Notifications",
    "language" to "Language",
    "help_support" to "Help & Support",
    "about" to "About AgriFarm",
    "logout" to "Logout",
    "enabled" to "Enabled",
    "save" to "Save",
    "cancel" to "Cancel",
    "select_state" to "Select State",
    "select_district" to "Select District",
    "select_crops" to "Select Crops",
    "select_language" to "Select Language",
    "coming_soon" to "Coming Soon",
    "back" to "Back"
)

private val hindiStrings = mapOf(
    // Home Screen
    "good_morning" to "सुप्रभात",
    "welcome" to "एग्रीफार्म में आपका स्वागत है",
    "search_placeholder" to "फसल, कीमत, सेवाएं खोजें...",
    "quick_actions" to "त्वरित कार्य",
    "iot_monitor" to "IoT मॉनिटर",
    "crop_advice" to "फसल सलाह",
    "disease_scan" to "रोग स्कैन",
    "market_prices" to "बाजार मूल्य",
    "today_overview" to "आज का अवलोकन",
    "temperature" to "तापमान",
    "humidity" to "नमी",
    "soil_health" to "मिट्टी स्वास्थ्य",
    "rain" to "बारिश",
    "ai_insights" to "AI जानकारी",
    "recent_updates" to "हाल के अपडेट",
    
    // Marketplace
    "market_title" to "बाजार मूल्य",
    "market_subtitle" to "लाइव फसल कीमतें और रुझान",
    "today" to "आज",
    "week" to "सप्ताह",
    "month" to "महीना",
    "average_price" to "औसत मूल्य",
    "total_volume" to "कुल मात्रा",
    "crop_prices" to "फसल मूल्य",
    "market_insights" to "बाजार जानकारी",
    "best_time_sell" to "बेचने का सर्वोत्तम समय",
    "price_forecast" to "मूल्य पूर्वानुमान",
    
    // My Farm
    "my_farm" to "मेरा खेत",
    "iot_monitoring" to "IoT सेंसर निगरानी",
    "device_status" to "ESP32 डिवाइस",
    "online" to "ऑनलाइन",
    "offline" to "ऑफलाइन",
    "sensor_readings" to "सेंसर रीडिंग",
    "soil_moisture" to "मिट्टी की नमी",
    "gas_level" to "गैस स्तर",
    "battery" to "बैटरी",
    "trends_24h" to "24-घंटे के रुझान",
    "alerts" to "अलर्ट",
    "good" to "अच्छा",
    "normal" to "सामान्य",
    "optimal" to "इष्टतम",
    
    // Services
    "services" to "सेवाएं",
    "ai_tools" to "AI-संचालित खेती उपकरण",
    "available_services" to "उपलब्ध सेवाएं",
    "crop_recommendation" to "फसल सिफारिश",
    "disease_detection" to "रोग पहचान",
    "satellite_monitoring" to "उपग्रह निगरानी",
    "weather_forecast" to "मौसम पूर्वानुमान",
    "market_intelligence" to "बाजार बुद्धिमत्ता",
    
    // Profile
    "profile" to "प्रोफ़ाइल",
    "edit_photo" to "फोटो संपादित करें",
    "personal_info" to "व्यक्तिगत जानकारी",
    "full_name" to "पूरा नाम",
    "email" to "ईमेल",
    "contact_number" to "संपर्क नंबर",
    "farm_details" to "खेत विवरण",
    "land_area" to "भूमि क्षेत्र",
    "crop_type" to "फसल प्रकार",
    "state" to "राज्य",
    "district" to "जिला",
    "address" to "पता",
    "settings" to "सेटिंग्स",
    "notifications" to "सूचनाएं",
    "language" to "भाषा",
    "help_support" to "सहायता और समर्थन",
    "about" to "एग्रीफार्म के बारे में",
    "logout" to "लॉगआउट",
    "enabled" to "सक्षम",
    "save" to "सहेजें",
    "cancel" to "रद्द करें",
    "select_state" to "राज्य चुनें",
    "select_district" to "जिला चुनें",
    "select_crops" to "फसलें चुनें",
    "select_language" to "भाषा चुनें",
    "coming_soon" to "जल्द आ रहा है",
    "back" to "वापस"
)
