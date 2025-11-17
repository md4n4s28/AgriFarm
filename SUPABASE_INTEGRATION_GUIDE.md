# 🗄️ Supabase Database Integration Guide

## ✅ Step 1: Create Supabase Project

1. Go to https://supabase.com
2. Click "New Project"
3. Enter project details:
   - Name: `agrifarm-iot`
   - Database Password: (save this!)
   - Region: Choose closest to you
4. Wait for project creation (~2 minutes)

---

## ✅ Step 2: Run SQL Setup

1. In Supabase Dashboard, go to **SQL Editor**
2. Click "New Query"
3. Copy entire content from `SUPABASE_SETUP.sql`
4. Paste and click **RUN**
5. You should see: "Success. No rows returned"

This creates:
- ✅ `sensor_data` table
- ✅ Indexes for fast queries
- ✅ RLS policies (security)
- ✅ Functions: `get_latest_sensor_data()`, `get_24h_sensor_trends()`, `is_device_online()`

---

## ✅ Step 3: Get API Keys

1. Go to **Settings** → **API**
2. Copy these values:

```
Project URL: https://xxxxx.supabase.co
anon/public key: eyJhbGc...
```

---

## ✅ Step 4: Update ESP32 Code

Open `ESP32_AGRIFARM_INTEGRATED.ino` and update:

```cpp
// Line 16-17
String supabaseURL = "https://YOUR_PROJECT.supabase.co/rest/v1/sensor_data";
String supabaseKey = "YOUR_ANON_KEY_HERE";
```

---

## ✅ Step 5: Update Android App

### 5.1 Update NetworkModule

File: `app/src/main/java/com/agrifarm/app/di/NetworkModule.kt`

```kotlin
@Provides
@Singleton
fun provideSensorApi(): SensorApi {
    return Retrofit.Builder()
        .baseUrl("https://YOUR_PROJECT.supabase.co/")  // ← Change this
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SensorApi::class.java)
}
```

### 5.2 Add Supabase Headers Interceptor

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

---

## ✅ Step 6: Test Database

### Test in Supabase SQL Editor:

```sql
-- Insert test data
INSERT INTO sensor_data (device_id, timestamp, temperature, humidity, soil_moisture, soil_moisture_pct, gas_level, rain_detected)
VALUES ('ESP32_FARM_001', 1234567890, 28.5, 65.2, 2450, 45, 1850, false);

-- Get latest data
SELECT * FROM get_latest_sensor_data('ESP32_FARM_001');

-- Check if online
SELECT is_device_online('ESP32_FARM_001');

-- Get 24h trends
SELECT * FROM get_24h_sensor_trends('ESP32_FARM_001');
```

---

## ✅ Step 7: Upload ESP32 & Run App

1. **Upload ESP32 code** with updated Supabase URL
2. **Open Serial Monitor** - verify data sending
3. **Run Android app**
4. **Go to "My Farm" tab**
5. **See**:
   - ✅ Device shows "Online" (green dot)
   - ✅ Real sensor readings
   - ✅ 24-hour trends with actual changes

---

## 📊 How It Works

```
ESP32 → Supabase (sensor_data table) → Android App
  ↓                    ↓                      ↓
Every 15s         Stores data           Polls every 5s
```

### Data Flow:
1. ESP32 sends JSON to Supabase REST API
2. Supabase stores in `sensor_data` table
3. Android app calls Supabase functions
4. Functions return latest data + trends
5. UI updates with real-time info

---

## 🎯 Features Now Working

### My Farm Tab:
- ✅ **Device Status**: Shows "Online" only when ESP32 sent data in last 5 minutes
- ✅ **Last Seen**: Shows when last data received
- ✅ **Real Sensor Data**: Live temperature, humidity, soil, gas, rain
- ✅ **24-Hour Trends**: Calculates actual changes from database
  - Soil Moisture: ↑ 5% or ↓ 3%
  - Temperature: ↑ 2°C or ↓ 1°C
  - Humidity: → Stable

---

## 🔧 Troubleshooting

### ESP32 not sending data:
- Check Serial Monitor for errors
- Verify Supabase URL and key
- Test with Postman first

### App shows "Offline":
- Check if ESP32 is running
- Verify last data in Supabase (SQL Editor: `SELECT * FROM sensor_data ORDER BY created_at DESC LIMIT 1`)
- Ensure data is < 5 minutes old

### No trends showing:
- Need at least 2 hours of data
- Check: `SELECT COUNT(*) FROM sensor_data WHERE created_at >= NOW() - INTERVAL '24 hours'`

---

## 🔐 Security (Production)

For production, update RLS policies:

```sql
-- Remove public access
DROP POLICY "Allow public read access" ON sensor_data;
DROP POLICY "Allow public insert access" ON sensor_data;

-- Add authenticated access only
CREATE POLICY "Authenticated read" ON sensor_data
    FOR SELECT USING (auth.role() = 'authenticated');

CREATE POLICY "Service role insert" ON sensor_data
    FOR INSERT WITH CHECK (auth.role() = 'service_role');
```

Then use service_role key for ESP32 (keep it secret!).

---

**Status: READY FOR TESTING** 🚀

Run SQL setup → Update keys → Upload ESP32 → Launch app!
