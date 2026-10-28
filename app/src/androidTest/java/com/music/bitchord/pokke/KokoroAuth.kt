package com.music.bitchord.pokke

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Minimal Kokoro Supabase Auth client for Pokke accounts.
 *
 * Full UI (login screen, session restore, profile editor) still to be wired
 * into the existing auth package. Google OAuth can be added later on top of
 * the same session model.
 */
object KokoroAuth {

    private const val TAG = "Pokke.Kokoro"

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val http = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(json) }
    }

    @Serializable
    data class AuthSession(
        @SerialName("access_token") val accessToken: String,
        @SerialName("refresh_token") val refreshToken: String? = null,
        @SerialName("expires_in") val expiresIn: Int? = null,
        val user: AuthUser? = null,
    )

    @Serializable
    data class AuthUser(
        val id: String,
        val email: String? = null,
        @SerialName("user_metadata") val userMetadata: Map<String, String>? = null,
    )

    @Serializable
    data class Profile(
        val id: String,
        @SerialName("display_name") val displayName: String? = null,
        val username: String? = null,
        val bio: String? = null,
        @SerialName("avatar_url") val avatarUrl: String? = null,
        @SerialName("banner_url") val bannerUrl: String? = null,
        val theme: String? = null,
        val frame: String? = null,
        val stickers: String? = null, // jsonb as raw string for now
    )

    @Serializable
    private data class EmailLoginBody(
        val email: String,
        val password: String,
    )

    /** Email + password sign-in against Kokoro Auth. */
    suspend fun signInWithEmail(email: String, password: String): Result<AuthSession> {
        return runCatching {
            http.post("${PokkeConfig.KOKORO_URL}/auth/v1/token?grant_type=password") {
                header("apikey", PokkeConfig.KOKORO_ANON_KEY)
                contentType(ContentType.Application.Json)
                setBody(EmailLoginBody(email, password))
            }.body<AuthSession>()
        }.onFailure {
            Log.w(TAG, "signIn failed: ${it.message}")
        }
    }

    /** Load public profile row (banner, theme, frame, etc.). */
    suspend fun fetchProfile(userId: String, accessToken: String): Profile? {
        return runCatching {
            http.get("${PokkeConfig.KOKORO_URL}/rest/v1/profiles") {
                header("apikey", PokkeConfig.KOKORO_ANON_KEY)
                header("Authorization", "Bearer $accessToken")
                // PostgREST filter
                url.parameters.append("id", "eq.$userId")
                url.parameters.append("select", "*")
                url.parameters.append("limit", "1")
            }.body<List<Profile>>().firstOrNull()
        }.onFailure {
            Log.w(TAG, "fetchProfile failed: ${it.message}")
        }.getOrNull()
    }
}
