package com.music.bitchord.pokke

/**
 * Pokke backend endpoints.
 *
 * - **Kokoro** → accounts, profiles, playlists, banners, themes
 * - **Ukiyo**  → music catalogue (`songs` table with audio_url, covers, lyrics)
 *
 * Anon / publishable keys are public by design (RLS protects data).
 * Google login will be added later; for now auth targets Kokoro Supabase Auth.
 */
object PokkeConfig {

    // ── Kokoro Supabase (auth + user data) ──────────────────────────────
    const val KOKORO_URL = "https://ajbmpgnzkgtcmulocftd.supabase.co"
    const val KOKORO_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFqYm1wZ256a2d0Y211bG9jZnRkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTAzNDU2MjksImV4cCI6MjEwNTkyMTYyOX0.twsBnT9WBBILyXFuoQh7viI578yoNbeQztKHsr8DB8k"

    // ── Ukiyo (music catalogue) ─────────────────────────────────────────
    const val UKIYO_URL = "https://esjoifsjljvymttinyhj.supabase.co"
    const val UKIYO_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVzam9pZnNqbGp2eW10dGlueWhqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTEwMzYyMTAsImV4cCI6MjEwNjYxMjIxMH0.GKA3KyoMElVwLUQlL2ad82IPjRmwKmMJEGPTUNwbuVQ"

    /** Default display name hint for new profiles (user asked for "pokk"). */
    const val DEFAULT_USERNAME_HINT = "pokk"
}
