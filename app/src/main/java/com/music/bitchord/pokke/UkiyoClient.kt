package com.music.bitchord.pokke

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Thin PostgREST client for the Ukiyo `songs` table.
 *
 * This is the music backend for Pokke. Search / stream resolution will call
 * into here instead of (or before) YouTube when Ukiyo is the active source.
 */
object UkiyoClient {

    private const val TAG = "Pokke.Ukiyo"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val http = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(json) }
    }

    @Serializable
    data class UkiyoSong(
        val id: String,
        val title: String,
        val artist: String,
        val album: String? = null,
        @SerialName("duration_seconds") val durationSeconds: Int? = null,
        @SerialName("audio_url") val audioUrl: String,
        @SerialName("cover_url") val coverUrl: String? = null,
        @SerialName("animated_cover_url") val animatedCoverUrl: String? = null,
        @SerialName("lyrics_text") val lyricsText: String? = null,
        @SerialName("youtube_id") val youtubeId: String? = null,
        val genre: String? = null,
    )

    /**
     * Full-text-ish search on title / artist via PostgREST `ilike`.
     * Returns up to [limit] rows that have a non-empty audio_url.
     */
    suspend fun search(query: String, limit: Int = 25): List<UkiyoSong> {
        if (query.isBlank()) return emptyList()
        val q = query.trim()
        return runCatching {
            http.get("${PokkeConfig.UKIYO_URL}/rest/v1/songs") {
                header("apikey", PokkeConfig.UKIYO_ANON_KEY)
                header("Authorization", "Bearer ${PokkeConfig.UKIYO_ANON_KEY}")
                // (title.ilike.*q*) OR (artist.ilike.*q*)
                parameter("or", "(title.ilike.*$q*,artist.ilike.*$q*)")
                parameter("select", "id,title,artist,album,duration_seconds,audio_url,cover_url,animated_cover_url,lyrics_text,youtube_id,genre")
                parameter("audio_url", "not.is.null")
                parameter("limit", limit.toString())
                parameter("order", "title.asc")
            }.body<List<UkiyoSong>>()
        }.onFailure {
            Log.w(TAG, "search failed: ${it.message}")
        }.getOrDefault(emptyList())
    }

    /** Fetch a single song by primary key. */
    suspend fun getById(id: String): UkiyoSong? {
        return runCatching {
            http.get("${PokkeConfig.UKIYO_URL}/rest/v1/songs") {
                header("apikey", PokkeConfig.UKIYO_ANON_KEY)
                header("Authorization", "Bearer ${PokkeConfig.UKIYO_ANON_KEY}")
                parameter("id", "eq.$id")
                parameter("select", "id,title,artist,album,duration_seconds,audio_url,cover_url,animated_cover_url,lyrics_text,youtube_id,genre")
                parameter("limit", "1")
            }.body<List<UkiyoSong>>().firstOrNull()
        }.onFailure {
            Log.w(TAG, "getById failed: ${it.message}")
        }.getOrNull()
    }

    /** Recent / sample catalogue (for home when Ukiyo is primary). */
    suspend fun recent(limit: Int = 30): List<UkiyoSong> {
        return runCatching {
            http.get("${PokkeConfig.UKIYO_URL}/rest/v1/songs") {
                header("apikey", PokkeConfig.UKIYO_ANON_KEY)
                header("Authorization", "Bearer ${PokkeConfig.UKIYO_ANON_KEY}")
                parameter("select", "id,title,artist,album,duration_seconds,audio_url,cover_url,animated_cover_url,lyrics_text,youtube_id,genre")
                parameter("audio_url", "not.is.null")
                parameter("order", "created_at.desc")
                parameter("limit", limit.toString())
            }.body<List<UkiyoSong>>()
        }.onFailure {
            Log.w(TAG, "recent failed: ${it.message}")
        }.getOrDefault(emptyList())
    }
}
