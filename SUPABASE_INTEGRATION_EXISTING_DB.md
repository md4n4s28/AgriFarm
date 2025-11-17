# 🗄️ Supabase Integration - Using Your Existing Database

## ✅ Your Database is Already Set Up!

You have:
- ✅ `users` table
- ✅ `iot_devices` table  
- ✅ `iot_sensor_data` table
- ✅ RLS policies configured

We just need to add helper functions and connect ESP32 + App.

---

## 📋 Step 1: Register Your ESP32 Device

Run this in Supabase SQL Editor:

```sql
-- First, create a test user (or use your existing user ID)
INSERT INTO public.users (id, phone, name, preferred_language)
VALUES (
    'YOUR_USER_UUID_HERE',  -- Replace with actual user UUID from auth.users
    '+1234567890',
    'Test Farmer',
    'en'
);

-- Register your ESP32 device
INSERT INTO public.iot_devices (user_id, device_id, device_name, status)
VALUES (
    'YOUR_USER_UUID_HERE',  -- Same user UUID
    'ESP32_FARM_001',       -- This matches your ESP32 code
    'Farm Sensor Unit 1',
    'offline'
);
```

---

## 📋 Step 2: Add Helper Functions

1. Open Supabase SQL Editor
2. Copy entire content from `SUPABASE_SETUP.sql`
3. Paste and click **RUN**
4. You should see: "Success. No rows returned"

This adds:
- ✅ `get_latest_sensor_data()` - Fetch latest readings
- ✅ `get_24h_sensor_trends()` - Calculate hourly averages
- ✅ `is_device_online()` - Check if device sent data in last 5 min
- ✅ Auto-update device status trigger

---

## 📋 Step 3: Update ESP32 Code

Open `ESP32_AGRIFARM_INTEGRATED.ino` and update:

```cpp
// Lines 16-17: Update Supabase URL
String supabaseURL = "https://YOUR_PROJECT.supabase.co/rest/v1/iot_sensor_data";
String supabaseKey = "YOUR_ANON_KEY";

// Line 24: Keep device ID matching database
String deviceId = "ESP32_FARM_001";
```

### Update sendToServer() function (around line 200):

```cpp
void sendToServer() {
  if (!wifiOK) return;

  HTTPClient http;
  
  // Get device UUID from iot_devices table first
  // For now, hardcode it after registration
  String deviceUUID = "YOUR_DEVICE_UUID_FROM_IOT_DEVICES_TABLE";
  
  StaticJsonDocument<512> doc;
  doc["device_id"] = deviceUUID;  // Use UUID, not string
  doc["temperature"] = dhtOK ? tempC : NULL;
  doc["humidity"] = dhtOK ? hum : NULL;
  doc["soil_moisture"] = soilOK ? soilAnalog : NULL;
  doc["gas_level"] = gasOK ? gasVal : NULL;
  doc["rain_detected"] = rainOK ? (rainDO == LOW) : false;
  
  String jsonString;
  serializeJson(doc, jsonString);
  
  http.begin(supabaseURL);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("apikey", supabaseKey);
  http.addHeader("Authorization", "Bearer " + supabaseKey);
  http.addHeader("Prefer", "return=minimal");
  
  int code = http.POST(jsonString);
  Serial.printf("Supabase Response: %d\n", code);
  http.end();
}
```

---

## 📋 Step 4: Get Your Supabase Credentials

1. Go to **Settings** → **API**
2. Copy:
   - **Project URL**: `https://xxxxx.supabase.co`
   - **anon public key**: `eyJhbGc...`

---

## 📋 Step 5: Update Android App

### 5.1 Update NetworkModule.kt

File: `app/src/main/java/com/agrifarm/app/di/NetworkModule.kt`

```kotlin
@Provides
@Singleton
fun provideSensorApi(): SensorApi {
    return Retrofit.Builder()
        .baseUrl("https://YOUR_PROJECT.supabase.co/")  // ← Your Supabase URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SensorApi::class.java)
}
```

### 5.2 Add Supabase Headers

Add this to NetworkModule.kt:

```kotlin
@Provides
@Singleton
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", "YOUR_ANON_KEY")
                .addHeader("Authorization", "Bearer YOUR_ANON_KEY")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}

// Update provideSensorApi to use OkHttpClient
@Provides
@Singleton
fun provideSensorApi(okHttpClient: OkHttpClient): SensorApi {
    return Retrofit.Builder()
        .baseUrl("https://YOUR_PROJECT.supabase.co/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SensorApi::class.java)
}
```

---

## 📋 Step 6: Test Everything

### 6.1 Test in Supabase SQL Editor:

```sql
-- Check if device is registered
SELECT * FROM public.iot_devices WHERE device_id = 'ESP32_FARM_001';

-- Insert test sensor data
INSERT INTO public.iot_sensor_data (device_id, temperature, humidity, soil_moisture, gas_level, rain_detected)
VALUES (
    (SELECT id FROM public.iot_devices WHERE device_id = 'ESP32_FARM_001'),
    28.5,
    65.2,
    2450,
    1850,
    false
);

-- Test functions
SELECT * FROM get_latest_sensor_data('ESP32_FARM_001');
SELECT is_device_online('ESP32_FARM_001');
SELECT * FROM get_24h_sensor_trends('ESP32_FARM_001');
```

### 6.2 Upload ESP32 & Monitor:

1. Upload code to ESP32
2. Open Serial Monitor
3. Watch for "Supabase Response: 201" (success)
4. Check Supabase Table Editor → `iot_sensor_data`

### 6.3 Run Android App:

1. Build & Run app
2. Go to **My Farm** tab
3. See:
   - ✅ Device shows "Online" (green dot)
   - ✅ Real sensor readings
   - ✅ Last seen timestamp
   - ✅ 24-hour trends (after 2+ hours of data)

---

## 🎯 What You'll See

### My Farm Tab:
- **Device Status**: 
  - 🟢 "Online" if ESP32 sent data in last 5 minutes
  - ⚫ "Offline" if no data for 5+ minutes
- **Last Seen**: "Just now" or "5 min ago"
- **Sensor Cards**: Real-time temperature, humidity, soil, gas, rain
- **24-Hour Trends**: 
  - ↑ 2°C (temperature increased)
  - ↓ 5% (soil moisture decreased)
  - → Stable (humidity unchanged)

---

## 🔧 Troubleshooting

### ESP32 Issues:

**Error: 401 Unauthorized**
- Check Supabase anon key is correct
- Verify RLS policy allows insert

**Error: 404 Not Found**
- Check Supabase URL is correct
- Ensure `/rest/v1/iot_sensor_data` endpoint exists

**Error: 400 Bad Request**
- Device UUID might be wrong
- Check device is registered in `iot_devices` table

### App Issues:

**Device shows "Offline"**
- Check ESP32 is running and sending data
- Verify data in Supabase: `SELECT * FROM iot_sensor_data ORDER BY timestamp DESC LIMIT 1`
- Ensure timestamp is < 5 minutes old

**No trends showing**
- Need at least 2 hours of data
- Check: `SELECT COUNT(*) FROM iot_sensor_data WHERE timestamp >= NOW() - INTERVAL '24 hours'`

**Connection error**
- Verify Supabase URL in NetworkModule
- Check anon key is added to headers
- Test API in Postman first

---

## 📊 Database Schema Reference

```
users (existing)
  ├─ id (UUID, primary key)
  └─ phone, name, farm_location, etc.

iot_devices (existing)
  ├─ id (UUID, primary key)
  ├─ user_id (references users)
  ├─ device_id (string, e.g., "ESP32_FARM_001")
  ├─ status (online/offline)
  └─ last_sync (timestamp)

iot_sensor_data (existing)
  ├─ id (UUID, primary key)
  ├─ device_id (references iot_devices.id)
  ├─ temperature, humidity, soil_moisture
  ├─ gas_level, rain_detected
  └─ timestamp (auto-generated)
```

---

## 🚀 Quick Start Checklist

- [ ] Register device in `iot_devices` table
- [ ] Run `SUPABASE_SETUP.sql` to add functions
- [ ] Get Supabase URL and anon key
- [ ] Update ESP32 code with URL + key
- [ ] Update NetworkModule.kt with URL + key
- [ ] Upload ESP32 code
- [ ] Build & run Android app
- [ ] Check "My Farm" tab for live data

---

**Status: READY TO INTEGRATE** 🌾

Your database is perfect! Just add functions → connect ESP32 → enjoy real-time IoT monitoring!
