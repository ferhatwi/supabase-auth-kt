package io.github.ferhatwi.supabase.auth

data class Session internal constructor(
    val access_token: String,
    val token_type: String,
    val expiresIn: Double,
    val refreshToken: String
) {
    internal constructor(map: Map<String, Any?>) : this(
        map["access_token"] as String,
        map["token_type"] as String,
        map["expires_in"] as Double,
        map["refresh_token"] as String
    )
}