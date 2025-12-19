package com.app.shortlovers.core.network

import com.app.shortlovers.core.models.MainResponse
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object that provides configured Retrofit instance for API communication.
 *
 * This object configures and provides a shared instance of [ApiService] with:
 * - Custom error handling via [DirectusErrorInterceptor]
 * - Timeout configurations (30 seconds for connect/read/write)
 * - Gson converter with snake_case to camelCase mapping
 * - Custom deserializer for polymorphic [MainResponse]
 *
 * ## Configuration Details
 *
 * ### OkHttp Client
 * ```
 * ┌─────────────────────────────────────┐
 * │      OkHttpClient                   │
 * ├─────────────────────────────────────┤
 * │ • DirectusErrorInterceptor          │
 * │   (Parses error responses)          │
 * │                                     │
 * │ • Connect Timeout: 30s              │
 * │ • Read Timeout: 30s                 │
 * │ • Write Timeout: 30s                │
 * └─────────────────────────────────────┘
 * ```
 *
 * ### Gson Converter
 * ```
 * ┌─────────────────────────────────────┐
 * │      Gson Configuration             │
 * ├─────────────────────────────────────┤
 * │ • Field Naming: LOWER_CASE_WITH_    │
 * │   UNDERSCORES (snake_case)          │
 * │                                     │
 * │ • Date Format: yyyy-MM-dd'T'HH:mm:ss│
 * │                                     │
 * │ • Lenient: true (tolerates malformed│
 * │   JSON)                             │
 * │                                     │
 * │ • Custom Deserializer:              │
 * │   MainResponseDeserializer          │
 * │   (Handles polymorphic data field)  │
 * └─────────────────────────────────────┘
 * ```
 *
 * ## Usage
 *
 * ```kotlin
 * // In ViewModel
 * class MyViewModel : ViewModel() {
 *     fun fetchData() {
 *         viewModelScope.launch {
 *             val result = safeApiCall { RetrofitInstance.api.getData() }
 *             // Handle result...
 *         }
 *     }
 * }
 * ```
 *
 * ## Base URL
 *
 * The base URL is set to `https://app.shortlovers.id`. All API endpoints defined in [ApiService]
 * are relative to this base URL.
 *
 * @see ApiService
 * @see DirectusErrorInterceptor
 * @see MainResponseDeserializer
 * @see safeApiCall
 */
object RetrofitInstance {

    /**
     * Configured OkHttpClient with error interceptor and timeouts.
     *
     * This client:
     * - Intercepts all responses with [DirectusErrorInterceptor]
     * - Times out after 30 seconds for connection, read, and write operations
     */
    private val okHttpClient: OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(DirectusErrorInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    /**
     * Gson instance with custom configuration.
     *
     * Configured with:
     * - snake_case to camelCase conversion
     * - ISO 8601 date format
     * - Lenient parsing
     * - Custom deserializer for [MainResponse]
     */
    private val gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setLenient()
            .registerTypeAdapter(MainResponse::class.java, MainResponseDeserializer())
            .create()

    /**
     * Gson converter factory used by Retrofit.
     *
     * This factory is exposed for potential reuse in other contexts.
     */
    val gsonFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

    /**
     * Lazy-initialized Retrofit API service instance.
     *
     * This is the main entry point for making API calls. Use this instance throughout the app
     * via [safeApiCall].
     *
     * @see ApiService
     * @see safeApiCall
     */
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://app.shortlovers.id")
            .client(okHttpClient)
            .addConverterFactory(gsonFactory)
            .build()
            .create(ApiService::class.java)
    }
}
