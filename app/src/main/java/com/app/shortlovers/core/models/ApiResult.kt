package com.app.shortlovers.core.models

class ApiResult<T> {
    // The meta returned by the API
    var meta: Meta? = null

    // The data returned by the API
    var data: T? = null
}

class Meta {
    var filterCount: Int? = null
    var totalCount: Int? = null
}

/**
 * Class is used to represent the result of an API call that returns an error
 */
class ErrorApiResult {
    var errors: List<HttpError>? = null
}

/**
 * Class is used to represent the error returned by the API
 */
class HttpError {
    var message: String? = null
    var code: String? = null
    var extensions: HttpErrorExtension? = null
}

/**
 * Class is used to represent the extensions of the error returned by the API
 */
class HttpErrorExtension {
    var message: String? = null
    var code: String? = null
    var error: String? = null
}