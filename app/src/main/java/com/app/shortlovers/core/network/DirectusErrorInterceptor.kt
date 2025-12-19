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
 * Parses error responses and throws [DirectusApiException] with proper error codes.
 */
class DirectusErrorInterceptor : Interceptor {

    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            val errorBody = response.peekBody(Long.MAX_VALUE).string()

            try {
                val errorResponse = gson.fromJson(errorBody, DirectusErrorResponse::class.java)
                val firstError = errorResponse?.errors?.firstOrNull()

                if (firstError != null) {
                    throw DirectusApiException.fromDirectusError(firstError, response.code)
                } else {
                    throw DirectusApiException(
                            code = DirectusErrorCode.UNKNOWN,
                            message = "HTTP ${response.code}: ${response.message}",
                            httpStatusCode = response.code
                    )
                }
            } catch (e: DirectusApiException) {
                throw e
            } catch (e: Exception) {
                // JSON parsing failed, create generic error
                throw DirectusApiException(
                        code = DirectusErrorCode.UNKNOWN,
                        message = "HTTP ${response.code}: ${response.message}",
                        httpStatusCode = response.code
                )
            }
        }

        return response
    }
}
