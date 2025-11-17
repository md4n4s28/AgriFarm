# 🎤 AI Voice Chat Setup Guide

## Features
- **Multilingual Support**: 11+ Indian languages including Hindi, Punjabi, Garhwali, Tamil, Telugu, Bengali, Marathi, Gujarati, Kannada, Malayalam, Odia
- **Speech-to-Text**: Converts voice to text in any supported language
- **AI Response**: Google Gemini AI responds in the same language as the question
- **Text-to-Speech**: AI speaks the response back to you
- **Real-time Chat**: See conversation history with timestamps

## Setup Instructions

### 1. Get Gemini API Key
1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Click "Create API Key"
3. Copy your API key

### 2. Add API Key to .env File
Open `.env` file in project root and add:
```bash
GEMINI_API_KEY=AIzaSy...
```

**Note**: The API key is already configured in your `.env` file. Never commit this file to Git!

### 3. Request Permissions at Runtime
Add this to your MainActivity or create a permission handler:
```kotlin
// Request microphone permission
if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
    != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this, 
        arrayOf(Manifest.permission.RECORD_AUDIO), 
        REQUEST_RECORD_AUDIO_PERMISSION)
}
```

### 4. Build and Run
```bash
./gradlew assembleDebug
```

## Supported Languages

| Language | Code | Native Name |
|----------|------|-------------|
| English | en | English |
| Hindi | hi | हिंदी |
| Punjabi | pa | ਪੰਜਾਬੀ |
| Tamil | ta | தமிழ் |
| Telugu | te | తెలుగు |
| Bengali | bn | বাংলা |
| Marathi | mr | मराठी |
| Gujarati | gu | ગુજરાતી |
| Kannada | kn | ಕನ್ನಡ |
| Malayalam | ml | മലയാളം |
| Odia | or | ଓଡ଼ିଆ |

## How to Use

1. **Open Voice Chat**: Tap the "AI Voice Assistant" card on the home screen
2. **Select Language**: Tap the language icon in the top bar
3. **Start Speaking**: Tap the microphone button and speak your question
4. **Listen to Response**: AI will respond in the same language and speak it back

## Example Questions

### English
- "What is the best time to plant wheat?"
- "How do I control pests in my tomato farm?"

### Hindi (हिंदी)
- "गेहूं बोने का सबसे अच्छा समय क्या है?"
- "टमाटर में कीट नियंत्रण कैसे करें?"

### Punjabi (ਪੰਜਾਬੀ)
- "ਕਣਕ ਬੀਜਣ ਦਾ ਸਭ ਤੋਂ ਵਧੀਆ ਸਮਾਂ ਕੀ ਹੈ?"

### Tamil (தமிழ்)
- "கோதுமை விதைக்க சிறந்த நேரம் எது?"

## Technical Details

### Architecture
- **ViewModel**: `VoiceChatViewModel` - Manages state and business logic
- **UI**: `VoiceChatScreen` - Compose UI with chat bubbles
- **API**: `GeminiApiService` - Retrofit service for Gemini AI
- **DI**: Hilt for dependency injection

### APIs Used
- **Google Gemini Pro**: AI responses
- **Android SpeechRecognizer**: Speech-to-text
- **Android TextToSpeech**: Text-to-speech

### Permissions Required
- `INTERNET` - For API calls
- `RECORD_AUDIO` - For voice input

## Troubleshooting

### No Response from AI
- Check your API key is valid
- Ensure internet connection is active
- Check Logcat for error messages

### Speech Recognition Not Working
- Grant microphone permission
- Check device has Google Speech Services installed
- Ensure selected language is supported by device

### TTS Not Speaking
- Check device TTS settings
- Install language data for selected language
- Go to Settings > Language & Input > Text-to-speech

## Future Enhancements
- Offline mode with local AI models
- Voice commands for app navigation
- Crop disease detection via voice description
- Market price queries via voice
- Weather forecast queries

---

**Built with ❤️ for Indian Farmers**
