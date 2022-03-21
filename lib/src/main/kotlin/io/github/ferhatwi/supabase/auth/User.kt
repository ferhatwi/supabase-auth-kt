package io.github.ferhatwi.supabase.auth

import java.time.LocalDateTime


data class User internal constructor(
    val id: String,
    val aud: String,
    val role: String,
    val email: String?,
    val emailConfirmedAt: LocalDateTime?,
    val invitedAt: LocalDateTime?,
    val confirmationSentAt: LocalDateTime?,
    val recoverySentAt: LocalDateTime?,
    val lastSignInAt: LocalDateTime?,
    val appMetadata: Map<String, Any?>,
    val userMetadata: Map<String, Any?>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val phoneConfirmedAt: LocalDateTime?,
    val phone: String,
    val confirmedAt: LocalDateTime?
) {
    internal constructor(map: Map<String, Any?>) : this(
        map["id"] as String,
        map["aud"] as String,
        map["role"] as String,
        map["email"] as String?,
        Date.parse(map["email_confirmed_at"] as String?),
        Date.parse(map["invited_at"] as String?),
        Date.parse(map["confirmation_sent_at"] as String?),
        Date.parse(map["recovery_sent_at"] as String?),
        Date.parse(map["last_sign_in_at"] as String?),
        map["app_metadata"] as Map<String, Any?>,
        map["user_metadata"] as Map<String, Any?>,
        Date.parse(map["created_at"] as String),
        Date.parse(map["updated_at"] as String),
        Date.parse(map["phone_confirmed_at"] as String?),
        map["phone"] as String,
        Date.parse(map["confirmed_at"] as String?),
    )
}