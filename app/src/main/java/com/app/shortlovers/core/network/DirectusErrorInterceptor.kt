package com.app.shortlovers.core.network

import com.app.shortlovers.core.models.DirectusApiException
import com.app.shortlovers.core.models.DirectusErrorCode
import com.app.shortlovers.core.models.DirectusErrorResponse
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor to handle Directus API error responses.
 *
 * This interceptor intercepts all HTTP responses and checks for error status codes. When an error
 * is detected (HTTP status >= 400), it:
 * 1. Reads the response body
 * 2. Attempts to parse it as a Directus error response
 * 3. Throws a [DirectusApiException] with structured error information
 *
 * ## Error Handling Flow
 *
 * ```
 * HTTP Response
 *     │
 *     ├─ Success (2xx) ──> Pass through
 *     │
 *     └─ Error (4xx/5xx)
 *         │
 *         ├─ Parse JSON error response
 *         │   │
 *         │   ├─ Success ──> Throw DirectusApiException with error code
 *         │   │
 *         │   └─ Failed ──> Throw DirectusApiException with UNKNOWN code
 *         │
 *         └─ Caught by safeApiCall() and converted to NetworkResult.Error
 * ```
 *
 * ## Usage
 *
 * This interceptor is automatically registered in [RetrofitInstance]:
 * ```kotlin
 * val okHttpClient = OkHttpClient.Builder()
 *     .addInterceptor(DirectusErrorInterceptor())
 *     .build()
 * ```
 *
 * ## Exception Propagation
 *
 * The thrown [DirectusApiException] will be caught by:
 * - [safeApiCall] helper function → converts to [NetworkResult.Error]
 * - ViewModels → handle error states appropriately
 *
 * @see DirectusApiException
 * @see safeApiCall
 * @see RetrofitInstance
 */
class DirectusErrorInterceptor : Interceptor {

    /** Gson instance for parsing error responses */
    private val gson = Gson()

    /**
     * Intercepts the HTTP response and handles errors.
     *
     * @param chain The interceptor chain
     * @return The original response if successful
     * @throws DirectusApiException if the response indicates an error
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Only process error responses (HTTP status >= 400)
        if (!response.isSuccessful) {
            // Use peekBody to read without consuming the response
            val errorBody = response.peekBody(Long.MAX_VALUE).string()

            try {
                // Attempt to parse Directus error format
                val errorResponse = gson.fromJson(errorBody, DirectusErrorResponse::class.java)
                val firstError = errorResponse?.errors?.firstOrNull()

                if (firstError != null) {
                    // Successfully parsed Directus error - throw with structured info
                    throw DirectusApiException.fromDirectusError(firstError, response.code)
                } else {
                    // Response body doesn't contain expected error format
                    throw DirectusApiException(
                        code = DirectusErrorCode.UNKNOWN,
                        message = "HTTP ${response.code}: ${response.message}",
                        httpStatusCode = response.code
                    )
                }
            } catch (e: DirectusApiException) {
                // Re-throw DirectusApiException as-is
                throw e
            } catch (e: Exception) {
                // JSON parsing failed - create generic error
                throw DirectusApiException(
                    code = DirectusErrorCode.UNKNOWN,
                    message = "HTTP ${response.code}: ${response.message}",
                    httpStatusCode = response.code
                )
            }
        }

        // Return successful response unchanged
        return response
    }
}
