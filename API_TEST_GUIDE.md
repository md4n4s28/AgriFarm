# 🔍 Voice Chat Troubleshooting Guide

## Issue: Voice recognized but no AI response

### Quick Checks:

1. **Check Logcat for errors**:
   - Open Android Studio → Logcat
   - Filter by "VoiceChat"
   - Look for error messages

2. **Verify API Key**:
   ```bash
   # Check .env file
   GEMINI_API_KEY=AIzaSyByp7kEG19eERvMhsCDF3v-NdHWI9Zg1Es
   ```

3. **Test API Key manually**:
   ```bash
   curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=AIzaSyByp7kEG19eERvMhsCDF3v-NdHWI9Zg1Es" \
   -H 'Content-Type: application/json' \
   -d '{"contents":[{"parts":[{"text":"Hello"}]}]}'
   ```

### Common Errors:

#### Error: "API key not valid"
- Go to https://makersuite.google.com/app/apikey
- Create a new API key
- Update `.env` file

#### Error: "Network error" / "Unable to resolve host"
- Check internet connection
- Check if device/emulator has internet access
- Try: Settings → Wi-Fi → Forget network → Reconnect

#### Error: "403 Forbidden"
- API key might be restricted
- Go to Google Cloud Console
- Enable "Generative Language API"

#### Error: "429 Too Many Requests"
- You've hit the rate limit
- Wait a few minutes
- Free tier: 60 requests per minute

### Debug Steps:

1. **Run the app**
2. **Open Logcat** (View → Tool Windows → Logcat)
3. **Speak something**
4. **Look for**:
   ```
   VoiceChat: Gemini API Error
   ```
5. **Check the full error message**

### Expected Logcat Output (Success):
```
D/OkHttp: --> POST https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=...
D/OkHttp: {"contents":[{"parts":[{"text":"..."}]}]}
D/OkHttp: <-- 200 OK
```

### Expected Logcat Output (Error):
```
E/VoiceChat: Gemini API Error
    java.net.UnknownHostException: Unable to resolve host
```

### Quick Fix:

If you see the error in chat, it will show:
```
Error: <actual error message>
```

This tells you exactly what went wrong!

---

## Alternative: Use a simpler test

Add this to test if API works:

```kotlin
// In VoiceChatViewModel, add:
fun testAPI() {
    viewModelScope.launch {
        try {
            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part("Say hello"))))
            )
            val response = geminiService.generateContent(geminiApiKey, request)
            android.util.Log.d("VoiceChat", "API Works! Response: ${response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text}")
        } catch (e: Exception) {
            android.util.Log.e("VoiceChat", "API Failed!", e)
        }
    }
}
```

Call this from the screen to test.
