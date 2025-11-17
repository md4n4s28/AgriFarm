# 🚀 AgriFarm Complete Setup Guide

## 📋 Prerequisites

- Android Studio (Latest)
- Supabase Account (Free)
- Google Account (for Firebase)

---

## 1️⃣ Supabase Database Setup

### Step 1: Create Supabase Project
1. Go to [supabase.com](https://supabase.com)
2. Click "New Project"
3. Enter project name: `agrifarm`
4. Set database password
5. Select region (closest to you)
6. Click "Create Project"

### Step 2: Run Database Schema
1. Go to **SQL Editor** in Supabase Dashboard
2. Copy and paste this complete SQL:

```sql
-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Auth Users Table
CREATE TABLE auth_users (
    id TEXT PRIMARY KEY DEFAULT 'user_' || EXTRACT(EPOCH FROM NOW())::TEXT,
    phone TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    name TEXT NOT NULL,
    email TEXT,
    is_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW()
);

-- OTP Codes Table
CREATE TABLE otp_codes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone TEXT NOT NULL,
    otp TEXT NOT NULL,
    expires_at BIGINT NOT NULL,
    is_used BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Users Table (for profile data)
CREATE TABLE users (
    id TEXT PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    phone TEXT,
    profile_url TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Sensor Data Table
CREATE TABLE sensor_data (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    temperature FLOAT NOT NULL,
    humidity FLOAT NOT NULL,
    soil_moisture INTEGER NOT NULL,
    soil_moisture_pct INTEGER NOT NULL,
    gas_level INTEGER NOT NULL,
    rain_detected BOOLEAN NOT NULL,
    rain_analog INTEGER NOT NULL,
    timestamp BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Crop Sessions Table
CREATE TABLE crop_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id TEXT NOT NULL,
    crop_name TEXT NOT NULL,
    crop_type TEXT NOT NULL,
    variety TEXT,
    land_area FLOAT NOT NULL,
    seeding_date BIGINT NOT NULL,
    expected_harvest_date BIGINT NOT NULL,
    current_stage TEXT DEFAULT 'SEEDING',
    health_status TEXT DEFAULT 'Good',
    expected_yield FLOAT DEFAULT 0,
    current_yield FLOAT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Schedules Table
CREATE TABLE schedules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id TEXT NOT NULL,
    type TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    scheduled_time BIGINT NOT NULL,
    is_completed BOOLEAN DEFAULT false,
    is_recurring BOOLEAN DEFAULT false,
    recurring_days INTEGER[],
    completed_at BIGINT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Pump Control Table
CREATE TABLE pump_control (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id TEXT NOT NULL UNIQUE,
    is_on BOOLEAN DEFAULT false,
    mode TEXT DEFAULT 'MANUAL',
    scheduled_start_time TEXT,
    scheduled_end_time TEXT,
    duration INTEGER DEFAULT 0,
    water_used FLOAT DEFAULT 0,
    last_run_time BIGINT,
    last_updated BIGINT DEFAULT EXTRACT(EPOCH FROM NOW()) * 1000
);

-- Weather Alerts Table
CREATE TABLE weather_alerts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id TEXT NOT NULL,
    type TEXT NOT NULL,
    severity TEXT NOT NULL,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    timestamp BIGINT NOT NULL,
    action_required TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Land Info Table
CREATE TABLE land_info (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id TEXT NOT NULL UNIQUE,
    total_area FLOAT NOT NULL,
    used_area FLOAT DEFAULT 0,
    available_area FLOAT,
    soil_type TEXT,
    ph_level FLOAT,
    location TEXT,
    latitude FLOAT,
    longitude FLOAT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Resource Usage Table
CREATE TABLE resource_usage (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id TEXT NOT NULL,
    session_id TEXT NOT NULL,
    resource_type TEXT NOT NULL,
    used FLOAT DEFAULT 0,
    planned FLOAT DEFAULT 0,
    remaining FLOAT DEFAULT 0,
    cost FLOAT DEFAULT 0,
    unit TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Crop Suggestions Table
CREATE TABLE crop_suggestions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    crop_name TEXT NOT NULL,
    soil_type TEXT NOT NULL,
    suitability_score FLOAT NOT NULL,
    expected_yield FLOAT,
    water_requirement FLOAT,
    fertilizer_requirement FLOAT,
    seed_cost FLOAT,
    total_investment FLOAT,
    expected_revenue FLOAT,
    profit_margin FLOAT,
    growth_duration INTEGER,
    difficulty TEXT,
    market_demand TEXT
);

-- Crop Comparisons Table
CREATE TABLE crop_comparisons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    crop_name TEXT NOT NULL,
    water_usage FLOAT,
    fertilizer_usage FLOAT,
    seed_cost FLOAT,
    labor_cost FLOAT,
    total_cost FLOAT,
    expected_revenue FLOAT,
    profit FLOAT,
    roi FLOAT
);

-- Enable Row Level Security
ALTER TABLE auth_users ENABLE ROW LEVEL SECURITY;
ALTER TABLE otp_codes ENABLE ROW LEVEL SECURITY;
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE sensor_data ENABLE ROW LEVEL SECURITY;
ALTER TABLE crop_sessions ENABLE ROW LEVEL SECURITY;
ALTER TABLE schedules ENABLE ROW LEVEL SECURITY;
ALTER TABLE pump_control ENABLE ROW LEVEL SECURITY;
ALTER TABLE weather_alerts ENABLE ROW LEVEL SECURITY;
ALTER TABLE land_info ENABLE ROW LEVEL SECURITY;
ALTER TABLE resource_usage ENABLE ROW LEVEL SECURITY;
ALTER TABLE crop_suggestions ENABLE ROW LEVEL SECURITY;
ALTER TABLE crop_comparisons ENABLE ROW LEVEL SECURITY;

-- RLS Policies
CREATE POLICY "Allow all" ON auth_users FOR ALL USING (true);
CREATE POLICY "Allow all" ON otp_codes FOR ALL USING (true);
CREATE POLICY "Allow all" ON users FOR ALL USING (true);
CREATE POLICY "Allow all" ON sensor_data FOR ALL USING (true);
CREATE POLICY "Allow all" ON crop_sessions FOR ALL USING (true);
CREATE POLICY "Allow all" ON schedules FOR ALL USING (true);
CREATE POLICY "Allow all" ON pump_control FOR ALL USING (true);
CREATE POLICY "Allow all" ON weather_alerts FOR ALL USING (true);
CREATE POLICY "Allow all" ON land_info FOR ALL USING (true);
CREATE POLICY "Allow all" ON resource_usage FOR ALL USING (true);
CREATE POLICY "Allow all" ON crop_suggestions FOR ALL USING (true);
CREATE POLICY "Allow all" ON crop_comparisons FOR ALL USING (true);

-- Indexes
CREATE INDEX idx_auth_users_phone ON auth_users(phone);
CREATE INDEX idx_otp_codes_phone ON otp_codes(phone);
CREATE INDEX idx_sensor_data_user_id ON sensor_data(user_id);
CREATE INDEX idx_sensor_data_timestamp ON sensor_data(timestamp DESC);
CREATE INDEX idx_crop_sessions_user_id ON crop_sessions(user_id);
CREATE INDEX idx_schedules_user_id ON schedules(user_id);
CREATE INDEX idx_weather_alerts_user_id ON weather_alerts(user_id);

-- Sample Data
INSERT INTO crop_suggestions (crop_name, soil_type, suitability_score, expected_yield, water_requirement, fertilizer_requirement, seed_cost, total_investment, expected_revenue, profit_margin, growth_duration, difficulty, market_demand)
VALUES 
('Rice', 'Loamy', 92, 4500, 15000, 250, 8000, 45000, 180000, 135000, 120, 'Medium', 'High'),
('Wheat', 'Loamy', 90, 3500, 10000, 200, 6000, 35000, 140000, 105000, 120, 'Easy', 'High'),
('Maize', 'Loamy', 88, 3800, 8000, 180, 6000, 35000, 140000, 105000, 90, 'Easy', 'High'),
('Cotton', 'Black', 85, 2500, 12000, 300, 10000, 50000, 200000, 150000, 150, 'Hard', 'High');

INSERT INTO crop_comparisons (crop_name, water_usage, fertilizer_usage, seed_cost, labor_cost, total_cost, expected_revenue, profit, roi)
VALUES 
('Wheat', 200000, 250, 8000, 20000, 45000, 165000, 120000, 267),
('Rice', 300000, 350, 10000, 25000, 60000, 220000, 160000, 267),
('Maize', 150000, 180, 6000, 15000, 35000, 140000, 105000, 300);
```

3. Click **Run** or press `Ctrl+Enter`
4. Wait for "Success" message

### Step 3: Enable Realtime
1. Go to **Database** → **Replication**
2. Enable Realtime for these tables:
   - sensor_data
   - crop_sessions
   - schedules
   - pump_control
   - weather_alerts

### Step 4: Get API Credentials
1. Go to **Settings** → **API**
2. Copy **Project URL** (e.g., `https://xxx.supabase.co`)
3. Copy **anon public** key

---

## 2️⃣ Android App Setup

### Step 1: Configure Environment
1. Open project in Android Studio
2. Create `.env` file in root directory:

```env
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key-here
GROQ_API_KEY=your-groq-key-here
```

3. Replace with your actual credentials

### Step 2: Update NetworkModule
1. Open `app/src/main/java/com/agrifarm/app/di/NetworkModule.kt`
2. Replace placeholder URLs with your Supabase credentials:

```kotlin
fun provideSupabaseClient(): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = "YOUR_SUPABASE_URL",
        supabaseKey = "YOUR_SUPABASE_KEY"
    ) {
        install(Postgrest)
        install(Realtime)
    }
}
```



### Step 4: Get Groq API Key
1. Go to [Groq Console](https://console.groq.com)
2. Create API key
3. Add to `.env` file

---

## 3️⃣ ESP32 IoT Setup (Optional)

### Hardware Required
- ESP32 DevKit
- DHT11 (Temperature/Humidity)
- Soil Moisture Sensor
- MQ-2 Gas Sensor
- Rain Sensor

### Arduino Code Setup
1. Open `ESP32_AGRIFARM_INTEGRATED/ESP32_AGRIFARM_INTEGRATED.ino`
2. Update WiFi credentials:
   ```cpp
   const char* ssid = "YOUR_WIFI_SSID";
   const char* password = "YOUR_WIFI_PASSWORD";
   ```
3. Update Supabase credentials:
   ```cpp
   const char* supabaseUrl = "https://your-project.supabase.co";
   const char* supabaseKey = "your-anon-key";
   const char* userId = "default_user";
   ```
4. Upload to ESP32

---

## 4️⃣ Build & Run

### Step 1: Sync Gradle
```bash
File → Sync Project with Gradle Files
```

### Step 2: Build
```bash
Build → Rebuild Project
```

### Step 3: Run
1. Connect Android device or start emulator
2. Click Run ▶️

---

## ✅ Verification

### Check Database
1. Go to Supabase → Table Editor
2. Verify all 10 tables exist
3. Check sample data in `crop_suggestions`

### Check App
1. Login with Google or Guest
2. Navigate to IoT Monitor
3. Check real-time data updates

### Check ESP32 (if connected)
1. Open Serial Monitor (115200 baud)
2. Verify WiFi connection
3. Check data sending to Supabase

---

## 🔧 Troubleshooting

### Gradle Sync Failed
```bash
File → Invalidate Caches → Restart
```

### Supabase Connection Error
- Verify `.env` file exists
- Check SUPABASE_URL and SUPABASE_ANON_KEY
- Test in Supabase Dashboard

### Build Error
```bash
./gradlew clean
./gradlew build
```

### Real-time Not Working
- Enable Realtime in Supabase Dashboard
- Check RLS policies
- Verify table replication enabled

---

## 📊 Database Tables Summary

| Table | Purpose | Real-time |
|-------|---------|-----------|  
| auth_users | Authentication | ❌ |
| otp_codes | OTP verification | ❌ |
| users | User profiles | ❌ |
| sensor_data | IoT readings | ✅ |
| crop_sessions | Crop tracking | ✅ |
| schedules | Farm tasks | ✅ |
| pump_control | Pump automation | ✅ |
| weather_alerts | Weather notifications | ✅ |
| land_info | Farm details | ❌ |
| resource_usage | Resource tracking | ❌ |
| crop_suggestions | AI recommendations | ❌ |
| crop_comparisons | Crop analytics | ❌ |

---

## 🎯 Quick Links

- [Supabase Dashboard](https://supabase.com/dashboard)
- [Groq Console](https://console.groq.com)

---

**Setup Complete! 🎉**

**Built with ❤️ by Team CodeRed for SIH 2025**
