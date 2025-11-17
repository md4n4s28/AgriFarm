# 📡 ESP32 Data Monitoring Guide

## 🔍 Why Device Shows "Online" Without ESP32

The device shows "online" because you inserted **test data** in Step 3 of `SUPABASE_QUICK_START.sql`. The `is_device_online()` function checks if data was received in the last 5 minutes.

**To see real ESP32 data, you need to:**
1. Update ESP32 code with correct Supabase endpoint
2. Upload code to ESP32
3. Monitor Serial output and Supabase table

---

## 🚨 CRITICAL: ESP32 Code Needs Update

### Current Issue:
**Line 19** in ESP32 code:
```cpp
String supabaseURL = "https://mxvpaakfigpnorepgmkw.supabase.co/functions/v1/sensor-data";
```

**This endpoint doesn't exist!** You need to change it to the REST API endpoint.

### ✅ Fix Required:

**Update Line 19 to:**
```cpp
String supabaseURL = "https://mxvpaakfigpnorepgmkw.supabase.co/rest/v1/iot_sensor_data";
```

### ✅ Update sendToServer() Function:

Replace the entire `sendToServer()` function (around line 320) with:

```cpp
void sendToServer() {
  if (!wifiOK) {
    Serial.println("No WiFi - skipping send");
    return;
  }

  HTTPClient http;
  
  // Get device UUID from database (you got this from SUPABASE_QUICK_START.sql Step 2)
  String deviceUUID = "YOUR_DEVICE_UUID_HERE";  // ← PASTE UUID FROM SUPABASE
  
  // Create JSON payload for Supabase iot_sensor_data table
  StaticJsonDocument<512> doc;
  doc["device_id"] = deviceUUID;  // Use UUID, not string
  doc["temperature"] = dhtOK ? tempC : JsonVariant();
  doc["humidity"] = dhtOK ? hum : JsonVariant();
  doc["soil_moisture"] = soilOK ? soilAnalog : JsonVariant();
  doc["gas_level"] = gasOK ? gasVal : JsonVariant();
  doc["rain_detected"] = rainOK ? (rainDO == LOW) : false;
  
  String jsonString;
  serializeJson(doc, jsonString);
  
  Serial.println("=== SENDING TO SUPABASE ===");
  Serial.println(jsonString);

  http.begin(supabaseURL);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("apikey", supabaseKey);
  http.addHeader("Authorization", "Bearer " + supabaseKey);
  http.addHeader("Prefer", "return=minimal");
  
  int code = http.POST(jsonString);
  
  Serial.print("Response Code: ");
  Serial.println(code);
  
  if (code > 0) {
    String response = http.getString();
    Serial.println("Response: " + response);
    
    if (code == 201) {
      Serial.println("✓ Data saved successfully!");
    }
  } else {
    Serial.println("✗ HTTP Error: " + String(code));
  }
  
  http.end();
}
```

---

## 📊 Where to Monitor ESP32 Data

### 1. **Serial Monitor** (Real-time ESP32 output)

**Open:** Arduino IDE → Tools → Serial Monitor (115200 baud)

**You'll see:**
```
WiFi Connected: 192.168.1.100
AgriFarm IoT System Ready

=== SENDING TO SUPABASE ===
{"device_id":"abc-123-uuid","temperature":28.5,"humidity":65.2,...}
Response Code: 201
Response: 
✓ Data saved successfully!
```

**Response Codes:**
- `201` ✅ Success - Data inserted
- `400` ❌ Bad Request - Check JSON format
- `401` ❌ Unauthorized - Check API key
- `404` ❌ Not Found - Check URL
- `409` ❌ Conflict - Duplicate data

### 2. **Supabase Table Editor** (View stored data)

**Steps:**
1. Go to Supabase Dashboard
2. Click **Table Editor** (left sidebar)
3. Select **iot_sensor_data** table
4. Click **Refresh** button
5. Sort by **timestamp** (descending)

**You'll see:**
| id | device_id | temperature | humidity | soil_moisture | timestamp |
|----|-----------|-------------|----------|---------------|-----------|
| ... | abc-123 | 28.5 | 65.2 | 2450 | 2024-01-15... |

### 3. **Supabase SQL Editor** (Query data)

**Run these queries:**

```sql
-- View latest 10 readings
SELECT * FROM public.iot_sensor_data 
ORDER BY timestamp DESC 
LIMIT 10;

-- Check device status
SELECT 
    d.device_id,
    d.status,
    d.last_sync,
    COUNT(sd.id) as total_readings
FROM public.iot_devices d
LEFT JOIN public.iot_sensor_data sd ON sd.device_id = d.id
WHERE d.device_id = 'ESP32_FARM_001'
GROUP BY d.device_id, d.status, d.last_sync;

-- Test if device is online
SELECT is_device_online('ESP32_FARM_001');

-- Get latest sensor data
SELECT * FROM get_latest_sensor_data('ESP32_FARM_001');
```

### 4. **Pipedream Webhook** (Fallback monitoring)

If Supabase fails, ESP32 sends to Pipedream webhook.

**View data:**
1. Go to https://pipedream.com
2. Find your webhook: `https://eob86s60nw99fn5.m.pipedream.net`
3. See all requests in real-time

---

## 🔧 Complete Setup Checklist

### Step 1: Get Device UUID from Supabase
```sql
-- Run this in Supabase SQL Editor
SELECT id, device_id FROM public.iot_devices WHERE device_id = 'ESP32_FARM_001';
```
**Copy the `id` (UUID)** - looks like: `abc-123-def-456-789`

### Step 2: Update ESP32 Code

**File:** `ESP32_AGRIFARM_INTEGRATED.ino`

**Line 19:**
```cpp
String supabaseURL = "https://mxvpaakfigpnorepgmkw.supabase.co/rest/v1/iot_sensor_data";
```

**Line 320 (in sendToServer function):**
```cpp
String deviceUUID = "abc-123-def-456-789";  // ← Paste your UUID here
```

### Step 3: Upload to ESP32

1. Connect ESP32 via USB
2. Select board: **ESP32 Dev Module**
3. Select port: **COM3** (or your port)
4. Click **Upload** ▶️
5. Wait for "Done uploading"

### Step 4: Open Serial Monitor

1. Tools → Serial Monitor
2. Set baud rate: **115200**
3. Watch for:
   ```
   WiFi Connected: 192.168.x.x
   === SENDING TO SUPABASE ===
   Response Code: 201
   ✓ Data saved successfully!
   ```

### Step 5: Verify in Supabase

**Table Editor:**
- Go to `iot_sensor_data` table
- Click refresh every 15 seconds
- See new rows appearing

**SQL Query:**
```sql
SELECT * FROM public.iot_sensor_data 
WHERE device_id = (SELECT id FROM public.iot_devices WHERE device_id = 'ESP32_FARM_001')
ORDER BY timestamp DESC 
LIMIT 5;
```

---

## 🐛 Troubleshooting

### ESP32 shows "Response Code: 404"
**Fix:** Update `supabaseURL` to `/rest/v1/iot_sensor_data`

### ESP32 shows "Response Code: 401"
**Fix:** Check `supabaseKey` is correct (line 20)

### ESP32 shows "Response Code: 400"
**Fix:** 
- Check `deviceUUID` is correct UUID (not "ESP32_FARM_001")
- Verify device exists: `SELECT * FROM iot_devices WHERE id = 'YOUR_UUID'`

### No data in Supabase table
**Check:**
1. Serial Monitor shows "Response Code: 201"?
2. Device UUID is correct?
3. RLS policies allow insert?

### Device still shows "offline" in app
**Reason:** Test data is older than 5 minutes
**Fix:** Wait for ESP32 to send real data, then check again

---

## 📈 Expected Data Flow

```
ESP32 (every 15s)
    ↓
    POST to Supabase REST API
    ↓
    iot_sensor_data table (new row)
    ↓
    Trigger: update_device_status()
    ↓
    iot_devices.status = 'online'
    ↓
    Android App (polls every 5s)
    ↓
    Calls: get_latest_sensor_data()
    ↓
    My Farm Tab shows "Online" 🟢
```

---

## ✅ Success Indicators

**ESP32 Serial Monitor:**
```
✓ Data saved successfully!
```

**Supabase Table:**
```
New rows appearing every 15 seconds
```

**Android App:**
```
Device: Online 🟢
Last seen: Just now
Temperature: 28.5°C
Humidity: 65.2%
```

---

**Ready to test!** 🚀

Update the code → Upload → Monitor Serial → Check Supabase → See data in app!
