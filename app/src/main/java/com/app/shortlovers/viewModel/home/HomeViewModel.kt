package com.app.shortlovers.viewModel.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shortlovers.core.models.DirectusApiException
import com.app.shortlovers.core.models.DirectusErrorCode
import com.app.shortlovers.core.models.MainResponse
import com.app.shortlovers.core.models.NetworkResult
import com.app.shortlovers.core.network.RetrofitInstance
import com.app.shortlovers.core.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _mainData: MutableState<List<MainResponse>?> = mutableStateOf(null)
    val mainData: State<List<MainResponse>?> = _mainData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<DirectusApiException?>(null)
    val error: StateFlow<DirectusApiException?> = _error.asStateFlow()

    private val _greeting = MutableStateFlow("Loading...")
    val greeting = _greeting.asStateFlow()

    val LogTag = HomeViewModel::class.simpleName

    init {
        fetchMainData()
        Log.d(LogTag, "HomeViewModel initialized")
    }

    fun fetchMainData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = safeApiCall { RetrofitInstance.api.getMainData() }) {
                is NetworkResult.Success -> {
                    _mainData.value = result.data.data
                    Log.d(LogTag, "fetchMainData success")
                    logResponseDetails(result.data.data)
                }

                is NetworkResult.Error -> {
                    _error.value = result.exception
                    handleError(result.exception)
                }

                is NetworkResult.Loading -> {
                    // Already handled by _isLoading
                }
            }

            _isLoading.value = false
        }
    }

    private fun handleError(exception: DirectusApiException) {
        Log.e(LogTag, "fetchMainData error: ${exception.code} - ${exception.message}")
        Log.e(LogTag, "User message: ${exception.getUserFriendlyMessage()}")

        // Handle specific error codes
        when (exception.code) {
            DirectusErrorCode.TOKEN_EXPIRED, DirectusErrorCode.INVALID_TOKEN -> {
                // TODO: Navigate to login or refresh token
                Log.w(LogTag, "Token issue - should redirect to login")
            }

            DirectusErrorCode.FORBIDDEN -> {
                Log.w(LogTag, "Access denied")
            }

            DirectusErrorCode.NETWORK_ERROR -> {
                Log.w(LogTag, "Network error - check internet connection")
            }

            else -> {
                Log.w(LogTag, "Unhandled error code: ${exception.code}")
            }
        }
    }

    private fun logResponseDetails(data: List<MainResponse>?) {
        Log.d(LogTag, "Response tabs count: ${data?.size ?: 0}")
        data?.forEach { tab ->
            if (tab.categories != null && tab.categories.isNotEmpty()) {
                Log.d(LogTag, "Tab: ${tab.tabName}, categories: ${tab.categories.size}")
                tab.categories.forEach { category ->
                    Log.d(
                        LogTag,
                        "  Category: ${category.categoryName}, dramas: ${category.dramas?.size ?: 0}"
                    )
                }
            } else if (tab.dramas != null && tab.dramas.isNotEmpty()) {
                Log.d(LogTag, "Tab: ${tab.tabName}, dramas: ${tab.dramas.size}")
                tab.dramas.take(3).forEach { drama -> Log.d(LogTag, "  Drama: ${drama.title}") }
                if (tab.dramas.size > 3) {
                    Log.d(LogTag, "  ... and ${tab.dramas.size - 3} more")
                }
            } else {
                Log.d(LogTag, "Tab: ${tab.tabName}, empty")
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
