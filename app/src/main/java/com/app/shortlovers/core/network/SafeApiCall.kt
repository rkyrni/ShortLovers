package com.app.shortlovers.core.network

import com.app.shortlovers.core.models.DirectusApiException
import com.app.shortlovers.core.models.DirectusErrorCode
import com.app.shortlovers.core.models.NetworkResult
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Safe API call wrapper that handles exceptions and returns [NetworkResult].
 *
 * Usage:
 * ```kotlin
 * val result = safeApiCall { api.getMainData() }
 * when (result) {
 *     is NetworkResult.Success -> handleSuccess(result.data)
 *     is NetworkResult.Error -> handleError(result.exception)
 *     is NetworkResult.Loading -> showLoading()
 * }
 * ```
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return withContext(Dispatchers.IO) {
        try {
            NetworkResult.Success(apiCall())
        } catch (e: DirectusApiException) {
            NetworkResult.Error(e)
        } catch (e: UnknownHostException) {
            NetworkResult.Error(
                    DirectusApiException(
                            code = DirectusErrorCode.NETWORK_ERROR,
                            message = "No internet connection",
                            reason = "Unable to resolve host"
                    )
            )
        } catch (e: SocketTimeoutException) {
            NetworkResult.Error(
                    DirectusApiException(
                            code = DirectusErrorCode.NETWORK_ERROR,
                            message = "Connection timed out",
                            reason = "Server took too long to respond"
                    )
            )
        } catch (e: IOException) {
            NetworkResult.Error(DirectusApiException.networkError(e))
        } catch (e: Exception) {
            NetworkResult.Error(DirectusApiException.unknown(e.message ?: "Unknown error"))
        }
    }
}
