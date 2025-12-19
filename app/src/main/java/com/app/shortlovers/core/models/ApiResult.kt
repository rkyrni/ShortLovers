package com.app.shortlovers.core.models

/** Generic API result wrapper from Directus */
data class ApiResult<T>(val meta: Meta? = null, val data: T? = null)

data class Meta(val filterCount: Int? = null, val totalCount: Int? = null)

// ============================================
// Directus Error Response Models
// ============================================

/** Directus error response structure */
data class DirectusErrorResponse(val errors: List<DirectusError>?)

data class DirectusError(val message: String?, val extensions: DirectusErrorExtensions?)

data class DirectusErrorExtensions(
        val code: String?, // FORBIDDEN, INVALID_TOKEN, etc.
        val reason: String?, // Additional context for some errors
        val path: String? // For ROUTE_NOT_FOUND errors
)

// ============================================
// Network Result Sealed Class
// ============================================

/** Type-safe result wrapper for API calls */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: DirectusApiException) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/** Directus error codes enum */
enum class DirectusErrorCode(val httpStatus: Int) {
    FAILED_VALIDATION(400),
    FORBIDDEN(403),
    INVALID_TOKEN(403),
    TOKEN_EXPIRED(401),
    INVALID_CREDENTIALS(401),
    INVALID_IP(401),
    INVALID_OTP(401),
    INVALID_PAYLOAD(400),
    INVALID_QUERY(400),
    UNSUPPORTED_MEDIA_TYPE(415),
    REQUESTS_EXCEEDED(429),
    ROUTE_NOT_FOUND(404),
    SERVICE_UNAVAILABLE(503),
    UNPROCESSABLE_CONTENT(422),
    NETWORK_ERROR(0),
    UNKNOWN(-1);

    companion object {
        fun fromCode(code: String?): DirectusErrorCode {
            return entries.find { it.name == code } ?: UNKNOWN
        }
    }
}

/** Custom exception for Directus API errors */
class DirectusApiException(
        val code: DirectusErrorCode,
        override val message: String,
        val reason: String? = null,
        val path: String? = null,
        val httpStatusCode: Int? = null
) : Exception(message) {

    /** Get user-friendly message in Indonesian */
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
