package com.agrifarm.app.data.auth

import android.content.Context
import android.content.Intent
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthHelper @Inject constructor() {
    
    private val webClientId = "179534132622-l73hbisrs6p1ksqp19g8roi8btls6642.apps.googleusercontent.com"
    
    fun getSignInIntent(context: Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .requestProfile()
            .build()
        val client = GoogleSignIn.getClient(context, gso)
        return client.signInIntent
    }
    
    suspend fun handleSignInResult(data: Intent?): Result<GoogleSignInAccount> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            Result.success(account)
        } catch (e: ApiException) {
            android.util.Log.e("GoogleAuth", "Sign in failed: ${e.statusCode}", e)
            Result.failure(Exception("Sign in failed: ${e.message}"))
        } catch (e: Exception) {
            android.util.Log.e("GoogleAuth", "Sign in failed", e)
            Result.failure(e)
        }
    }
    
    suspend fun signIn(context: Context): Result<GoogleIdTokenCredential> {
        return try {
            val credentialManager = CredentialManager.create(context)
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .setNonce(System.currentTimeMillis().toString())
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            val result = credentialManager.getCredential(context, request)
            val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
            
            Result.success(credential)
        } catch (e: androidx.credentials.exceptions.NoCredentialException) {
            android.util.Log.e("GoogleAuth", "No Google account found. Please add a Google account in Settings.", e)
            Result.failure(Exception("No Google account found. Please add a Google account in device Settings → Accounts."))
        } catch (e: Exception) {
            android.util.Log.e("GoogleAuth", "Sign in failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
