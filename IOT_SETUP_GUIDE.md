# 🌾 AgriFarm IoT Integration Guide

## ✅ COMPLETE INTEGRATION - Production Ready!

### What's Integrated:
- ✅ ESP32 with Supabase database integration
- ✅ SN74HC595 shift register for LED control
- ✅ OLED display (128x64) with real-time data
- ✅ DHT11 (Temperature & Humidity)
- ✅ Soil Moisture (Analog + Digital)
- ✅ Rain Sensor (Analog + Digital)
- ✅ Gas Sensor (MQ-135)
- ✅ Buzzer alerts for critical conditions
- ✅ WiFi LED indicator
- ✅ Android app with My Farm tab
- ✅ Real device online/offline status
- ✅ 24-hour trend analysis
- ✅ Auto-refresh every 5 seconds

---

## 📡 ESP32 Hardware Setup

### Components (Your Working Setup):
- ESP32 board
- SN74HC595 Shift Register (LED control)
- OLED Display (SSD1306, 128x64)
- DHT11 (Temperature & Humidity)
- Soil Moisture sensor (Analog + Digital)
- Rain sensor (Analog + Digital)
- Gas sensor (MQ-135)
- Buzzer
- 8 LEDs (connected to shift register)
- WiFi LED

### Pin Connections (Your Configuration):
```
DHT11 Data → GPIO 15
Soil Analog → GPIO 34
Soil Digital → GPIO 27
Rain Analog → GPIO 32
Rain Digital → GPIO 26
Gas Analog → GPIO 35
Buzzer → GPIO 5
WiFi LED → GPIO 13

74HC595 Shift Register:
  Data Pin → GPIO 23
  Latch Pin → GPIO 19
  Clock Pin → GPIO 18

OLED (I2C):
  SDA → GPIO 21
  SCL → GPIO 22
```

### LED Indicators (Shift Register):
```
Bit 0: Soil Health (Green)
Bit 1: DHT Health (Green)
Bit 2: Gas Health (Green)
Bit 3: Rain Health (Green)
Bit 4: Warning (Yellow)
Bit 5: Critical (Red)
Bit 6: Soil Dry (Yellow)
Bit 7: System OK (Green)
```

---

## 🚀 Complete Setup Guide

### Step 1: Setup Supabase Database

1. **Your database already exists!** ✅
2. **Add helper functions**:
   - Open Supabase SQL Editor
   - Copy entire `SUPABASE_SETUP.sql`
   - Paste and click **RUN**
   - Verify: "Success. No rows returned"

3. **Register your ESP32 device**:
   ```sql
   INSERT INTO public.iot_devices (user_id, device_id, device_name)
   VALUES (
       'YOUR_USER_UUID',      -- From auth.users table
       'ESP32_FARM_001',      -- Device identifier
       'Farm Sensor Unit 1'   -- Friendly name
   );
   ```

4. **Get your credentials**:
   - Go to Settings → API
   - Copy Project URL: `https://xxxxx.supabase.co`
   - Copy anon/public key: `eyJhbGc...`

### Step 2: Upload ESP32 Code

1. Open `ESP32_AGRIFARM_INTEGRATED.ino` in Arduino IDE

2. **Update WiFi** (lines 13-14):
   ```cpp
   const char* ssid = "YOUR_WIFI_NAME";
   const char* password = "YOUR_WIFI_PASSWORD";
   ```

3. **Update Supabase** (lines 16-17):
   ```cpp
   String supabaseURL = "https://YOUR_PROJECT.supabase.co/rest/v1/iot_sensor_data";
   String supabaseKey = "YOUR_ANON_KEY_HERE";
   ```

4. **Update device ID** (line 21) - must match database:
   ```cpp
   String deviceId = "ESP32_FARM_001";
   ```

5. **Install libraries** (Arduino IDE → Tools → Manage Libraries):
   - Adafruit SSD1306
   - Adafruit GFX Library
   - DHT sensor library by Adafruit
   - ArduinoJson by Benoit Blanchon

6. **Select board**: ESP32 Dev Module

7. **Upload** and open Serial Monitor (115200 baud)

---

### Step 3: Update Android App

1. **Update NetworkModule.kt**:
   ```kotlin
   @Provides
   @Singleton
   fun provideSensorApi(): SensorApi {
       return Retrofit.Builder()
           .baseUrl("https://YOUR_PROJECT.supabase.co/")
           .addConverterFactory(GsonConverterFactory.create())
           .build()
           .create(SensorApi::class.java)
   }
   ```

2. **Add Supabase headers** (add to NetworkModule.kt):
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
           .build()
   }
   ```

3. **Build and run** the app

### Step 4: Test Everything

1. **ESP32 Serial Monitor** should show:
   ```
   WiFi Connected: 192.168.x.x
   Sending: {"device_id":"...","temperature":28.5,...}
   Supabase Response: 201
   ```

2. **Supabase Table Editor**:
   - Go to `iot_sensor_data` table
   - See new rows appearing every 15 seconds

3. **Android App - My Farm Tab**:
   - Device shows 🟢 "Online"
   - Real sensor readings displayed
   - 24-hour trends (after 2+ hours of data)

---

## 📱 App Features

### My Farm Tab:
- **Device Status Card**:
  - 🟢 Online (green) - ESP32 active in last 5 min
  - ⚫ Offline (gray) - No data for 5+ min
  - Last seen timestamp

- **Sensor Cards**:
  - 🌡️ **Temperature** - °C with color coding
  - 💧 **Humidity** - % with status
  - 🌱 **Soil Moisture** - % with dry/good/wet status
  - ☁️ **Rain Sensor** - Detected/Not detected
  - 💨 **Gas Level** - ADC value with safe/alert status

- **24-Hour Trends**:
  - ↑ Soil Moisture increased 5%
  - ↓ Temperature decreased 2°C
  - → Humidity stable

### IoT Monitor Screen:
- Real-time sensor monitoring
- Auto-refresh every 5 seconds
- Health indicators for each sensor
- Color-coded alerts

---

## 🔧 How It Works

### Complete Data Flow:
```
ESP32 Sensors → WiFi → Supabase (iot_sensor_data) → Android App
     ↓              ↓                ↓                      ↓
  Every 2s      Every 15s      Auto-trigger         Every 5s
  (read)        (send)      (update status)        (fetch)
```

### Database Architecture:
```
iot_devices (device registration)
    ↓
iot_sensor_data (sensor readings)
    ↓
Functions: get_latest_sensor_data()
           get_24h_sensor_trends()
           is_device_online()
    ↓
Android App (display)
```

### Update Frequency:
- **ESP32**: Reads sensors every 2s, sends every 15s
- **OLED**: Updates display every 2.5s
- **Database**: Stores every reading with timestamp
- **App**: Polls Supabase every 5s
- **Trends**: Calculated hourly from last 24h data

### ESP32 → Supabase JSON:
```json
{
  "device_id": "uuid-from-iot_devices-table",
  "temperature": 28.5,
  "humidity": 65.2,
  "soil_moisture": 2450,
  "gas_level": 1850,
  "rain_detected": false
}
```

### Supabase → App JSON:
```json
{
  "device_id": "ESP32_FARM_001",
  "temperature": 28.5,
  "humidity": 65.2,
  "soil_moisture_pct": 45,
  "gas_level": 1850,
  "rain_detected": false,
  "dht_ok": true,
  "soil_ok": true
}
```

---

## 🗄️ Database Schema

### Your Existing Tables:

**iot_devices** (device registration):
```sql
id (UUID) - Primary key
user_id (UUID) - References users
device_id (TEXT) - "ESP32_FARM_001"
device_name (TEXT) - "Farm Sensor Unit 1"
status (TEXT) - "online" or "offline"
last_sync (TIMESTAMP) - Auto-updated by trigger
```

**iot_sensor_data** (sensor readings):
```sql
id (UUID) - Primary key
device_id (UUID) - References iot_devices.id
temperature (DECIMAL)
humidity (DECIMAL)
soil_moisture (INTEGER)
gas_level (INTEGER)
rain_detected (BOOLEAN)
timestamp (TIMESTAMP) - Auto-generated
```

### Added Functions:

1. **get_latest_sensor_data(device_id)** - Returns most recent reading
2. **get_24h_sensor_trends(device_id)** - Returns hourly averages
3. **is_device_online(device_id)** - Checks if data < 5 min old
4. **update_device_status()** - Trigger to auto-update device status

---

## ✨ Complete Feature List

### ESP32 Hardware:
- ✅ Sensor fault detection (N/A for disconnected)
- ✅ ADC stabilization (5-sample averaging)
- ✅ OLED real-time display with status
- ✅ 8 LED indicators via SN74HC595:
  - Bit 0-3: Sensor health (green)
  - Bit 4: Warning (yellow)
  - Bit 5: Critical (red)
  - Bit 6: Soil dry (yellow)
  - Bit 7: System OK (green)
- ✅ Buzzer alerts:
  - Continuous tone for critical (gas > 3600)
  - Short beep for warnings (every 15s)
- ✅ WiFi auto-reconnect every 30s
- ✅ Supabase REST API integration

### Android App:
- ✅ **My Farm Tab**:
  - Real device online/offline status
  - Last seen timestamp
  - Live sensor readings
  - 24-hour trend analysis
  - Color-coded alerts
- ✅ **IoT Monitor Screen**:
  - Real-time monitoring
  - Health indicators
  - Auto-refresh
- ✅ Material 3 design
- ✅ Hilt dependency injection
- ✅ MVVM architecture
- ✅ Coroutines for async operations

---

## 🎨 Sensor Thresholds (Calibrate for Your Farm)

### Soil Moisture:
- Wet: < 1500 (Green)
- Normal: 1500-3500 (Green)
- Dry: 3000-3500 (Orange)
- Very Dry: > 3500 (Red)

### Gas Levels:
- Normal: < 2000 (Green)
- Warning: 2000-3000 (Orange)
- Critical: > 3600 (Red + Buzzer)

### Temperature:
- Cold: < 15°C (Blue)
- Normal: 15-35°C (Green)
- Hot: > 35°C (Red)

---

## 🐛 Troubleshooting

### ESP32 Issues:

**OLED not working**:
- Check I2C address: 0x3C (default)
- Verify SDA → GPIO 21, SCL → GPIO 22
- Test: Run I2C scanner sketch

**Sensors show N/A**:
- Check wiring and power (3.3V or 5V)
- Verify pin connections match code
- Test individual sensors with simple sketches

**WiFi not connecting**:
- Verify SSID and password (case-sensitive)
- Check 2.4GHz WiFi (ESP32 doesn't support 5GHz)
- Move closer to router

**Supabase errors**:
- **401 Unauthorized**: Wrong anon key
- **404 Not Found**: Wrong URL or table name
- **400 Bad Request**: Check device UUID in database
- **500 Server Error**: Check RLS policies

**Serial Monitor shows errors**:
```
Supabase Response: 201 ✅ Success
Supabase Response: 401 ❌ Check anon key
Supabase Response: 404 ❌ Check URL
```

### App Issues:

**Device shows "Offline"**:
1. Check ESP32 Serial Monitor - is it sending?
2. Check Supabase table: `SELECT * FROM iot_sensor_data ORDER BY timestamp DESC LIMIT 1`
3. Verify timestamp is < 5 minutes old
4. Check device_id matches in both tables

**No trends showing**:
- Need at least 2 hours of data
- Check: `SELECT COUNT(*) FROM iot_sensor_data WHERE timestamp >= NOW() - INTERVAL '24 hours'`
- Verify function returns data: `SELECT * FROM get_24h_sensor_trends('ESP32_FARM_001')`

**Connection error in app**:
- Verify Supabase URL in NetworkModule.kt
- Check anon key in headers
- Test API with Postman first
- Check internet connection

**Build errors**:
- Sync Gradle files
- Clean and rebuild project
- Invalidate caches and restart Android Studio

---

## 📱 Using the App

### Home Screen:
1. Tap **"IoT Monitor"** button → Real-time sensor screen
2. Bottom navigation → **"My Farm"** tab

### My Farm Tab:
- **Device Status Card**: Shows online/offline with last seen
- **Sensor Cards Grid**: 6 cards with live readings
- **24-Hour Trends**: Shows changes over last day
- **Alerts**: Warnings and recommendations

### IoT Monitor Screen:
- Full-screen sensor monitoring
- Auto-refresh every 5 seconds
- Back button to return home

### What Each Color Means:

**Temperature**:
- 🔵 Blue: < 15°C (cold)
- 🟢 Green: 15-35°C (normal)
- 🔴 Red: > 35°C (hot)

**Soil Moisture**:
- 🔴 Red: < 30% (dry - needs water)
- 🟠 Orange: 30-60% (moderate)
- 🟢 Green: > 60% (good)

**Gas Level**:
- 🟢 Green: < 3000 (safe)
- 🟠 Orange: 3000-3600 (warning)
- 🔴 Red: > 3600 (critical - ventilate!)

---

## 🔐 Security Notes

- Never commit WiFi passwords
- Keep Supabase keys secure
- Use environment variables in production
- Enable Row Level Security in Supabase

---

## 📚 Additional Documentation

- **SUPABASE_INTEGRATION_EXISTING_DB.md** - Complete Supabase setup guide
- **SUPABASE_SETUP.sql** - SQL functions to run
- **ESP32_AGRIFARM_INTEGRATED.ino** - ESP32 source code
- **SENSOR_INTEGRATION_COMPLETE.md** - Quick reference

---

## ✅ Setup Checklist

- [ ] Run `SUPABASE_SETUP.sql` in Supabase SQL Editor
- [ ] Register device in `iot_devices` table
- [ ] Get Supabase URL and anon key
- [ ] Update ESP32 code (WiFi + Supabase)
- [ ] Install Arduino libraries
- [ ] Upload ESP32 code
- [ ] Verify Serial Monitor shows "201"
- [ ] Update NetworkModule.kt (Supabase URL + key)
- [ ] Build and run Android app
- [ ] Check My Farm tab shows "Online"
- [ ] Wait 2+ hours for trend data

---

**Built with ❤️ by Team CodeRed for SIH 2025**  
**Smart India Hackathon - Problem Statement SIH25030**  
**AI-Based Crop Recommendation for Farmers**

**Status: PRODUCTION READY** 🚀
