package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class GoogleAuthResult {
    data class Success(
        val firebaseUid: String,
        val email: String,
        val displayName: String,
        val idToken: String
    ) : GoogleAuthResult()

    data class Cancelled(val message: String = "Login Google dibatalkan pengguna.") : GoogleAuthResult()
    data class ClientNotConfigured(val message: String) : GoogleAuthResult()
    data class Error(val message: String) : GoogleAuthResult()
}

class GoogleSignInHelper(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    fun getWebClientId(): String {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        val fromResources = if (resId != 0) {
            try { context.getString(resId) } catch (_: Exception) { "" }
        } else ""

        val fromEnv = System.getenv("GOOGLE_WEB_CLIENT_ID") ?: ""

        // Primary: resource or env var; Fallback: genuine client_type 3 Web Client ID from google-services.json
        return fromResources.ifBlank {
            fromEnv.ifBlank {
                "370424798110-1bvllhd3aoqine1scbrou94clcr11ekf.apps.googleusercontent.com"
            }
        }
    }

    suspend fun signInWithGoogle(): GoogleAuthResult = withContext(Dispatchers.IO) {
        val webClientId = getWebClientId()
        if (webClientId.isBlank()) {
            return@withContext GoogleAuthResult.ClientNotConfigured(
                "GOOGLE_WEB_CLIENT_ID belum dikonfigurasi di secrets/environment atau google-services.json."
            )
        }

        val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val response: GetCredentialResponse = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val email = googleIdTokenCredential.id
                val displayName = googleIdTokenCredential.displayName ?: email.substringBefore("@")
                val idToken = googleIdTokenCredential.idToken

                // Authenticate with Firebase Authentication
                val firebaseAuth = FirebaseAuth.getInstance()
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                val firebaseUid = authResult.user?.uid ?: idToken.take(32)

                return@withContext GoogleAuthResult.Success(
                    firebaseUid = firebaseUid,
                    email = email,
                    displayName = displayName,
                    idToken = idToken
                )
            } else {
                return@withContext GoogleAuthResult.Error("Tipe kredensial tidak sesuai: ${credential.type}")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d("GoogleSignIn", "Cancelled by user")
            return@withContext GoogleAuthResult.Cancelled()
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignIn", "Credential Manager error: ${e.message}", e)
            return@withContext GoogleAuthResult.Error("Gagal otentikasi Google: ${e.message}")
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Unexpected error during Google Sign-In", e)
            return@withContext GoogleAuthResult.Error("Terjadi kesalahan sistem: ${e.message}")
        }
    }
}
