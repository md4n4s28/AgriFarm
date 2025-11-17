# 🔐 Environment Variables Setup Guide

## Quick Setup

### Step 1: Copy the example file
```bash
cp .env.example .env
```

### Step 2: Fill in your values

Open `.env` file and add your actual credentials:

```env
# Supabase (Get from supabase.com dashboard)
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# AI Model APIs (Your team's backend URLs)
AI_CROP_DETECTION_URL=http://192.168.1.100:8000/api/crop-detection
AI_PRICE_PREDICTION_URL=http://192.168.1.100:8000/api/price-prediction
AI_DISEASE_DETECTION_URL=http://192.168.1.100:8000/api/disease-detection
```

### Step 3: Sync Gradle

After adding values, click "Sync Now" in Android Studio.

---

## 📋 Where to Get Each API Key

### 1. Supabase
1. Go to [supabase.com](https://supabase.com)
2. Create project
3. Go to Settings → API
4. Copy URL and anon key

### 2. Weather API (OpenWeatherMap)
1. Go to [openweathermap.org](https://openweathermap.org/api)
2. Sign up for free account
3. Get API key from dashboard

### 3. AI Model URLs
- Ask your backend team for the deployed API URLs
- For local testing: `http://localhost:8000` or `http://192.168.x.x:8000`

### 4. MQTT Broker (For IoT)
- Free option: `broker.hivemq.com` (no auth needed)
- Or use your own MQTT broker

---

## 🔒 Security Notes

- ✅ `.env` is in `.gitignore` - won't be committed
- ✅ Never share your `.env` file
- ✅ Use `.env.example` to share structure with team
- ✅ Each team member creates their own `.env`

---

## 🧪 Testing Configuration

To verify your .env is loaded correctly:

1. Build the project
2. Check BuildConfig class has values
3. Run app and check logs

---

## 🆘 Troubleshooting

**Problem:** BuildConfig not found  
**Solution:** Sync Gradle, then Rebuild Project

**Problem:** API keys not loading  
**Solution:** Check .env file is in project root (same level as app folder)

**Problem:** Supabase connection failed  
**Solution:** Verify URL and key are correct, no extra spaces

---

## 📝 Current Configuration Status

- [ ] Supabase URL added
- [ ] Supabase Key added
- [ ] AI Model URLs added
- [ ] Weather API key added
- [ ] MQTT broker configured
- [ ] Tested and working

---

**Last Updated:** [Add date when you configure]
