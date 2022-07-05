package io.github.ferhatwi.supabase.auth

sealed class SupabaseAuthException(override val message: String) : Throwable(message) {
    data class NoActiveSession(override val message: String) : SupabaseAuthException(message)
}