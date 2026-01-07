package com.app.shortlovers.core.models

import com.google.gson.annotations.SerializedName

/**
 * Represents a tab in the main response. Note: The `data` field in JSON can be either:
 * - List of categories (for Utama, Terbaru, Populer tabs)
 * - List of dramas directly (for Semua tab)
 *
 * We use custom deserializer to populate either `categories` or `dramas` based on content.
 */
data class MainResponse(
    @SerializedName("tab_name") val tabName: String?,

    // For tabs with categorized content (Utama, Terbaru, Populer)
    val categories: List<MainResponseCategory>? = null,

    // For tabs with direct drama list (Semua)
    val dramas: List<MainResponseDrama>? = null
)

data class MainResponseCategory(
    @SerializedName("category_name") val categoryName: String?,
    val dramas: List<MainResponseDrama>?
)

data class MainResponseDrama(
    val id: String? = null,
    val title: String? = null,
    @SerializedName("cover_link") val coverLink: String? = null,
    val cover: String? = null,
    val category: MainResponseDramaCategory? = null,
)

data class MainResponseDramaCategory(val name: String?)
