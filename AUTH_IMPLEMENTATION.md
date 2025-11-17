# 🔐 Local Authentication Implementation

## Overview

Complete local authentication system with phone number + password, OTP verification, and forgot password functionality. All data stored in Supabase.

---

## 🗄️ Database Tables

### auth_users
```sql
- id (TEXT, PRIMARY KEY)
- phone (TEXT, UNIQUE)
- password (TEXT, SHA-256 hashed)
- name (TEXT)
- email (TEXT, optional)
- is_verified (BOOLEAN)
- created_at (TIMESTAMP)
```

### otp_codes
```sql
- id (UUID, PRIMARY KEY)
- phone (TEXT)
- otp (TEXT, 6-digit)
- expires_at (BIGINT, 5 minutes)
- is_used (BOOLEAN)
- created_at (TIMESTAMP)
```

---

## 🔄 Authentication Flow

### 1. Registration
```
User enters: Phone + Password + Name + Email (optional)
↓
Password hashed (SHA-256)
↓
User created in auth_users (is_verified = false)
↓
6-digit OTP generated and stored
↓
OTP sent to phone (SMS API integration needed)
↓
User enters OTP
↓
OTP verified → is_verified = true
↓
Login successful
```

### 2. Login
```
User enters: Phone + Password
↓
Password hashed and compared
↓
Check is_verified = true
↓
Login successful
```

### 3. Forgot Password
```
User enters: Phone
↓
Check user exists
↓
6-digit OTP generated and sent
↓
User enters: OTP + New Password
↓
OTP verified
↓
Password updated (hashed)
↓
Password reset successful
```

---

## 📱 Screens

### LoginScreen
- Phone number input
- Password input
- Login button
- "Forgot Password?" link
- "Register" link

### RegisterScreen
- Full name input
- Phone number input
- Email input (optional)
- Password input
- Confirm password input
- Register button
- "Already have account?" link

### OtpVerificationScreen
- OTP input (6 digits)
- Verify button
- Resend OTP button

### ForgotPasswordScreen
- Phone number input
- Send OTP button
- OTP input (after OTP sent)
- New password input
- Confirm password input
- Reset password button
- Resend OTP button

---

## 🔧 Implementation Files

### Entities
- `AuthEntities.kt` - AuthUserEntity, OtpEntity

### Database
- `SupabaseDatabase.kt` - Auth methods added

### Repository
- `AuthRepository.kt` - Business logic
  - register()
  - login()
  - sendOtp()
  - verifyOtp()
  - forgotPassword()
  - resetPassword()

### ViewModel
- `AuthViewModel.kt` - UI state management

### Screens
- `LoginScreen.kt`
- `RegisterScreen.kt`
- `OtpVerificationScreen.kt`
- `ForgotPasswordScreen.kt`

---

## 🔐 Security Features

### Password Hashing
```kotlin
SHA-256 hash algorithm
Never store plain text passwords
```

### OTP Security
```kotlin
6-digit random OTP
5-minute expiration
One-time use only
Marked as used after verification
```

### Phone Verification
```kotlin
Users must verify phone before login
OTP sent to registered phone number
```

---

## 📲 SMS Integration (TODO)

### Option 1: Twilio
```kotlin
// Add to build.gradle
implementation("com.twilio.sdk:twilio:9.2.0")

// In AuthRepository.sendOtp()
Twilio.init(ACCOUNT_SID, AUTH_TOKEN)
Message.creator(
    PhoneNumber("+91$phone"),
    PhoneNumber(TWILIO_NUMBER),
    "Your AgriFarm OTP: $otp"
).create()
```

### Option 2: MSG91
```kotlin
// Add to build.gradle
implementation("com.squareup.retrofit2:retrofit:2.9.0")

// Create MSG91 API service
interface Msg91Api {
    @GET("sendotp.php")
    suspend fun sendOtp(
        @Query("authkey") authKey: String,
        @Query("mobile") mobile: String,
        @Query("otp") otp: String
    )
}
```

### Option 3: Firebase Phone Auth
```kotlin
// Add to build.gradle
implementation("com.google.firebase:firebase-auth:22.3.0")

// Use Firebase Phone Authentication
PhoneAuthProvider.getInstance().verifyPhoneNumber(
    phoneNumber,
    60,
    TimeUnit.SECONDS,
    activity,
    callbacks
)
```

---

## 🧪 Testing

### Test Credentials
For development, OTP is logged to console:
```
Log.d("AuthRepository", "OTP for $phone: $otp")
```

### Test Flow
1. Register with phone: 9876543210
2. Check Logcat for OTP
3. Enter OTP to verify
4. Login with phone + password
5. Test forgot password flow

---

## 🚀 Usage Example

### In MainActivity or Navigation
```kotlin
NavHost(navController, startDestination = "login") {
    composable("login") {
        LoginScreen(
            onLoginSuccess = { userId, email, name ->
                navController.navigate("home")
            },
            onNavigateToRegister = {
                navController.navigate("register")
            },
            onNavigateToForgotPassword = {
                navController.navigate("forgot_password")
            }
        )
    }
    
    composable("register") {
        RegisterScreen(
            onNavigateToOtp = { phone ->
                navController.navigate("otp/$phone")
            },
            onNavigateToLogin = {
                navController.popBackStack()
            }
        )
    }
    
    composable("otp/{phone}") { backStackEntry ->
        val phone = backStackEntry.arguments?.getString("phone") ?: ""
        OtpVerificationScreen(
            phone = phone,
            onVerified = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
    
    composable("forgot_password") {
        ForgotPasswordScreen(
            onPasswordReset = {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}
```

---

## ✅ Features

- ✅ Phone + Password authentication
- ✅ SHA-256 password hashing
- ✅ OTP verification (6-digit)
- ✅ Phone number verification
- ✅ Forgot password with OTP
- ✅ Password reset
- ✅ Resend OTP functionality
- ✅ OTP expiration (5 minutes)
- ✅ One-time OTP usage
- ✅ All data in Supabase
- ✅ No local storage
- ✅ Real-time ready

---

## 🔄 Migration from Google Auth

### Old Flow
```
Google Sign-In → Google Account → User Profile
```

### New Flow
```
Phone + Password → OTP Verification → User Profile
```

### Benefits
- No dependency on Google services
- Works offline (after initial auth)
- More control over user data
- Better for Indian market (phone-first)
- SMS OTP is familiar to users

---

## 📝 Next Steps

1. **Integrate SMS API**
   - Choose provider (Twilio/MSG91/Firebase)
   - Add API credentials to `.env`
   - Implement in `AuthRepository.sendOtp()`

2. **Add Session Management**
   - Store auth token locally
   - Auto-login on app restart
   - Token refresh mechanism

3. **Add Profile Completion**
   - After OTP verification
   - Collect additional user info
   - Update user profile

4. **Add Security Enhancements**
   - Rate limiting for OTP
   - Account lockout after failed attempts
   - Two-factor authentication option

---

**Built with ❤️ by Team CodeRed for SIH 2025**
