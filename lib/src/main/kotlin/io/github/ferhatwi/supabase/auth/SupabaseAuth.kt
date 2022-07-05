package io.github.ferhatwi.supabase.auth

import io.github.ferhatwi.supabase.Supabase
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.util.*
import kotlin.collections.set

var session: MutableStateFlow<Session?> = MutableStateFlow(null)
    internal set


class SupabaseAuth {

    companion object {
        fun getInstance() = SupabaseAuth()
    }

    fun signUpWithEmail(
        email: String,
        password: String,
        redirectTo: String? = null,
        data: Map<String, Any?>? = null,
        captchaToken: String? = null
    ) = flow {
        val url =
            "${authURL()}/signup${if (redirectTo != null) "?${redirectTo.encodeURLPath()}" else ""}"

        val map = hashMapOf<String, Any?>(
            "email" to email,
            "password" to password
        )
        if (data != null) map["data"] = data

        if (captchaToken != null) map["gotrue_meta_security"] =
            mapOf("captcha_token" to captchaToken)



        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
            }
            setBody(map)
        }.body<Map<String, Any?>>().also {
            @Suppress("UNCHECKED_CAST") val user = User(it["user"] as Map<String, Any?>)
            val session = (if (it.contains("access_token")) Session(user.id, it) else null).also {
                session.value = it
            }

            if (session != null) Supabase.setAuth(session.accessToken)

            emit(Pair(session, user))
        }
    }

    fun signInWithEmail(
        email: String,
        password: String,
        redirectTo: String? = null
    ) = flow {
        val url =
            "${authURL()}/token?grant_type=password${if (redirectTo != null) "&${redirectTo.encodeURLPath()}" else ""}"

        val map = mapOf("email" to email, "password" to password)

        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
            }
            setBody(map)
        }.body<Map<String, Any?>>().also {
            @Suppress("UNCHECKED_CAST") val user = User(it["user"] as Map<String, Any?>)
            val session = Session(user.id, it).also {
                session.value = it
                Supabase.setAuth(it.accessToken)
            }

            emit(Pair(session, user))
        }
    }

    fun signUpWithPhone(
        phone: String,
        password: String,
        data: Map<String, Any?>? = null,
        captchaToken: String? = null
    ) = flow {
        val url = "${authURL()}/signup"

        val map = hashMapOf<String, Any?>(
            "phone" to phone,
            "password" to password
        )
        if (data != null) {
            map["data"] = data
        }
        if (captchaToken != null) {
            map["gotrue_meta_security"] = mapOf("captcha_token" to captchaToken)
        }


        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
            }
            setBody(map)
        }.body<Map<String, Any?>>().also {
            @Suppress("UNCHECKED_CAST") val user = User(it["user"] as Map<String, Any?>)

            val session = (if (it.contains("access_token")) Session(user.id, it) else null).also {
                session.value = it
            }

            if (session != null) Supabase.setAuth(session.accessToken)


            emit(Pair(session, user))
        }
    }

    fun signInWithPhone(
        phone: String,
        password: String,
    ) = flow {
        val url = "${authURL()}/token?grant_type=password"
        val map = mapOf("phone" to phone, "password" to password)

        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
            }
            setBody(map)
        }.body<Map<String, Any?>>().also {
            @Suppress("UNCHECKED_CAST") val user = User(it["user"] as Map<String, Any?>)

            val session = Session(user.id, it).also {
                session.value = it
                Supabase.setAuth(it.accessToken)
            }

            emit(Pair(session, user))
        }
    }


    fun signInWithThirdPartyProvider(
        idToken: String,
        unencryptedNonce: String,
        clientKey: String,
        provider: Provider
    ) = flow {
        val url = "${authURL()}/token?grant_type=id_token"

        val map = mapOf(
            "id_token" to idToken,
            "nonce" to unencryptedNonce,
            "client_id" to clientKey,
            "provider" to provider.toString()
        )

        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
            }
            setBody(map)
        }.body<Map<String, Any?>>().also {
            @Suppress("UNCHECKED_CAST") val user = User(it["user"] as Map<String, Any?>)

            val session = (if (it.contains("access_token")) Session(user.id, it) else null).also {
                session.value = it
            }

            if (session != null) Supabase.setAuth(session.accessToken)

            emit(Pair(session, user))
        }
    }

    fun sendMagicLinkEmail(
        email: String,
        shouldCreateUser: Boolean = true,
        redirectTo: String? = null,
        captchaToken: String? = null
    ) = flow {
        val url =
            "${authURL()}/otp${if (redirectTo != null) "?${redirectTo.encodeURLPath()}" else ""}"

        val map = mapOf(
            "email" to email,
            "create_user" to shouldCreateUser,
            "gotrue_meta_security" to mapOf("hcaptcha_token" to captchaToken)
        )

        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
            }
            setBody(map)
        }.also {
            emit(SupabaseAuthSuccess)
        }
    }

    fun sendMobileOTP(
        phone: String,
        shouldCreateUser: Boolean = true,
        captchaToken: String? = null
    ) = flow {
        val url = "${authURL()}/otp"

        val map = mapOf(
            "phone" to phone,
            "create_user" to shouldCreateUser,
            "gotrue_meta_security" to mapOf("hcaptcha_token" to captchaToken)
        )

        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
            }
            setBody(map)
        }.also {
            emit(SupabaseAuthSuccess)
        }
    }

    fun signOut() = flow {
        val url = "${authURL()}/logout"

        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
                authorize()
            }
        }.also {
            Supabase.clearAuth()
            emit(SupabaseAuthSuccess)
        }
    }

    fun verifyMobileOTP(
        phone: String,
        token: String,
        redirectTo: String? = null
    ) = flow {
        val url = "${authURL()}/verify"

        val map = hashMapOf("phone" to phone, token to token, "type" to "sms")
        if (redirectTo != null) {
            map["redirect_to"] = redirectTo
        }

        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
            }
            setBody(map)
        }.body<Map<String, Any?>>().also {
            @Suppress("UNCHECKED_CAST") val user = User(it["user"] as Map<String, Any?>)

            val session = Session(user.id, it).also {
                session.value = it
                Supabase.setAuth(it.accessToken)
            }

            emit(Pair(session, user))
        }
    }

    fun resetPasswordForEmail(
        email: String,
        redirectTo: String? = null,
        captchaToken: String? = null
    ) = flow {
        val url =
            "${authURL()}/recover${if (redirectTo != null) "?${redirectTo.encodeURLPath()}" else ""}"

        val map = mapOf(
            "email" to email,
            "gotrue_meta_security" to mapOf("hcaptcha_token" to captchaToken)
        )


        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
            }
            setBody(map)
        }.also {
            emit(SupabaseAuthSuccess)
        }
    }


    fun refreshSession(currentSession: Session) = flow {
        val url = "${authURL()}/token?grant_type=refresh_token"
        val map = mapOf("refresh_token" to currentSession.refreshToken)

        getClient().post(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
            }
            setBody(map)
        }.body<Map<String, Any?>>().also {
            Session(currentSession.userID, it).also {
                session.value = it
                Supabase.setAuth(it.accessToken)
                emit(it)
            }
        }
    }

    fun autoRefreshSession() = flow {
        session.value?.also {
            emit(it)

            (it
                .acquiredAt
                .plusSeconds((it.expiresIn - 10).toLong())
                .toInstant()
                .toEpochMilli()
                    -
                    OffsetDateTime.now()
                        .toInstant()
                        .toEpochMilli())
                .let {
                    if (it > 0) {
                        delay(it)
                    }
                }

            suspend fun repeating() {
                delay((it.expiresIn.toLong() - 10) * 1000)
                session.value?.also {
                    refreshSession(it).collect {
                        emit(it)
                        repeating()
                    }
                } ?: throw SupabaseAuthException.NoActiveSession("NoActiveSession")
            }
            repeating()

        } ?: throw SupabaseAuthException.NoActiveSession("NoActiveSession")
    }

    fun getUser() = flow {
        val url = "${authURL()}/user"

        getClient().get(url) {
            headers {
                apiKey()
                authorize()
            }
        }.body<Map<String, Any?>>().also {
            @Suppress("UNCHECKED_CAST")
            emit(User(it["user"] as Map<String, Any?>))
        }
    }

    fun updateUser(
        email: UpdatableUserAttributes.Email? = null,
        phone: UpdatableUserAttributes.Phone? = null,
        password: UpdatableUserAttributes.Password? = null,
        emailChangeToken: UpdatableUserAttributes.EmailChangeToken? = null,
        data: UpdatableUserAttributes.Data? = null
    ) = flow {
        val url = "${authURL()}/user"

        val map = hashMapOf<String, Any?>()
        if (email != null) {
            map["email"] = email.value
        }
        if (phone != null) {
            map["phone"] = phone.value
        }
        if (password != null) {
            map["password"] = password.value
        }
        if (emailChangeToken != null) {
            map["email_change_token"] = emailChangeToken.value
        }
        if (data != null) {
            map["data"] = data.value
        }


        getClient().put(url) {
            contentType(ContentType.Application.Json)
            headers {
                apiKey()
                authorize()
            }
            setBody(map)
        }.body<Map<String, Any?>>().also {
            @Suppress("UNCHECKED_CAST")
            emit(User(it["user"] as Map<String, Any?>))
        }
    }

    fun generateNonce() = UUID.randomUUID().toString().let {
        Nonce(it, MessageDigest.getInstance("SHA-256").digest(it.toByteArray())
            .fold("") { str, byte -> str + "%02x".format(byte) })
    }

    fun setSession(value: Session) {
        session.value = value
        Supabase.setAuth(value.accessToken)
    }

}

fun Supabase.auth(): SupabaseAuth = SupabaseAuth.getInstance()
