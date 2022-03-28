package io.github.ferhatwi.supabase.auth

import java.security.MessageDigest
import java.util.*

fun generateNonce(result: (nonce: String, encryptedNonce: String) -> Unit) {
    val nonce = UUID.randomUUID().toString()
    result(
        nonce,
        MessageDigest.getInstance("SHA-256").digest(nonce.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) })
}