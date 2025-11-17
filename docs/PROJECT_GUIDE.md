# 📱 AgriFarm Mobile App - Development Guide
**Smart India Hackathon 2025 - SIH25030**

---

## 📑 Quick Navigation
- [Project Overview](#project-overview)
- [Setup Checklist](#setup-checklist)
- [Phase 1: Project Setup](#phase-1-project-setup)
- [Phase 2: Supabase Setup](#phase-2-supabase-setup)
- [Phase 3: Theme & UI](#phase-3-theme--ui)
- [Phase 4: Authentication](#phase-4-authentication)
- [Phase 5: IoT Integration](#phase-5-iot-integration)
- [Phase 6: Dashboard](#phase-6-dashboard)
- [Phase 7: Crop Module](#phase-7-crop-module)
- [Phase 8: Market Prices](#phase-8-market-prices)
- [Phase 9: Disease Detection](#phase-9-disease-detection)
- [Phase 10: Testing](#phase-10-testing)

---

## 🎯 Project Overview

**App Name:** AgriFarm  
**Package:** com.agrifarm.app  
**Colors:** Green (#4CAF50), White (#FFFFFF), Black (#000000)

**Tech Stack:**
- Jetpack Compose
- Supabase (Auth, Database, Storage)
- Hilt (Dependency Injection)
- Retrofit (API calls)
- Coil (Image loading)
- Vico (Charts)

**IoT Device:** ESP32 with Soil, DHT11, Rain, Gas sensors

---

## ✅ Setup Checklist

### Prerequisites
- [ ] Android Studio installed
- [ ] JDK 11+ installed
- [ ] Supabase account created
- [ ] Git configured

### Phase 1 Checklist
- [ ] Update build.gradle.kts files
- [ ] Update libs.versions.toml
- [ ] Add Hilt and Serialization plugins
- [ ] Create project folder structure
- [ ] Sync Gradle successfully

### Phase 2 Checklist
- [ ] Create Supabase project
- [ ] Copy API credentials to local.properties
- [ ] Run database schema SQL
- [ ] Create storage buckets
- [ ] Test Supabase connection

### Phase 3 Checklist
- [ ] Create Color.kt
- [ ] Create Type.kt
- [ ] Create Theme.kt
- [ ] Test theme in preview

---

## 📦 Phase 1: Project Setup

### Step 1.1: Update Dependencies

**File:** `app/build.gradle.kts`

Add at top:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}
```

Update android block:
```kotlin
android {
    namespace = "com.agrifarm.app"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.agrifarm.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        
        buildConfigField("String", "SUPABASE_URL", "\"${project.findProperty("SUPABASE_URL") ?: ""}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${project.findProperty("SUPABASE_KEY") ?: ""}\"")
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
```

Add dependencies:
```kotlin
dependencies {
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Supabase
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.0.3")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.0.3")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.0.3")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.0.3")
    implementation("io.ktor:ktor-client-android:2.3.7")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Image & Charts
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.patrykandpatrick.vico:compose:1.13.1")
    
    // Camera
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    
    // Location
    implementation("com.google.android.gms:play-services-location:21.1.0")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}
```

**File:** `build.gradle.kts` (root)

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
}
```

### Step 1.2: Create Folder Structure

Run in terminal:
```bash
cd app/src/main/java
mkdir -p com/agrifarm/app/{data/{local,remote/{api,dto,supabase},repository,model},domain/{model,repository,usecase},presentation/{auth,dashboard,iot,crop,market,disease,profile,common,navigation,theme},di,util}
```

### Step 1.3: Sync Gradle

Click "Sync Now" in Android Studio. Wait for completion.

---

## 🔐 Phase 2: Supabase Setup

### Step 2.1: Create Project

1. Go to [supabase.com](https://supabase.com)
2. Create new project: "agrifarm-sih"
3. Choose region: Southeast Asia
4. Save database password securely

### Step 2.2: Get Credentials

1. Go to Settings → API
2. Copy Project URL and anon key
3. Add to `local.properties`:

```properties
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Step 2.3: Create Database

Go to SQL Editor, run this:

```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.users (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    phone VARCHAR(15) UNIQUE,
    name VARCHAR(100),
    farm_location_lat DECIMAL(10, 8),
    farm_location_lng DECIMAL(11, 8),
    farm_size DECIMAL(10, 2),
    preferred_language VARCHAR(10) DEFAULT 'en',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE public.iot_devices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES public.users(id) ON DELETE CASCADE,
    device_id VARCHAR(50) UNIQUE NOT NULL,
    device_name VARCHAR(100),
    status VARCHAR(20) DEFAULT 'offline',
    last_sync TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE public.iot_sensor_data (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id UUID REFERENCES public.iot_devices(id) ON DELETE CASCADE,
    soil_moisture INTEGER,
    temperature DECIMAL(5, 2),
    humidity DECIMAL(5, 2),
    rain_detected BOOLEAN,
    gas_level INTEGER,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_sensor_data_device_time ON public.iot_sensor_data(device_id, timestamp DESC);

CREATE TABLE public.crop_recommendations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES public.users(id) ON DELETE CASCADE,
    recommended_crops JSONB,
    confidence_score DECIMAL(5, 2),
    reasoning TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE public.market_prices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    crop_name VARCHAR(100),
    state VARCHAR(100),
    district VARCHAR(100),
    market VARCHAR(100),
    price DECIMAL(10, 2),
    unit VARCHAR(20),
    date DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE public.disease_detections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES public.users(id) ON DELETE CASCADE,
    image_url TEXT,
    disease_name VARCHAR(200),
    confidence DECIMAL(5, 2),
    treatment TEXT,
    detected_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE public.alerts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES public.users(id) ON DELETE CASCADE,
    type VARCHAR(50),
    title VARCHAR(200),
    message TEXT,
    priority VARCHAR(20) DEFAULT 'medium',
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.iot_devices ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.iot_sensor_data ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.crop_recommendations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.disease_detections ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.alerts ENABLE ROW LEVEL SECURITY;

-- RLS Policies
CREATE POLICY "Users can view own profile" ON public.users
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update own profile" ON public.users
    FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users can insert own profile" ON public.users
    FOR INSERT WITH CHECK (auth.uid() = id);

CREATE POLICY "Users can view own devices" ON public.iot_devices
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own devices" ON public.iot_devices
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can view own sensor data" ON public.iot_sensor_data
    FOR SELECT USING (
        device_id IN (SELECT id FROM public.iot_devices WHERE user_id = auth.uid())
    );

CREATE POLICY "Allow device data insertion" ON public.iot_sensor_data
    FOR INSERT WITH CHECK (true);

CREATE POLICY "Authenticated users can view market prices" ON public.market_prices
    FOR SELECT USING (auth.role() = 'authenticated');
```

### Step 2.4: Create Storage Buckets

1. Go to Storage
2. Create bucket: `disease-images` (public)
3. Create bucket: `profile-images` (public)

### Step 2.5: Create Supabase Client

**File:** `app/src/main/java/com/agrifarm/app/data/remote/supabase/SupabaseClient.kt`

```kotlin
package com.agrifarm.app.data.remote.supabase

import com.agrifarm.app.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}
```

---

## 🎨 Phase 3: Theme & UI

### Step 3.1: Colors

**File:** `app/src/main/java/com/agrifarm/app/presentation/theme/Color.kt`

```kotlin
package com.agrifarm.app.presentation.theme

import androidx.compose.ui.graphics.Color

val GreenPrimary = Color(0xFF4CAF50)
val GreenDark = Color(0xFF2E7D32)
val GreenLight = Color(0xFF81C784)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val GrayLight = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFF757575)
val Error = Color(0xFFF44336)
val Warning = Color(0xFFFFC107)
val Success = Color(0xFF4CAF50)
```

### Step 3.2: Theme

**File:** `app/src/main/java/com/agrifarm/app/presentation/theme/Theme.kt`

```kotlin
package com.agrifarm.app.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = White,
    secondary = GreenDark,
    onSecondary = White,
    error = Error,
    onError = White,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black
)

@Composable
fun AgriFarmTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = GreenPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
```

---

## 🔑 Phase 4: Authentication

### Step 4.1: Create Models

**File:** `app/src/main/java/com/agrifarm/app/domain/model/User.kt`

```kotlin
package com.agrifarm.app.domain.model

data class User(
    val id: String,
    val phone: String?,
    val name: String,
    val farmLocationLat: Double?,
    val farmLocationLng: Double?,
    val farmSize: Double?,
    val preferredLanguage: String = "en"
)
```

### Step 4.2: Auth Repository Interface

**File:** `app/src/main/java/com/agrifarm/app/domain/repository/AuthRepository.kt`

```kotlin
package com.agrifarm.app.domain.repository

import com.agrifarm.app.domain.model.User

interface AuthRepository {
    suspend fun signInWithPhone(phone: String): Result<String>
    suspend fun verifyOtp(phone: String, otp: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
}
```

### Step 4.3: Auth Repository Implementation

**File:** `app/src/main/java/com/agrifarm/app/data/repository/AuthRepositoryImpl.kt`

```kotlin
package com.agrifarm.app.data.repository

import com.agrifarm.app.data.remote.supabase.SupabaseClient
import com.agrifarm.app.domain.model.User
import com.agrifarm.app.domain.repository.AuthRepository
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    
    private val supabase = SupabaseClient.client
    
    override suspend fun signInWithPhone(phone: String): Result<String> {
        return try {
            supabase.auth.signInWith(OTP) {
                this.phone = phone
            }
            Result.success("OTP sent to $phone")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyOtp(phone: String, otp: String): Result<User> {
        return try {
            supabase.auth.verifyPhoneOtp(
                type = OTP.Type.SMS,
                phone = phone,
                token = otp
            )
            getCurrentUser()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signOut(): Result<Unit> {
        return try {
            supabase.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val authUser = supabase.auth.currentUserOrNull()
            if (authUser == null) {
                Result.success(null)
            } else {
                val userDto = supabase.from("users")
                    .select()
                    .decodeSingle<UserDto>()
                
                Result.success(User(
                    id = userDto.id,
                    phone = userDto.phone,
                    name = userDto.name,
                    farmLocationLat = userDto.farm_location_lat,
                    farmLocationLng = userDto.farm_location_lng,
                    farmSize = userDto.farm_size,
                    preferredLanguage = userDto.preferred_language
                ))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Serializable
private data class UserDto(
    val id: String,
    val phone: String?,
    val name: String,
    val farm_location_lat: Double?,
    val farm_location_lng: Double?,
    val farm_size: Double?,
    val preferred_language: String
)
```

### Step 4.4: Hilt Module

**File:** `app/src/main/java/com/agrifarm/app/di/AppModule.kt`

```kotlin
package com.agrifarm.app.di

import com.agrifarm.app.data.repository.AuthRepositoryImpl
import com.agrifarm.app.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }
}
```

### Step 4.5: Application Class

**File:** `app/src/main/java/com/agrifarm/app/AgriFarmApp.kt`

```kotlin
package com.agrifarm.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AgriFarmApp : Application()
```

Update `AndroidManifest.xml`:
```xml
<application
    android:name=".AgriFarmApp"
    ...>
```

---

## 📡 Phase 5: IoT Integration

### Step 5.1: IoT Models

**File:** `app/src/main/java/com/agrifarm/app/domain/model/IoTDevice.kt`

```kotlin
package com.agrifarm.app.domain.model

data class IoTDevice(
    val id: String,
    val deviceId: String,
    val deviceName: String,
    val status: DeviceStatus,
    val lastSync: Long?
)

enum class DeviceStatus {
    ONLINE, OFFLINE, ERROR
}

data class SensorData(
    val id: String,
    val deviceId: String,
    val soilMoisture: Int?,
    val temperature: Float?,
    val humidity: Float?,
    val rainDetected: Boolean?,
    val gasLevel: Int?,
    val timestamp: Long
)
```

### Step 5.2: Update ESP32 Code

Update your Arduino code to send to Supabase:

```cpp
// Change API URL to your Supabase function
String apiURL = "https://YOUR_PROJECT.supabase.co/rest/v1/iot_sensor_data";
String apiKey = "YOUR_SUPABASE_ANON_KEY";

void sendToServer() {
  if (!wifiOK) return;

  HTTPClient http;
  http.begin(apiURL);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("apikey", apiKey);
  http.addHeader("Authorization", "Bearer " + apiKey);
  
  String json = "{";
  json += "\"device_id\":\"YOUR_DEVICE_UUID\",";
  json += "\"humidity\":" + String(dhtOK ? hum : -1) + ",";
  json += "\"soil_moisture\":" + String(soilOK ? soilAnalog : -1) + ",";
  json += "\"gas_level\":" + String(gasOK ? gasVal : -1) + ",";
  json += "\"rain_detected\":" + String(rainDO ? "true" : "false");
  json += "}";
  
  int code = http.POST(json);
  Serial.println(code > 0 ? "Sent: " + String(code) : "HTTP Err");
  http.end();
}
```

---

## 📊 Phase 6: Dashboard

### Step 6.1: Navigation Setup

**File:** `app/src/main/java/com/agrifarm/app/presentation/navigation/Screen.kt`

```kotlin
package com.agrifarm.app.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object IoT : Screen("iot")
    object Crop : Screen("crop")
    object Market : Screen("market")
    object Disease : Screen("disease")
    object Profile : Screen("profile")
}
```

### Step 6.2: Main Activity

**File:** `app/src/main/java/com/agrifarm/app/MainActivity.kt`

```kotlin
package com.agrifarm.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.agrifarm.app.presentation.navigation.NavGraph
import com.agrifarm.app.presentation.theme.AgriFarmTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgriFarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
```

---

## 🌾 Phase 7: Crop Module

Coming soon - Will integrate AI model for crop recommendations

---

## 💰 Phase 8: Market Prices

Coming soon - Will integrate market price APIs

---

## 🔬 Phase 9: Disease Detection

Coming soon - Will integrate camera and AI disease detection model

---

## ✅ Phase 10: Testing

Coming soon - Testing checklist and deployment guide

---

## 📝 Notes

- Keep this file updated as you progress
- Mark checkboxes as you complete tasks
- Add your own notes and learnings
- Save API keys securely in local.properties

---

## 🆘 Common Issues

**Issue:** Gradle sync fails  
**Fix:** Check internet connection, update Gradle version

**Issue:** Supabase connection error  
**Fix:** Verify API keys in local.properties

**Issue:** Build config not found  
**Fix:** Ensure buildFeatures { buildConfig = true }

---

**Last Updated:** [Add date when you update]
**Current Phase:** Phase 1 - Project Setup
