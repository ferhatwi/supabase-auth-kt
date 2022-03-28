package io.github.ferhatwi.supabase.auth

import io.github.ferhatwi.supabase.Supabase
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.flow
import kotlin.collections.set

object AuthState {
    internal var onAuthStateChanged: OnAuthStateChanged? = null
}

class SupabaseAuth {

    companion object {
        fun getInstance() = SupabaseAuth()
    }

    suspend fun signUpWithEmail(
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
        if (data != null) {
            map["data"] = data
        }
        if (captchaToken != null) {
            map["gotrue_meta_security"] = mapOf("captcha_token" to captchaToken)
        }

        runCatching<Map<String, Any?>>({
            getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
        }, onSuccess = {
            val session = if (it.contains("access_token")) Session(it) else null
            val user = User(it["user"] as Map<String, Any?>)

            if (session != null) {
                Supabase.setAuth(session.access_token)
                AuthState.onAuthStateChanged?.onSignIn(user)
            }

            emit(Pair(session, user))
        })
    }

    suspend fun signInWithEmail(
        email: String,
        password: String,
        redirectTo: String? = null
    ) = flow {
        val url =
            "${authURL()}/token?grant_type=password${if (redirectTo != null) "&${redirectTo.encodeURLPath()}" else ""}"

        val map = mapOf("email" to email, "password" to password)

        runCatching<Map<String, Any?>>({
            getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
        }, onSuccess = {
            val session = Session(it)
            val user = User(it["user"] as Map<String, Any?>)
            Supabase.setAuth(session.access_token)
            AuthState.onAuthStateChanged?.onSignIn(user)

            emit(Pair(session, user))
        })
    }

    suspend fun signUpWithPhone(
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


        runCatching<Map<String, Any?>>({
            getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
        }, onSuccess = {
            val session = if (it.contains("access_token")) Session(it) else null
            val user = User(it["user"] as Map<String, Any?>)

            if (session != null) {
                Supabase.setAuth(session.access_token)
                AuthState.onAuthStateChanged?.onSignIn(user)
            }

            emit(Pair(session, user))
        })
    }

    suspend fun signInWithPhone(
        phone: String,
        password: String,
    ) = flow {
        val url = "${authURL()}/token?grant_type=password"
        val map = mapOf("phone" to phone, "password" to password)

        runCatching<Map<String, Any?>>({
            getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
        }, onSuccess = {
            val session = Session(it)
            val user = User(it["user"] as Map<String, Any?>)
            Supabase.setAuth(session.access_token)
            AuthState.onAuthStateChanged?.onSignIn(user)

            emit(Pair(session, user))
        })
    }

    suspend fun signInWithThirdPartyProvider(
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

        runCatching<Map<String, Any?>>({
            getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
        }, onSuccess = {
            val session = if (it.contains("access_token")) Session(it) else null
            val user = User(it["user"] as Map<String, Any?>)

            if (session != null) {
                Supabase.setAuth(session.access_token)
                AuthState.onAuthStateChanged?.onSignIn(user)
            }

            emit(Pair(session, user))
        })
    }

    suspend fun sendMagicLinkEmail(
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

        runCatching<HttpResponse>({
            getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
        }, onSuccess = {
            emit(SupabaseAuthSuccess)
        })
    }

    suspend fun sendMobileOTP(
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

        runCatching<HttpResponse>({
            getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
        }, onSuccess = {
            emit(SupabaseAuthSuccess)
        })
    }

    suspend fun signOut() = flow {
        val url = "${authURL()}/logout"

        runCatching<HttpResponse>({
            getClient().request(url, HttpMethod.Post, mapOf<String, Any?>()) {
                applicationJson()
                authorize()
            }
        }, onSuccess = {
            AuthState.onAuthStateChanged?.onSignOut()
            emit(SupabaseAuthSuccess)
        })
    }

    suspend fun verifyMobileOTP(
        phone: String,
        token: String,
        redirectTo: String? = null
    ) = flow {
        val url = "${authURL()}/verify"

        val map = hashMapOf("phone" to phone, token to token, "type" to "sms")
        if (redirectTo != null) {
            map["redirect_to"] = redirectTo
        }


        runCatching<Map<String, Any?>>({
            getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
        }, onSuccess = {
            val session = Session(it)
            val user = User(it["user"] as Map<String, Any?>)
            Supabase.setAuth(session.access_token)
            AuthState.onAuthStateChanged?.onSignIn(user)

            emit(Pair(session, user))
        })
    }

    suspend fun resetPasswordForEmail(
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


        runCatching<HttpResponse>({
            getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
        }, onSuccess = {
            emit(SupabaseAuthSuccess)
        })
    }

    suspend fun refreshAccessToken(refreshToken: String) = flow {
        val url = "${authURL()}/token?grant_type=refresh_token"
        val map = mapOf("refresh_token" to refreshToken)

        runCatching<Map<String, Any?>>({
            getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
        }, onSuccess = {
            val session = Session(it)
            val user = User(it["user"] as Map<String, Any?>)
            Supabase.setAuth(session.access_token)
            AuthState.onAuthStateChanged?.onSignIn(user)

            emit(Pair(session, user))
        })
    }

    suspend fun getUser() = flow {
        val url = "${authURL()}/user"

        runCatching<Map<String, Any?>>({
            getClient().request(url, HttpMethod.Get) {
                authorize()
            }
        }, onSuccess = {
            emit(Pair(Session(it), User(it["user"] as Map<String, Any?>)))
        })
    }

    suspend fun updateUser(
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

        runCatching<Map<String, Any?>>({
            getClient().request(url, HttpMethod.Put, map) {
                authorize()
                applicationJson()
            }
        }, onSuccess = {
            emit(User(it["user"] as Map<String, Any?>))
        })
    }

    fun setOnAuthStateChanged(onAuthStateChanged: OnAuthStateChanged) {
        AuthState.onAuthStateChanged = onAuthStateChanged
    }

}

fun Supabase.auth(): SupabaseAuth = SupabaseAuth.getInstance()
