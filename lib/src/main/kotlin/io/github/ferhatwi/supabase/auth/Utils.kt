package io.github.ferhatwi.supabase.auth

import io.github.ferhatwi.supabase.Supabase
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


internal fun authURL() = "https://${Supabase.PROJECT_ID}.supabase.co/auth/v1"

internal fun getClient(): HttpClient {
    return HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }
}

internal suspend inline fun <reified T> HttpClient.request(
    url: String,
    method: HttpMethod,
    body: Any = EmptyContent,
    noinline headers: HeadersBuilder.() -> Unit = {}
): T {
    return request(url) {
        this.method = method
        this.body = body
        headers {
            apiKey()
            headers()
        }
    }
}

internal fun HttpRequestBuilder.apiKey() {
    headers.append("apikey", Supabase.API_KEY)
}

internal fun HeadersBuilder.authorize() {
    append(HttpHeaders.Authorization, "Bearer ${Supabase.AUTHORIZATION}")
}

internal fun HeadersBuilder.applicationJson() {
    append(HttpHeaders.ContentType, "application/json")
}

internal suspend inline fun <reified T> runCatching(
    block: () -> T,
    onSuccess: (T) -> Unit
) = runCatching { onSuccess(block()) }
    .getOrElse {
        when (it) {
            is ResponseException -> {
                val map: Map<String, Any?> = it.response.receive()
                val message = map["message"] as String?
                val code = map["code"] as String?
                val statusCode = it.response
                throw SupabaseAuthException("$message $code, $statusCode")
            }
            else -> throw it
        }
    }

internal object Date {
    private fun ISO_8601(countOfMillis: Int) =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.${"S".repeat(countOfMillis)}'Z'")

    //2022-03-11T00:07:22.312...Z
    @JvmName("ParseNonNull")
    fun parse(value: String): LocalDateTime =
        LocalDateTime.parse(value, ISO_8601(value.substringAfterLast(".").length - 1))

    @JvmName("ParseNullable")
    fun parse(value: String?): LocalDateTime? = if (value == null) null else parse(value)
}