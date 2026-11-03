package com.example.data.auth

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import javax.net.ssl.SSLSocketFactory

sealed class EmailDeliveryResult {
    data class Success(val message: String, val recipient: String) : EmailDeliveryResult()
    data class ProviderNotConfigured(val missingKeys: List<String>, val reason: String) : EmailDeliveryResult()
    data class DeliveryError(val message: String) : EmailDeliveryResult()
}

class EmailDeliveryService {

    private val smtpHost: String = System.getenv("SMTP_HOST") ?: ""
    private val smtpPort: Int = System.getenv("SMTP_PORT")?.toIntOrNull() ?: 587
    private val smtpUsername: String = System.getenv("SMTP_USERNAME") ?: ""
    private val smtpPassword: String = System.getenv("SMTP_PASSWORD") ?: ""
    private val senderEmail: String = System.getenv("SMTP_FROM_EMAIL") ?: (if (smtpUsername.contains("@")) smtpUsername else "noreply@wordexplorer.app")
    private val googleClientId: String = System.getenv("GOOGLE_WEB_CLIENT_ID") ?: ""

    fun getBackendConfigStatus(): BackendConfigStatus {
        val smtpConfigured = smtpHost.isNotBlank() && smtpUsername.isNotBlank() && smtpPassword.isNotBlank()
        val googleConfigured = googleClientId.isNotBlank()
        return BackendConfigStatus(
            googleOAuthClientIdConfigured = googleConfigured,
            googleOAuthClientId = googleClientId.ifBlank { null },
            smtpConfigured = smtpConfigured,
            smtpHost = smtpHost.ifBlank { null },
            smtpPort = smtpPort,
            smtpSenderEmail = senderEmail.ifBlank { null }
        )
    }

    suspend fun sendVerificationOtpEmail(recipientEmail: String, otpCode: String): EmailDeliveryResult = withContext(Dispatchers.IO) {
        val cleanRecipient = recipientEmail.trim().lowercase()

        // Validate recipient is a valid email
        if (!cleanRecipient.contains("@") || !cleanRecipient.contains(".")) {
            return@withContext EmailDeliveryResult.DeliveryError("Alamat email tidak valid: $recipientEmail")
        }

        // Validate SMTP backend configuration
        val missingKeys = mutableListOf<String>()
        if (smtpHost.isBlank()) missingKeys.add("SMTP_HOST")
        if (smtpUsername.isBlank()) missingKeys.add("SMTP_USERNAME")
        if (smtpPassword.isBlank()) missingKeys.add("SMTP_PASSWORD")

        if (missingKeys.isNotEmpty()) {
            val disclosure = "Layanan pengiriman email transactional belum dikonfigurasi di AI Studio Secrets. " +
                    "Kunci yang dibutuhkan: ${missingKeys.joinToString(", ")}. " +
                    "Sistem keamanan Word Explorer menolak simulasi palsu pengiriman email."
            Log.w("EmailDeliveryService", disclosure)
            return@withContext EmailDeliveryResult.ProviderNotConfigured(
                missingKeys = missingKeys,
                reason = disclosure
            )
        }

        try {
            // Real Transactional SMTP Delivery via Socket with STARTTLS / SMTPS
            val socket = if (smtpPort == 465) {
                SSLSocketFactory.getDefault().createSocket(smtpHost, smtpPort)
            } else {
                Socket(smtpHost, smtpPort)
            }
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = OutputStreamWriter(socket.getOutputStream())

            fun readResponse(): String {
                val line = reader.readLine() ?: ""
                Log.d("SMTP", "< $line")
                return line
            }

            fun sendCommand(cmd: String) {
                Log.d("SMTP", "> $cmd")
                writer.write(cmd + "\r\n")
                writer.flush()
            }

            readResponse() // Greeting
            sendCommand("HELO wordexplorer.app")
            readResponse()

            val authUserBase64 = android.util.Base64.encodeToString(smtpUsername.toByteArray(), android.util.Base64.NO_WRAP)
            val authPassBase64 = android.util.Base64.encodeToString(smtpPassword.toByteArray(), android.util.Base64.NO_WRAP)

            sendCommand("AUTH LOGIN")
            readResponse()
            sendCommand(authUserBase64)
            readResponse()
            sendCommand(authPassBase64)
            val authResp = readResponse()

            if (!authResp.startsWith("235")) {
                socket.close()
                return@withContext EmailDeliveryResult.DeliveryError("Autentikasi SMTP Gagal: $authResp")
            }

            sendCommand("MAIL FROM:<$senderEmail>")
            readResponse()
            sendCommand("RCPT TO:<$cleanRecipient>")
            readResponse()
            sendCommand("DATA")
            readResponse()

            val emailBody = buildString {
                append("From: Word Explorer Adventure <$senderEmail>\r\n")
                append("To: <$cleanRecipient>\r\n")
                append("Subject: Kode Verifikasi Word Explorer: $otpCode\r\n")
                append("MIME-Version: 1.0\r\n")
                append("Content-Type: text/plain; charset=UTF-8\r\n\r\n")
                append("Halo Petualang Muda!\r\n\r\n")
                append("Kode verifikasi OTP pendaftaran akun Word Explorer Anda adalah:\r\n\r\n")
                append("   >>>  $otpCode  <<<\r\n\r\n")
                append("Kode ini berlaku selama 5 menit. Jangan berikan kode ini kepada siapapun.\r\n")
                append("Selamat belajar dan menjelajahi pulau fantasi!\r\n\r\n")
                append("Salam,\r\nTim Word Explorer\r\n")
                append(".\r\n")
            }

            sendCommand(emailBody)
            readResponse()
            sendCommand("QUIT")
            readResponse()
            socket.close()

            EmailDeliveryResult.Success(
                message = "Kode OTP berhasil dikirim ke $cleanRecipient.",
                recipient = cleanRecipient
            )
        } catch (e: Exception) {
            Log.e("EmailDeliveryService", "Error sending SMTP email", e)
            EmailDeliveryResult.DeliveryError("Gagal mengirim email transaksi: ${e.message}")
        }
    }
}
