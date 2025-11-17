# 🔐 Google Authentication Setup Guide

## Part 1: Supabase Configuration

### Step 1: Enable Google Auth in Supabase

1. Go to **Authentication** → **Providers**
2. Find **Google** and click to expand
3. Toggle **Enable Sign in with Google** to ON
4. Keep this page open - you'll need to add credentials

### Step 2: Create Google OAuth Credentials

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create new project or select existing: **AgriFarm**
3. Go to **APIs & Services** → **Credentials**
4. Click **+ CREATE CREDENTIALS** → **OAuth client ID**
5. If prompted, configure OAuth consent screen:
   - User Type: **External**
   - App name: **AgriFarm**
   - User support email: Your email
   - Developer contact: Your email
   - Click **Save and Continue** through all steps

### Step 3: Create OAuth Client IDs

#### For Web (Supabase):
1. Application type: **Web application**
2. Name: **AgriFarm Supabase**
3. Authorized redirect URIs:
   ```
   https://YOUR_PROJECT.supabase.co/auth/v1/callback
   ```
   (Get this from Supabase Auth → Providers → Google → Callback URL)
4. Click **CREATE**
5. Copy **Client ID** and **Client Secret**
6. Paste into Supabase Google provider settings
7. Click **Save**

#### For Android:
1. Click **+ CREATE CREDENTIALS** → **OAuth client ID** again
2. Application type: **Android**
3. Name: **AgriFarm Android**
4. Package name: `com.agrifarm.app`
5. Get SHA-1 certificate fingerprint:
   ```bash
   # For debug keystore
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   
   # For Windows
   keytool -list -v -keystore "C:\Users\YOUR_USERNAME\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
   ```
6. Copy the SHA-1 fingerprint and paste it
7. Click **CREATE**
8. Copy the **Client ID** (you'll need this for Android app)

---

## Part 2: Android App Integration

### Step 1: Add Dependencies

Add to `app/build.gradle.kts`:

```kotlin
dependencies {
    // Supabase
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.0.0")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.0.0")
    implementation("io.ktor:ktor-client-android:2.3.7")
    
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    
    // Credential Manager (modern approach)
    implementation("androidx.credentials:credentials:1.2.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")
}
```

### Step 2: Create Supabase Client

Create `app/src/main/java/com/agrifarm/app/data/auth/SupabaseClient.kt`:

```kotlin
package com.agrifarm.app.data.auth

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://YOUR_PROJECT.supabase.co",
        supabaseKey = "YOUR_ANON_KEY"
    ) {
        install(Auth)
        install(Postgrest)
    }
}
```

### Step 3: Create Auth Repository

Create `app/src/main/java/com/agrifarm/app/data/repository/AuthRepository.kt`:

```kotlin
package com.agrifarm.app.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import com.agrifarm.app.data.auth.SupabaseClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    
    private val supabase = SupabaseClient.client
    
    suspend fun signInWithGoogle(context: Context): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("YOUR_WEB_CLIENT_ID.apps.googleusercontent.com")
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            val result = credentialManager.getCredential(context, request)
            val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
            
            supabase.auth.signInWith(IDToken) {
                idToken = credential.idToken
                provider = Google
            }
            
            val userId = supabase.auth.currentUserOrNull()?.id ?: ""
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut() {
        supabase.auth.signOut()
    }
    
    fun getCurrentUserId(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }
    
    fun isSignedIn(): Boolean {
        return supabase.auth.currentUserOrNull() != null
    }
}
```

### Step 4: Create Login Screen

Create `app/src/main/java/com/agrifarm/app/presentation/auth/LoginScreen.kt`:

```kotlin
package com.agrifarm.app.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Agriculture,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "AgriFarm",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            
            Text(
                "Smart Crop Recommendation",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            when (val state = uiState) {
                is LoginUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is LoginUiState.Error -> {
                    Text(
                        state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    GoogleSignInButton {
                        viewModel.signInWithGoogle(context)
                    }
                }
                else -> {
                    GoogleSignInButton {
                        viewModel.signInWithGoogle(context)
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Sign in with Google")
    }
}
```

### Step 5: Create Login ViewModel

Create `app/src/main/java/com/agrifarm/app/presentation/auth/LoginViewModel.kt`:

```kotlin
package com.agrifarm.app.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrifarm.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState
    
    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            authRepository.signInWithGoogle(context).fold(
                onSuccess = { userId ->
                    _uiState.value = LoginUiState.Success(userId)
                },
                onFailure = { error ->
                    _uiState.value = LoginUiState.Error(error.message ?: "Login failed")
                }
            )
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val userId: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
```

### Step 6: Update Navigation

Update `MainActivity.kt` to add login screen:

```kotlin
// Add to navigation
composable(Screen.Login.route) {
    LoginScreen(onLoginSuccess = {
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Login.route) { inclusive = true }
        }
    })
}

// Set startDestination based on auth
val startDestination = if (authRepository.isSignedIn()) {
    Screen.Home.route
} else {
    Screen.Login.route
}
```

---

## Part 3: Testing

### Test Flow:
1. **Build and run** app
2. **Login screen** appears
3. **Tap "Sign in with Google"**
4. **Select Google account**
5. **App navigates to home**
6. **User ID** is now available for IoT device registration

### Get User ID:
```kotlin
val userId = authRepository.getCurrentUserId()
// Use this to register IoT devices
```

---

## Quick Start (Without Auth for Now)

To test IoT immediately without auth:

1. Run `SUPABASE_QUICK_START.sql` to create test user
2. Copy the generated UUIDs
3. Update ESP32 with device UUID
4. Test IoT functionality
5. Add Google Auth later

---

**Status: Ready for Integration** 🔐
