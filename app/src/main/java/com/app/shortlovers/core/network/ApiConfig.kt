package com.app.shortlovers.core.network

/**
 * Central API configuration for the ShortLovers app.
 *
 * Contains base URL and other API-related constants used throughout the app.
 */
object ApiConfig {
    /** Base URL for the Shortorya API. */
    const val BASE_URL = "https://app.shortorya.com"

    /**
     * Constructs the full URL for an asset (image/video).
     *
     * @param assetId The UUID of the asset
     * @return Full URL to access the asset
     */
    fun getAssetUrl(assetId: String): String = "$BASE_URL/assets/$assetId"
}
