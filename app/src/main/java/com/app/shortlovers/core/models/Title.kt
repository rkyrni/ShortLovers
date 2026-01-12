package com.app.shortlovers.core.models

import com.app.shortlovers.core.network.ApiConfig
import com.google.gson.annotations.SerializedName

/**
 * Represents a title (drama/series) from the Shortorya API.
 *
 * Titles are the main content items displayed in the app. Each title can belong to a [TitleGroup]
 * for categorization.
 *
 * @property id Unique identifier for the title
 * @property title Display name of the drama/series
 * @property poster UUID of the poster image asset (use [posterUrl] to get full URL)
 * @property synopsis Description/summary of the title
 * @property viewCount Number of views
 * @property bookmarkCount Number of bookmarks
 * @property episodeCount Total number of episodes
 * @property dateCreated ISO 8601 timestamp when the title was created
 */
data class Title(
        val id: Int,
        val title: String?,
        val poster: String?,
        val synopsis: String?,
        @SerializedName("view_count") val viewCount: Int?,
        @SerializedName("bookmark_count") val bookmarkCount: Int?,
        @SerializedName("episode_count") val episodeCount: Int?,
        @SerializedName("date_created") val dateCreated: String?
) {
    /**
     * Full URL to the poster image.
     *
     * Constructs the URL using the base API URL and the poster asset UUID. Returns null if no
     * poster is set.
     */
    val posterUrl: String?
        get() = poster?.let { ApiConfig.getAssetUrl(it) }
}
