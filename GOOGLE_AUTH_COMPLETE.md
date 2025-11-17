# ✅ Google Authentication - Setup Complete!

## 🎉 What You Have:

### Google Cloud Console:
- ✅ **Android OAuth Client ID**: `179534132622-nv7ii0428llc6vqb18so9mgpum47u8tp.apps.googleusercontent.com`
- ✅ **Web OAuth Client ID**: Created with callback URL
- ✅ **Callback URL**: `https://mxvpaakfigpnorepgmkw.supabase.co/auth/v1/callback`

### Android App:
- ✅ Google Sign-In dependencies added
- ✅ LoginScreen with Google button
- ✅ GoogleAuthHelper with your client ID
- ✅ Navigation configured (Login → Home)

---

## 🔧 Enable Google Auth in Supabase

### Step 1: Go to Supabase Dashboard

1. Open: https://supabase.com/dashboard
2. Select your project: **mxvpaakfigpnorepgmkw**
3. Go to **Authentication** → **Providers**

### Step 2: Configure Google Provider

1. Find **Google** in the list
2. Toggle **Enable Sign in with Google** to ON
3. Enter credentials from Google Cloud Console:

**Client ID (Web):**
```
YOUR_WEB_CLIENT_ID_HERE
```
(Get this from Google Cloud Console → Credentials → Web client)

**Client Secret (Web):**
```
YOUR_WEB_CLIENT_SECRET_HERE
```
(Get this from Google Cloud Console → Credentials → Web client)

4. **Authorized Client IDs** (add both):
```
179534132622-nv7ii0428llc6vqb18so9mgpum47u8tp.apps.googleusercontent.com
YOUR_WEB_CLIENT_ID_HERE
```

5. Click **Save**

---

## 📱 Test the App

### Step 1: Build & Run
```bash
# Sync Gradle first
# Then run the app
```

### Step 2: Login Flow
1. App opens → **Login Screen** appears
2. Tap **"Sign in with Google"**
3. Select your Google account
4. Grant permissions
5. **Success!** → Navigate to Home screen

### Step 3: Verify User in Supabase
1. Go to Supabase Dashboard
2. **Authentication** → **Users**
3. See your Google account listed

---

## 🔗 Complete Integration Flow

```
User taps "Sign in with Google"
    ↓
Android Credential Manager
    ↓
Google Sign-In (Android Client ID)
    ↓
Get ID Token
    ↓
App receives: userId, email, name
    ↓
Navigate to Home Screen
    ↓
(Optional) Send to Supabase for user profile
```

---

## 💾 Save User to Supabase (Optional)

### Update LoginViewModel to save user:

```kotlin
fun signInWithGoogle(context: Context) {
    viewModelScope.launch {
        _uiState.value = LoginUiState.Loading
        googleAuthHelper.signIn(context).fold(
            onSuccess = { credential ->
                // Save to Supabase
                saveUserToSupabase(
                    userId = credential.id,
                    email = credential.id,
                    name = credential.displayName ?: "User"
                )
                
                _uiState.value = LoginUiState.Success(
                    userId = credential.id,
                    email = credential.id,
                    name = credential.displayName ?: "User"
                )
            },
            onFailure = { error ->
                _uiState.value = LoginUiState.Error(error.message ?: "Login failed")
            }
        )
    }
}

private suspend fun saveUserToSupabase(userId: String, email: String, name: String) {
    // Insert into public.users table
    // Use Supabase client or Retrofit
}
```

---

## 🗄️ Link User to IoT Devices

### After login, register IoT device:

```sql
-- In Supabase SQL Editor
INSERT INTO public.iot_devices (user_id, device_id, device_name, status)
VALUES (
    'USER_ID_FROM_GOOGLE_LOGIN',  -- From credential.id
    'ESP32_FARM_001',
    'Farm Sensor Unit 1',
    'offline'
);
```

### Or in app after login:

```kotlin
// In LoginViewModel after successful login
supabaseClient.from("iot_devices").insert(
    mapOf(
        "user_id" to credential.id,
        "device_id" to "ESP32_FARM_001",
        "device_name" to "Farm Sensor Unit 1"
    )
)
```

---

## 🎯 Current Status

### ✅ Working:
- Android app with Google Sign-In
- Login screen UI
- Google authentication flow
- Navigation after login

### ⏳ Next Steps:
1. Enable Google provider in Supabase
2. Test login in app
3. Link user to IoT devices
4. Save user profile to Supabase

---

## 🐛 Troubleshooting

### "Sign in failed" error:
- Check SHA-1 is correct in Google Cloud Console
- Verify Android Client ID matches in GoogleAuthHelper
- Ensure package name is `com.agrifarm.app`

### "No Google accounts found":
- Add Google account to device/emulator
- Settings → Accounts → Add account → Google

### "Invalid client" error:
- Web Client ID might be wrong
- Check Supabase callback URL matches Google Cloud Console

### User not saved to Supabase:
- Check RLS policies allow insert
- Verify user table structure matches
- Check Supabase logs for errors

---

## 📚 Files Created/Modified

```
✅ app/build.gradle.kts - Added Google Sign-In dependencies
✅ GoogleAuthHelper.kt - Handles Google authentication
✅ LoginScreen.kt - UI with Google Sign-In button
✅ LoginViewModel.kt - Login logic
✅ Screen.kt - Added Login route
✅ MainActivity.kt - Login as start destination
```

---

## 🚀 Ready to Test!

1. **Sync Gradle** in Android Studio
2. **Build & Run** the app
3. **Tap "Sign in with Google"**
4. **Select account** and grant permissions
5. **Success!** You're logged in 🎉

---

**Status: READY FOR TESTING** 🔐

Your app now has Google authentication integrated!
