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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class GoogleAuthResult {
    data class Success(
        val googleId: String,
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
    private val webClientId: String = System.getenv("GOOGLE_WEB_CLIENT_ID") ?: ""

    suspend fun signInWithGoogle(): GoogleAuthResult = withContext(Dispatchers.IO) {
        if (webClientId.isBlank()) {
            return@withContext GoogleAuthResult.ClientNotConfigured(
                "GOOGLE_WEB_CLIENT_ID belum dikonfigurasi di secrets/environment. " +
                "Untuk mengaktifkan Google OAuth resmi, tambahkan Web Client ID dari Google Cloud Console."
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
                val googleId = googleIdTokenCredential.idToken.take(32) // Unique account ID token slice
                return@withContext GoogleAuthResult.Success(
                    googleId = googleId,
                    email = email,
                    displayName = displayName,
                    idToken = googleIdTokenCredential.idToken
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
