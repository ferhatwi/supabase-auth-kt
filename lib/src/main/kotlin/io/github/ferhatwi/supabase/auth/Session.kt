package io.github.ferhatwi.supabase.auth

import java.time.OffsetDateTime

data class Session(
    val userID: String,
    val acquiredAt: OffsetDateTime,
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Double,
    val refreshToken: String
) {
    internal constructor(userID: String, map: Map<String, Any?>) : this(
        userID,
        OffsetDateTime.now(),
        map["access_token"] as String,
        map["token_type"] as String,
        map["expires_in"] as Double,
        map["refresh_token"] as String
    )
}
