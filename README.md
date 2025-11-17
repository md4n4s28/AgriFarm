# 🌾 AgriFarm - Smart Crop Recommendation App

**Smart India Hackathon 2025 - Problem Statement SIH25030**

AI-powered advisory platform for farmers integrating satellite imagery, IoT sensors, weather forecasts, and market data.

---

## 🚀 Quick Start

### 1. Clone & Setup
```bash
cd AgriFarmV001
cp .env.example .env
# Fill in your API keys in .env
```

### 2. Open in Android Studio
- Open project in Android Studio
- Wait for Gradle sync
- Click "Sync Now" if prompted

### 3. Configure Environment
- Add Supabase credentials to `.env`
- See `docs/ENV_SETUP_GUIDE.md` for details

### 4. Run
- Connect device or start emulator
- Click Run ▶️

---

## 📚 Documentation

- **[Progress Checklist](docs/PROGRESS_CHECKLIST.md)** - Track development progress
- **[Project Guide](docs/PROJECT_GUIDE.md)** - Complete development guide
- **[Environment Setup](docs/ENV_SETUP_GUIDE.md)** - Configure API keys

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

- ✅ Real-time IoT sensor monitoring
- ✅ AI-powered crop recommendations
- ✅ Market price predictions
- ✅ Disease detection via camera
- ✅ Weather forecasts
- ✅ Satellite imagery analysis
- ✅ Multi-language support

---

## 🔧 Project Structure

```
app/src/main/java/com/agrifarm/app/
├── data/           # Data layer (repositories, APIs)
├── domain/         # Business logic (models, use cases)
├── presentation/   # UI layer (screens, viewmodels)
├── di/             # Dependency injection
└── util/           # Utilities and constants
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

## 📱 Current Status

**Phase:** 1.1 - Configuration Setup ✅  
**Next:** 1.2 - Folder Structure & Theme

See [PROGRESS_CHECKLIST.md](docs/PROGRESS_CHECKLIST.md) for detailed status.

---

## 🔐 Security

- Never commit `.env` file
- Keep API keys secure
- Use `.env.example` to share structure

---

## 📞 Support

Check `docs/` folder for detailed guides and troubleshooting.

---

**Built with ❤️ by Team CodeRed for SIH 2025**
