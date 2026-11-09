package com.example.data.auth

data class BackendConfigStatus(
    val googleOAuthClientIdConfigured: Boolean,
    val googleOAuthClientId: String?,
    val smtpConfigured: Boolean,
    val smtpHost: String?,
    val smtpPort: Int,
    val smtpSenderEmail: String?,
    val firestoreConfigured: Boolean = true,
    val firestoreDatabaseId: String = "ai-studio-android-wordexpl-2f4f7f9a-bc24-41b4-9274-e859c5a563b3",
    val databaseEngine: String = "Cloud Firestore (Active Database: ai-studio-android-wordexpl-2f4f7f9a-bc24-41b4-9274-e859c5a563b3)",
    val otpAlgorithm: String = "SHA-256 Server Hash (5-Min Expiry, Max 5 Attempts)",
    val dailyLimitStatus: String = "Active (Max 3 Levels/Day Enforced in Firestore & Local Rules)",
    val accountLinkingStatus: String = "Active (Canonical User Linking on Verified Email / Firebase UID)"
) {
    val isReadyForRealSmtp: Boolean get() = smtpConfigured && !smtpHost.isNullOrBlank()
    val isReadyForGoogleOAuth: Boolean get() = googleOAuthClientIdConfigured && !googleOAuthClientId.isNullOrBlank()
}
