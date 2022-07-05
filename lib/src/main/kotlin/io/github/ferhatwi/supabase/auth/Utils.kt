package io.github.ferhatwi.supabase.auth

import io.github.ferhatwi.supabase.Supabase
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*


internal fun authURL() = "https://${Supabase.PROJECT_ID}.supabase.co/auth/v1"

internal fun getClient(): HttpClient {
    return HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
    }
}

internal fun HttpRequestBuilder.apiKey() {
    headers.append("apikey", Supabase.API_KEY)
}

internal fun HeadersBuilder.authorize() {
    append(HttpHeaders.Authorization, "Bearer ${Supabase.AUTHORIZATION}")
}