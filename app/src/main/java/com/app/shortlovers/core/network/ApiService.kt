package com.app.shortlovers.core.network

import com.app.shortlovers.core.models.ApiResult
import com.app.shortlovers.core.models.Title
import com.app.shortlovers.core.models.TitleGroup
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service interface for the Shortorya API.
 *
 * All endpoints follow the Directus API structure. See [ApiConfig] for base URL configuration.
 */
interface ApiService {

        /**
         * Fetches all published title groups.
         *
         * Title groups are used as tabs in the home screen.
         *
         * @param fields Comma-separated list of fields to return
         * @param status Filter by status (default: published)
         * @return List of [TitleGroup] wrapped in [ApiResult]
         */
        @GET("items/title_groups")
        suspend fun getTitleGroups(
                @Query("fields") fields: String = "id,name,key",
                @Query("filter[status][_eq]") status: String = "published"
        ): ApiResult<List<TitleGroup>>

        /**
         * Fetches titles with optional filtering.
         *
         * @param fields Comma-separated list of fields to return
         * @param status Filter by status (default: published)
         * @param language Filter by language (default: "id" for Indonesian)
         * @param group Filter by title group ID (optional)
         * @param sort Sort order (e.g., "-date_created" for newest first)
         * @param limit Maximum number of items to return
         * @return List of [Title] wrapped in [ApiResult]
         */
        @GET("items/titles")
        suspend fun getTitles(
                @Query("fields")
                fields: String =
                        "id,title,poster,synopsis,view_count,bookmark_count,episode_count,date_created",
                @Query("filter[status][_eq]") status: String = "published",
                @Query("filter[language][_eq]") language: String = "id",
                @Query("filter[group][_eq]") group: Int? = null,
                @Query("sort") sort: String? = null,
                @Query("limit") limit: Int = 50
        ): ApiResult<List<Title>>
}
