package io.github.ferhatwi.supabase.auth

import java.time.OffsetDateTime

data class User internal constructor(
    val id: String,
    val aud: String,
    val role: String,
    val email: String?,
    val emailConfirmedAt: OffsetDateTime?,
    val invitedAt: OffsetDateTime?,
    val confirmationSentAt: OffsetDateTime?,
    val recoverySentAt: OffsetDateTime?,
    val lastSignInAt: OffsetDateTime?,
    val appMetadata: Map<String, Any?>,
    val userMetadata: Map<String, Any?>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val phoneConfirmedAt: OffsetDateTime?,
    val phone: String,
    val confirmedAt: OffsetDateTime?
) {
    internal constructor(map: Map<String, Any?>) : this(
        map["id"] as String,
        map["aud"] as String,
        map["role"] as String,
        map["email"] as String?,
        (map["email_confirmed_at"] as String?)?.let { OffsetDateTime.parse(it) },
        (map["invited_at"] as String?)?.let { OffsetDateTime.parse(it) },
        (map["confirmation_sent_at"] as String?)?.let { OffsetDateTime.parse(it) },
        (map["recovery_sent_at"] as String?)?.let { OffsetDateTime.parse(it) },
        (map["last_sign_in_at"] as String?)?.let { OffsetDateTime.parse(it) },
        map["app_metadata"] as Map<String, Any?>,
        map["user_metadata"] as Map<String, Any?>,
        OffsetDateTime.parse(map["created_at"] as String),
        OffsetDateTime.parse(map["updated_at"] as String),
        (map["phone_confirmed_at"] as String?)?.let { OffsetDateTime.parse(it) },
        map["phone"] as String,
        (map["confirmed_at"] as String?)?.let { OffsetDateTime.parse(it) },
    )
}