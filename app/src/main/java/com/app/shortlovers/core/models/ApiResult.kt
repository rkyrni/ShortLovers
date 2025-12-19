package com.app.shortlovers.core.models

/**
 * Generic API result wrapper from Directus.
 *
 * Wraps all successful API responses with optional metadata.
 *
 * @param T The type of data returned by the API
 * @property meta Optional metadata about the response (e.g., pagination info)
 * @property data The actual data returned by the API
 *
 * @sample
 * ```kotlin
 * val result: ApiResult<List<User>> = api.getUsers()
 * val users = result.data // List<User>?
 * val totalCount = result.meta?.totalCount // Int?
 * ```
 */
data class ApiResult<T>(val meta: Meta? = null, val data: T? = null)

/**
 * Metadata about an API response.
 *
 * Contains pagination and filtering information.
 *
 * @property filterCount Number of items after filtering
 * @property totalCount Total number of items available
 */
data class Meta(val filterCount: Int? = null, val totalCount: Int? = null)

// ============================================
// Directus Error Response Models
// ============================================

/**
 * Directus error response structure.
 *
 * Directus returns errors in the following format:
 * ```json
 * {
 *   "errors": [
 *     {
 *       "message": "Error message",
 *       "extensions": {
 *         "code": "ERROR_CODE",
 *         "reason": "Additional context"
 *       }
 *     }
 *   ]
 * }
 * ```
 *
 * @property errors List of error objects
 */
data class DirectusErrorResponse(val errors: List<DirectusError>?)

/**
 * Individual error object from Directus.
 *
 * @property message Human-readable error message
 * @property extensions Additional error metadata
 */
data class DirectusError(val message: String?, val extensions: DirectusErrorExtensions?)

/**
 * Additional error metadata from Directus.
 *
 * @property code Error code (e.g., "FORBIDDEN", "INVALID_TOKEN")
 * @property reason Additional context for some errors
 * @property path API path for ROUTE_NOT_FOUND errors
 */
data class DirectusErrorExtensions(val code: String?, val reason: String?, val path: String?)

// ============================================
// Network Result Sealed Class
// ============================================

/**
 * Type-safe result wrapper for API calls.
 *
 * Use this sealed class to handle API responses in a type-safe manner with proper error handling
 * and loading states.
 *
 * @param T The type of data expected on success
 *
 * @sample
 * ```kotlin
 * val result = safeApiCall { api.getData() }
 * when (result) {
 *     is NetworkResult.Success -> handleData(result.data)
 *     is NetworkResult.Error -> showError(result.exception.getUserFriendlyMessage())
 *     is NetworkResult.Loading -> showLoading()
 * }
 * ```
 */
sealed class NetworkResult<out T> {
    /** Successful API response. @property data The data returned by the API */
    data class Success<T>(val data: T) : NetworkResult<T>()

    /**
     * Error response from API or network. @property exception The exception containing error
     * details
     */
    data class Error(val exception: DirectusApiException) : NetworkResult<Nothing>()

    /** Loading state (can be used before making the API call) */
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Directus error codes enum.
 *
 * Maps Directus error codes to HTTP status codes. All codes are based on official Directus
 * documentation.
 *
 * @property httpStatus The HTTP status code associated with this error
 *
 * @see <a href="https://docs.directus.io/reference/errors.html">Directus Error Reference</a>
 */
enum class DirectusErrorCode(val httpStatus: Int) {
    /** Validation for this particular item failed (400) */
    FAILED_VALIDATION(400),

    /** You are not allowed to do the current action (403) */
    FORBIDDEN(403),

    /** Provided token is invalid (403) */
    INVALID_TOKEN(403),

    /** Provided token is valid but has expired (401) */
    TOKEN_EXPIRED(401),

    /** Username/password or access token is wrong (401) */
    INVALID_CREDENTIALS(401),

    /** Your IP address isn't allow-listed (401) */
    INVALID_IP(401),

    /** Incorrect OTP was provided (401) */
    INVALID_OTP(401),

    /** Provided payload is invalid (400) */
    INVALID_PAYLOAD(400),

    /** The requested query parameters cannot be used (400) */
    INVALID_QUERY(400),

    /** Provided payload format or Content-Type header is unsupported (415) */
    UNSUPPORTED_MEDIA_TYPE(415),

    /** You have exceeded the rate limit (429) */
    REQUESTS_EXCEEDED(429),

    /** Endpoint does not exist (404) */
    ROUTE_NOT_FOUND(404),

    /** Could not use external service (503) */
    SERVICE_UNAVAILABLE(503),

    /** You tried doing something illegal (422) */
    UNPROCESSABLE_CONTENT(422),

    /** Network connectivity issues (0) */
    NETWORK_ERROR(0),

    /** Unknown or unhandled error (-1) */
    UNKNOWN(-1);

    companion object {
        /**
         * Converts a Directus error code string to enum value.
         *
         * @param code The error code string from Directus API
         * @return The corresponding [DirectusErrorCode], or [UNKNOWN] if not found
         */
        fun fromCode(code: String?): DirectusErrorCode {
            return entries.find { it.name == code } ?: UNKNOWN
        }
    }
}

/**
 * Custom exception for Directus API errors.
 *
 * This exception is thrown by [DirectusErrorInterceptor] when the API returns an error response. It
 * contains structured error information and provides user-friendly messages in Indonesian.
 *
 * @property code The Directus error code enum
 * @property message The raw error message from the API
 * @property reason Additional context about the error (optional)
 * @property path The API path that caused the error (optional)
 * @property httpStatusCode The HTTP status code (optional)
 *
 * @sample
 * ```kotlin
 * try {
 *     val data = api.getData()
 * } catch (e: DirectusApiException) {
 *     when (e.code) {
 *         DirectusErrorCode.TOKEN_EXPIRED -> navigateToLogin()
 *         DirectusErrorCode.FORBIDDEN -> showAccessDenied()
 *         else -> showError(e.getUserFriendlyMessage())
 *     }
 * }
 * ```
 */
class DirectusApiException(
    val code: DirectusErrorCode,
    override val message: String,
    val reason: String? = null,
    val path: String? = null,
    val httpStatusCode: Int? = null
) : Exception(message) {

    /**
     * Returns a user-friendly error message in Indonesian.
     *
     * Maps technical error codes to human-readable messages that can be displayed directly to
     * users.
     *
     * @return Indonesian error message suitable for displaying to users
     */
    fun getUserFriendlyMessage(): String {
        return when (code) {
            DirectusErrorCode.FORBIDDEN -> "Akses ditolak. Anda tidak memiliki izin."
            DirectusErrorCode.INVALID_TOKEN -> "Token tidak valid. Silakan login kembali."
            DirectusErrorCode.TOKEN_EXPIRED -> "Sesi telah berakhir. Silakan login kembali."
            DirectusErrorCode.INVALID_CREDENTIALS -> "Email atau password salah."
            DirectusErrorCode.INVALID_IP -> "Alamat IP tidak diizinkan."
            DirectusErrorCode.INVALID_OTP -> "Kode OTP salah."
            DirectusErrorCode.INVALID_PAYLOAD -> "Data yang dikirim tidak valid."
            DirectusErrorCode.INVALID_QUERY -> "Permintaan tidak valid."
            DirectusErrorCode.FAILED_VALIDATION -> "Validasi gagal. Periksa kembali data Anda."
            DirectusErrorCode.UNSUPPORTED_MEDIA_TYPE -> "Format file tidak didukung."
            DirectusErrorCode.REQUESTS_EXCEEDED -> "Terlalu banyak permintaan. Coba lagi nanti."
            DirectusErrorCode.ROUTE_NOT_FOUND -> "Endpoint tidak ditemukan."
            DirectusErrorCode.SERVICE_UNAVAILABLE -> "Layanan sedang tidak tersedia."
            DirectusErrorCode.UNPROCESSABLE_CONTENT -> "Permintaan tidak dapat diproses."
            DirectusErrorCode.NETWORK_ERROR ->
                "Gagal terhubung ke server. Periksa koneksi internet."

            DirectusErrorCode.UNKNOWN -> reason ?: message
        }
    }

    companion object {
        fun fromDirectusError(error: DirectusError, httpStatus: Int? = null): DirectusApiException {
            val code = DirectusErrorCode.fromCode(error.extensions?.code)
            return DirectusApiException(
                code = code,
                message = error.message ?: "Unknown error",
                reason = error.extensions?.reason,
                path = error.extensions?.path,
                httpStatusCode = httpStatus
            )
        }

        fun networkError(cause: Throwable): DirectusApiException {
            return DirectusApiException(
                code = DirectusErrorCode.NETWORK_ERROR,
                message = cause.message ?: "Network error",
                reason = cause.localizedMessage
            )
        }

        fun unknown(message: String): DirectusApiException {
            return DirectusApiException(code = DirectusErrorCode.UNKNOWN, message = message)
        }
    }
}
