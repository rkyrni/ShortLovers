package com.app.shortlovers.core.network

import com.app.shortlovers.core.models.DirectusApiException
import com.app.shortlovers.core.models.DirectusErrorCode
import com.app.shortlovers.core.models.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Safe API call wrapper that handles exceptions and returns [NetworkResult].
 *
 * This suspend function wraps API calls to provide type-safe error handling. It executes the API
 * call on the IO dispatcher and catches all exceptions, converting them to appropriate
 * [NetworkResult] states.
 *
 * ## Exception Handling
 *
 * The function catches and converts the following exceptions:
 *
 * | Exception Type | Converted To | Description | |----------------|--------------|-------------| |
 * [DirectusApiException] | NetworkResult.Error | API returned error response (caught by
 * interceptor) | | [UnknownHostException] | NetworkResult.Error | No internet connection / DNS
 * resolution failed | | [SocketTimeoutException] | NetworkResult.Error | Connection or read timeout
 * | | [IOException] | NetworkResult.Error | Other network I/O errors | | [Exception] |
 * NetworkResult.Error | Unexpected errors |
 *
 * ## Usage in ViewModel
 *
 * ```kotlin
 * class MyViewModel : ViewModel() {
 *     fun fetchData() {
 *         viewModelScope.launch {
 *             _isLoading.value = true
 *
 *             when (val result = safeApiCall { api.getData() }) {
 *                 is NetworkResult.Success -> {
 *                     _data.value = result.data
 *                 }
 *                 is NetworkResult.Error -> {
 *                     handleError(result.exception)
 *                 }
 *                 is NetworkResult.Loading -> {
 *                     // Not used in this pattern
 *                 }
 *             }
 *
 *             _isLoading.value = false
 *         }
 *     }
 *
 *     private fun handleError(exception: DirectusApiException) {
 *         when (exception.code) {
 *             DirectusErrorCode.TOKEN_EXPIRED -> navigateToLogin()
 *             DirectusErrorCode.NETWORK_ERROR -> showRetryDialog()
 *             else -> showError(exception.getUserFriendlyMessage())
 *         }
 *     }
 * }
 * ```
 *
 * ## Benefits
 *
 * - **Type Safety**: Sealed class ensures exhaustive when statements
 * - **Consistent Error Handling**: All exceptions converted to DirectusApiException
 * - **User-Friendly Messages**: Built-in Indonesian error messages via getUserFriendlyMessage()
 * - **Network Awareness**: Specific handling for connectivity issues
 *
 * @param T The expected return type of the API call
 * @param apiCall A suspend lambda that performs the actual API call
 * @return [NetworkResult.Success] with data on success, or [NetworkResult.Error] on failure
 *
 * @see NetworkResult
 * @see DirectusApiException
 * @see DirectusErrorInterceptor
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return withContext(Dispatchers.IO) {
        try {
            // Execute API call and wrap result in Success
            NetworkResult.Success(apiCall())
        } catch (e: DirectusApiException) {
            // API error already parsed by DirectusErrorInterceptor
            NetworkResult.Error(e)
        } catch (e: UnknownHostException) {
            // DNS resolution failed - likely no internet connection
            NetworkResult.Error(
                DirectusApiException(
                    code = DirectusErrorCode.NETWORK_ERROR,
                    message = "No internet connection",
                    reason = "Unable to resolve host"
                )
            )
        } catch (e: SocketTimeoutException) {
            // Connection or read timeout exceeded
            NetworkResult.Error(
                DirectusApiException(
                    code = DirectusErrorCode.NETWORK_ERROR,
                    message = "Connection timed out",
                    reason = "Server took too long to respond"
                )
            )
        } catch (e: IOException) {
            // Other network I/O errors
            NetworkResult.Error(DirectusApiException.networkError(e))
        } catch (e: Exception) {
            // Unexpected errors - wrap as unknown error
            NetworkResult.Error(DirectusApiException.unknown(e.message ?: "Unknown error"))
        }
    }
}
