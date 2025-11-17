# 🌾 AgriFarm - Smart Crop Recommendation App

**Smart India Hackathon 2025 - Problem Statement SIH25030**

AI-powered advisory platform for farmers with real-time IoT integration, Supabase backend, and zero local data storage.

---

## 🚀 Quick Start

### 1. Clone & Setup
```bash
cd AgriFarmV001
cp .env.example .env
# Fill in your API keys in .env
```

### 2. Complete Setup
- Follow **[SETUP.md](SETUP.md)** for complete instructions
- Setup Supabase database
- Configure environment variables
- Setup IoT (optional)

### 3. Run
- Open in Android Studio
- Wait for Gradle sync
- Connect device or start emulator
- Click Run ▶️

---

## 📚 Documentation

- **[SETUP.md](SETUP.md)** - Complete setup guide (Database + App + IoT)

---

## 🎨 Tech Stack

- **Frontend:** Jetpack Compose, Material 3
- **Backend:** Supabase (Auth, Database, Storage)
- **DI:** Hilt
- **Networking:** Retrofit, Ktor
- **IoT:** ESP32 with sensors (Soil, DHT11, Rain, Gas)
- **AI Models:** Crop detection, Price prediction, Disease detection

---

## 🎯 Features

- ✅ **Real-time Data**: All data syncs via Supabase Realtime
- ✅ **Zero Local Storage**: No Room, no SharedPreferences
- ✅ **Entity-Based ORM**: JPQL-style entities for fast table modifications
- ✅ **IoT Integration**: Live ESP32 sensor data streaming
- ✅ **AI-Powered**: Crop recommendations & disease detection
- ✅ **Market Intelligence**: Price predictions & trends
- ✅ **Weather Alerts**: Real-time notifications
- ✅ **Multi-language**: Support for regional languages

---

## 🔧 Project Structure

```
app/src/main/java/com/agrifarm/app/
├── data/
│   ├── api/              # External API services
│   ├── auth/             # Authentication
│   ├── database/         # ⭐ Supabase entities & database
│   │   ├── SupabaseEntities.kt
│   │   ├── SupabaseDatabase.kt
│   │   └── EntityMappers.kt
│   ├── model/            # Domain models
│   └── repository/       # Data repositories
├── di/                   # Dependency injection
├── domain/               # ViewModels
├── presentation/         # UI screens
└── util/                 # Utilities
```

---

## 🌈 Theme

- **Primary:** Green (#4CAF50)
- **Secondary:** Dark Green (#2E7D32)
- **Background:** White (#FFFFFF)
- **Text:** Black (#000000)

---

## 👥 Team CodeRed

**Problem Statement:** AI-Based Crop Recommendation for Farmers  
**Category:** Agriculture, FoodTech & Rural Development

---

## 🔄 Real-time Architecture

### Data Flow
```
ESP32 Sensors → Supabase Database → Android App (Real-time)
     ↑                    ↑                    ↓
  WiFi              Realtime API          UI Updates
```

### Key Benefits
- ⚡ Instant data synchronization
- 📱 Multi-device support
- ☁️ Cloud-first architecture
- 🔄 Automatic conflict resolution
- 📊 Live dashboard updates

---

## 🔐 Security

- Never commit `.env` file
- Keep API keys secure
- Use Row Level Security (RLS) in production
- Enable Supabase Auth policies

---

## 📊 Database Management

### Add New Table
1. Add entity in `SupabaseEntities.kt`
2. Add methods in `SupabaseDatabase.kt`
3. Add mapper in `EntityMappers.kt`
4. Run SQL in Supabase Dashboard

### Modify Existing Table
1. Update entity class
2. Run ALTER TABLE in Supabase
3. Update mappers if needed

---

## 📞 Support

Check **[SETUP.md](SETUP.md)** for complete setup instructions

---

**Built with ❤️ by Team CodeRed for SIH 2025**
