package io.github.ferhatwi.supabase.auth

import io.github.ferhatwi.supabase.Supabase
import io.ktor.client.statement.*
import io.ktor.http.*
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
        captchaToken: String? = null,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: (session: Session?, user: User) -> Unit
    ) {
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

        runCatching({
            val result: Map<String, Any?> = getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
            val session = if (result.contains("access_token")) Session(result) else null
            val user = User(result["user"] as Map<String, Any?>)

            if (session != null) {
                Supabase.setAuth(session.access_token)
                AuthState.onAuthStateChanged?.onSignIn(user)
            }
            onSuccess(
                session,
                user
            )
        }, onFailure)

    }

    suspend fun signInWithEmail(
        email: String,
        password: String,
        redirectTo: String? = null,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: (session: Session, user: User) -> Unit
    ) {
        val url =
            "${authURL()}/token?grant_type=password${if (redirectTo != null) "&${redirectTo.encodeURLPath()}" else ""}"

        val map = mapOf("email" to email, "password" to password)

        runCatching({
            val result: Map<String, Any?> = getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
            val session = Session(result)
            val user = User(result["user"] as Map<String, Any?>)

            Supabase.setAuth(session.access_token)
            AuthState.onAuthStateChanged?.onSignIn(user)
            onSuccess(
                session,
                user
            )
        }, onFailure)
    }


    suspend fun signUpWithPhone(
        phone: String,
        password: String,
        data: Map<String, Any?>? = null,
        captchaToken: String? = null,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: (session: Session?, user: User) -> Unit
    ) {
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

        runCatching({
            val result: Map<String, Any?> = getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
            val session = if (result.contains("access_token")) Session(result) else null
            val user = User(result["user"] as Map<String, Any?>)

            if (session != null) {
                Supabase.setAuth(session.access_token)
                AuthState.onAuthStateChanged?.onSignIn(user)
            }
            onSuccess(
                session,
                user
            )
        }, onFailure)
    }

    suspend fun signInWithPhone(
        phone: String,
        password: String,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: (session: Session, user: User) -> Unit
    ) {
        val url = "${authURL()}/token?grant_type=password"

        val map = mapOf("phone" to phone, "password" to password)

        runCatching({
            val result: Map<String, Any?> = getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
            val session = Session(result)
            val user = User(result["user"] as Map<String, Any?>)

            Supabase.setAuth(session.access_token)
            AuthState.onAuthStateChanged?.onSignIn(user)
            onSuccess(
                session,
                user
            )
        }, onFailure)
    }

    /**
     * [generateNonce] function's result;
     * "nonce: String" can be used here as "nonce" argument,
     * "encryptedNonce: String" can be used to set nonce to OpenID connect.
     *
     */

    suspend fun signInWithThirdPartyProvider(
        idToken: String,
        nonce: String,
        clientKey: String,
        provider: Provider,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: (session: Session, user: User) -> Unit
    ) {
        val url = "${authURL()}/token?grant_type=id_token"

        val map = mapOf(
            "id_token" to idToken,
            "nonce" to nonce,
            "client_id" to clientKey,
            "provider" to provider.toString()
        )

        runCatching({
            val result: Map<String, Any?> = getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
            val session = Session(result)
            val user = User(result["user"] as Map<String, Any?>)

            Supabase.setAuth(session.access_token)
            AuthState.onAuthStateChanged?.onSignIn(user)
            onSuccess(
                session,
                user
            )
        }, onFailure)
    }

    suspend fun sendMagicLinkEmail(
        email: String,
        shouldCreateUser: Boolean = true,
        redirectTo: String? = null,
        captchaToken: String? = null,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: () -> Unit
    ) {
        val url =
            "${authURL()}/otp${if (redirectTo != null) "?${redirectTo.encodeURLPath()}" else ""}"

        val map = mapOf(
            "email" to email,
            "create_user" to shouldCreateUser,
            "gotrue_meta_security" to mapOf("hcaptcha_token" to captchaToken)
        )

        runCatching({
            getClient().request<HttpResponse>(url, HttpMethod.Post, map) {
                applicationJson()
            }
            onSuccess()
        }, onFailure)

    }

    suspend fun sendMobileOTP(
        phone: String,
        shouldCreateUser: Boolean = true,
        captchaToken: String? = null,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: () -> Unit
    ) {
        val url = "${authURL()}/otp"

        val map = mapOf(
            "phone" to phone,
            "create_user" to shouldCreateUser,
            "gotrue_meta_security" to mapOf("hcaptcha_token" to captchaToken)
        )

        runCatching({
            getClient().request<HttpResponse>(url, HttpMethod.Post, map) {
                applicationJson()
            }
            onSuccess()
        }, onFailure)
    }


    suspend fun signOut(
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: () -> Unit
    ) {

        val url = "${authURL()}/logout"

        runCatching({
            getClient().request<HttpResponse>(url, HttpMethod.Post, mapOf<String, Any?>()) {
                applicationJson()
                authorize()
            }

            AuthState.onAuthStateChanged?.onSignOut()
            onSuccess()
        }, onFailure)
    }


    suspend fun verifyMobileOTP(
        phone: String,
        token: String,
        redirectTo: String? = null,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: (session: Session, user: User) -> Unit
    ) {
        val url = "${authURL()}/verify"

        val map = hashMapOf("phone" to phone, token to token, "type" to "sms")
        if (redirectTo != null) {
            map["redirect_to"] = redirectTo
        }

        runCatching({
            val result: Map<String, Any?> = getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
            val session = Session(result)
            val user = User(result["user"] as Map<String, Any?>)

            Supabase.setAuth(session.access_token)
            AuthState.onAuthStateChanged?.onSignIn(user)
            onSuccess(
                session,
                user
            )

        }, onFailure)

    }


    suspend fun resetPasswordForEmail(
        email: String,
        redirectTo: String? = null,
        captchaToken: String? = null,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: () -> Unit
    ) {
        val url =
            "${authURL()}/recover${if (redirectTo != null) "?${redirectTo.encodeURLPath()}" else ""}"

        val map = mapOf(
            "email" to email,
            "gotrue_meta_security" to mapOf("hcaptcha_token" to captchaToken)
        )

        runCatching({
            getClient().request<HttpResponse>(url, HttpMethod.Post, map) {
                applicationJson()
            }
            onSuccess()
        }, onFailure)

    }


    /**
     * Generates a new JWT with [refreshToken] and sets it as [Supabase.AUTHORIZATION] with function [Supabase.setAuth].
     */
    suspend fun refreshAccessToken(
        refreshToken: String,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: (session: Session, user: User) -> Unit
    ) {

        val url = "${authURL()}/token?grant_type=refresh_token"
        val map = mapOf("refresh_token" to refreshToken)

        runCatching({
            val result: Map<String, Any?> = getClient().request(url, HttpMethod.Post, map) {
                applicationJson()
            }
            val session = Session(result)
            Supabase.setAuth(session.access_token)
            onSuccess(
                session,
                User(result["user"] as Map<String, Any?>)
            )
        }, onFailure)

    }

    /**
     * Gets the current [User] using the last JWT that have been set with the function [Supabase.setAuth].
     */
    suspend fun getUser(
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: (session: Session, user: User) -> Unit
    ) {
        val url = "${authURL()}/user"

        runCatching({
            val result: Map<String, Any?> = getClient().request(url, HttpMethod.Get) {
                authorize()
            }
            onSuccess(
                Session(result),
                User(result["user"] as Map<String, Any?>)
            )
        }, onFailure)

    }

    /**
     * Updates current [User] using the last JWT that have been set with the function [Supabase.setAuth].
     * [email], [phone], [password], [emailChangeToken], [data] arguments should be left as null if the
     * update of the attribute isn't wanted.
     */
    suspend fun updateUser(
        email: UpdatableUserAttributes.Email? = null,
        phone: UpdatableUserAttributes.Phone? = null,
        password: UpdatableUserAttributes.Password? = null,
        emailChangeToken: UpdatableUserAttributes.EmailChangeToken? = null,
        data: UpdatableUserAttributes.Data? = null,
        onFailure: (HttpStatusCode) -> Unit,
        onSuccess: (session: Session, user: User) -> Unit
    ) {
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


        runCatching({
            val result: Map<String, Any?> = getClient().request(url, HttpMethod.Put, map) {
                authorize()
                applicationJson()
            }
            onSuccess(
                Session(result),
                User(result["user"] as Map<String, Any?>)
            )
        }, onFailure)
    }


    fun setOnAuthStateChanged(onAuthStateChanged: OnAuthStateChanged) {
        AuthState.onAuthStateChanged = onAuthStateChanged
    }

}

fun Supabase.auth(): SupabaseAuth = SupabaseAuth.getInstance()
