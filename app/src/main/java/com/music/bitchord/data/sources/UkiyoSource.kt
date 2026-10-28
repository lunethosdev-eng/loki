package com.music.bitchord.data.sources

import com.music.bitchord.data.TrackLog
import com.music.bitchord.data.model.Song
import com.music.bitchord.pokke.PokkeConfig
import com.music.bitchord.pokke.UkiyoClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Pokke primary catalogue: Ukiyo Supabase `songs` table (audio_url, covers…).
 *
 * Stored as [SourceKind.ADDON] with [SourceConfig.baseUrl] = [PokkeConfig.UKIYO_URL]
 * so it shows on the Sources screen and can be toggled like any other source.
 * YouTube stays optional (off by default).
 */
class UkiyoSource(
    override val config: SourceConfig,
) : MusicSource, SourceRegistry.ConfigBacked {

    override val configId: String get() = config.id
    override val kind: SourceKind get() = config.kind
    override val displayName: String get() = config.label.ifBlank { "Ukiyo" }

    override suspend fun health(): SourceHealth = withContext(Dispatchers.IO) {
        runCatching {
            val rows = UkiyoClient.recent(limit = 1)
            if (rows.isNotEmpty()) SourceHealth.Ok("Ukiyo OK")
            else SourceHealth.Ok("Ukiyo reachable (empty catalogue)")
        }.getOrElse {
            TrackLog.w(TAG, "Ukiyo health: ${it.message}")
            SourceHealth.Unreachable(it.message ?: "unreachable")
        }
    }

    override suspend fun search(
        query: String,
        limit: Int,
        waitForAll: Boolean,
        request: StreamRequest?,
    ): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        UkiyoClient.search(query, limit = limit).map { it.toSong(config.id) }
    }

    override suspend fun stream(trackId: String, request: StreamRequest): SourceStream? =
        withContext(Dispatchers.IO) {
            val row = UkiyoClient.getById(trackId) ?: return@withContext null
            if (row.audioUrl.isBlank()) return@withContext null
            SourceStream(
                url = row.audioUrl,
                format = StreamFormat(),
                durationSec = row.durationSeconds,
            )
        }

    private fun UkiyoClient.UkiyoSong.toSong(configId: String) = Song(
        videoId = SourceRegistry.trackKey(configId, id),
        title = title,
        artist = artist,
        thumbnailUrl = coverUrl ?: animatedCoverUrl,
        durationText = durationSeconds?.let { sec ->
            val m = sec / 60
            val s = sec % 60
            "%d:%02d".format(m, s)
        },
        albumName = album,
    )

    companion object {
        private const val TAG = "Pokke.UkiyoSource"

        fun isUkiyoConfig(config: SourceConfig): Boolean {
            if (config.label.equals("Ukiyo", ignoreCase = true)) return true
            val url = config.baseUrl.lowercase()
            return url.contains("esjoifsjljvymttinyhj") ||
                url.contains(PokkeConfig.UKIYO_URL.lowercase().removePrefix("https://"))
        }

        /** Seeded on first run as the primary enabled source. */
        fun defaultConfig() = SourceConfig(
            kind = SourceKind.ADDON,
            label = "Ukiyo",
            baseUrl = PokkeConfig.UKIYO_URL,
            enabled = true,
        )
    }
}
