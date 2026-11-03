package com.example.data.auth

data class BackendConfigStatus(
    val googleOAuthClientIdConfigured: Boolean,
    val googleOAuthClientId: String?,
    val smtpConfigured: Boolean,
    val smtpHost: String?,
    val smtpPort: Int,
    val smtpSenderEmail: String?,
    val databaseEngine: String = "Room Persistent SQLite (Local Verified Canonical Store)",
    val otpAlgorithm: String = "SHA-256 Server Hash (5-Min Expiry, Max 5 Attempts)",
    val dailyLimitStatus: String = "Active (Max 3 Levels/Day, Min 17 Days for 50 Levels)",
    val accountLinkingStatus: String = "Active (Canonical User Linking on Verified Email)"
) {
    val isReadyForRealSmtp: Boolean get() = smtpConfigured && !smtpHost.isNullOrBlank()
    val isReadyForGoogleOAuth: Boolean get() = googleOAuthClientIdConfigured && !googleOAuthClientId.isNullOrBlank()
}
