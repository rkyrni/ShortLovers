package com.app.shortlovers.viewModel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shortlovers.core.models.DirectusApiException
import com.app.shortlovers.core.models.DirectusErrorCode
import com.app.shortlovers.core.models.NetworkResult
import com.app.shortlovers.core.models.TabItem
import com.app.shortlovers.core.models.Title
import com.app.shortlovers.core.models.TitleGroup
import com.app.shortlovers.core.network.RetrofitInstance
import com.app.shortlovers.core.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen.
 *
 * Manages the state for:
 * - Tabs (from title_groups API + "Terbaru" tab)
 * - Titles displayed in the content area
 * - Featured titles for the carousel
 * - Loading and error states
 */
class HomeViewModel : ViewModel() {

    // region State

    private val _tabs = MutableStateFlow<List<TabItem>>(emptyList())
    val tabs: StateFlow<List<TabItem>> = _tabs.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    private val _titles = MutableStateFlow<List<Title>>(emptyList())
    val titles: StateFlow<List<Title>> = _titles.asStateFlow()

    private val _featuredTitles = MutableStateFlow<List<Title>>(emptyList())
    val featuredTitles: StateFlow<List<Title>> = _featuredTitles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<DirectusApiException?>(null)
    val error: StateFlow<DirectusApiException?> = _error.asStateFlow()

    // endregion

    private val logTag = HomeViewModel::class.simpleName

    init {
        Log.d(logTag, "HomeViewModel initialized")
        fetchInitialData()
    }

    // region Public Methods

    /** Selects a tab by index and fetches the corresponding titles. */
    fun selectTab(index: Int) {
        if (index == _selectedTabIndex.value) return
        _selectedTabIndex.value = index
        _tabs.value.getOrNull(index)?.let { tab -> fetchTitlesForTab(tab) }
    }

    /** Refreshes all data - tabs, titles, and featured content. */
    fun refresh() {
        fetchInitialData()
    }

    /** Clears the current error state. */
    fun clearError() {
        _error.value = null
    }

    // endregion

    // region Private Methods

    /** Fetches initial data: tabs, titles for the first tab, and featured titles. */
    private fun fetchInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Fetch title groups for tabs
            when (val result = safeApiCall { RetrofitInstance.api.getTitleGroups() }) {
                is NetworkResult.Success -> {
                    val titleGroups = result.data.data ?: emptyList()
                    buildTabs(titleGroups)
                    Log.d(logTag, "Fetched ${titleGroups.size} title groups")
                }
                is NetworkResult.Error -> {
                    handleError(result.exception)
                    _isLoading.value = false
                    return@launch
                }
                is NetworkResult.Loading -> {
                    /* Already handled */
                }
            }

            // Fetch titles for the first tab
            _tabs.value.firstOrNull()?.let { firstTab -> fetchTitlesForTab(firstTab) }

            // Fetch featured titles for carousel
            fetchFeaturedTitles()

            _isLoading.value = false
        }
    }

    /** Builds the tab list from title groups + "Terbaru" tab. */
    private fun buildTabs(titleGroups: List<TitleGroup>) {
        val tabs = mutableListOf<TabItem>()

        // Add "Terbaru" as the first tab
        tabs.add(TabItem.Latest)

        // Add title groups as tabs
        titleGroups.forEach { group -> tabs.add(TabItem.Group(group)) }

        _tabs.value = tabs
        _selectedTabIndex.value = 0
        Log.d(logTag, "Built ${tabs.size} tabs")
    }

    /** Fetches titles based on the selected tab. */
    private fun fetchTitlesForTab(tab: TabItem) {
        viewModelScope.launch {
            Log.d(logTag, "Fetching titles for tab: ${tab.name}")

            val result =
                    when (tab) {
                        is TabItem.Latest -> {
                            // Fetch latest titles sorted by date_created descending
                            safeApiCall {
                                RetrofitInstance.api.getTitles(sort = "-date_created", limit = 50)
                            }
                        }
                        is TabItem.Group -> {
                            // Fetch titles for specific group
                            Log.d(
                                    logTag,
                                    "Fetching for group ID: ${tab.group.id}, name: ${tab.group.name}"
                            )
                            safeApiCall {
                                RetrofitInstance.api.getTitles(group = tab.group.id, limit = 50)
                            }
                        }
                    }

            when (result) {
                is NetworkResult.Success -> {
                    _titles.value = result.data.data ?: emptyList()
                    Log.d(logTag, "Fetched ${_titles.value.size} titles for ${tab.name}")
                }
                is NetworkResult.Error -> {
                    Log.e(logTag, "Error fetching titles: ${result.exception.message}")
                    // Don't show error for tab switch, just show empty
                    _titles.value = emptyList()
                }
                is NetworkResult.Loading -> {
                    /* Already handled */
                }
            }
        }
    }

    /**
     * Fetches featured titles for the carousel. Uses the latest 5 titles sorted by date_created.
     */
    private fun fetchFeaturedTitles() {
        viewModelScope.launch {
            when (val result = safeApiCall {
                        RetrofitInstance.api.getTitles(sort = "-date_created", limit = 5)
                    }
            ) {
                is NetworkResult.Success -> {
                    _featuredTitles.value = result.data.data ?: emptyList()
                    Log.d(logTag, "Fetched ${_featuredTitles.value.size} featured titles")
                }
                is NetworkResult.Error -> {
                    Log.e(logTag, "Error fetching featured titles: ${result.exception.message}")
                }
                is NetworkResult.Loading -> {
                    /* Already handled */
                }
            }
        }
    }

    /** Handles API errors and logs appropriate messages. */
    private fun handleError(exception: DirectusApiException) {
        _error.value = exception
        Log.e(logTag, "API Error: ${exception.code} - ${exception.message}")
        Log.e(logTag, "User message: ${exception.getUserFriendlyMessage()}")

        when (exception.code) {
            DirectusErrorCode.TOKEN_EXPIRED, DirectusErrorCode.INVALID_TOKEN -> {
                Log.w(logTag, "Token issue - should redirect to login")
            }
            DirectusErrorCode.FORBIDDEN -> {
                Log.w(logTag, "Access denied")
            }
            DirectusErrorCode.NETWORK_ERROR -> {
                Log.w(logTag, "Network error - check internet connection")
            }
            else -> {
                Log.w(logTag, "Unhandled error code: ${exception.code}")
            }
        }
    }

    // endregion
}
