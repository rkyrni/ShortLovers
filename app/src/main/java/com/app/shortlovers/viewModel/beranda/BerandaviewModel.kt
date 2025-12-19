package com.app.shortlovers.viewModel.beranda

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shortlovers.core.models.MainResponse
import com.app.shortlovers.core.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BerandaViewModel : ViewModel() {
    private val _mainData: MutableState<List<MainResponse>?> = mutableStateOf(null)
    val mainData: State<List<MainResponse>?> = _mainData

    private val _greeting = MutableStateFlow("Loading...")
    val greeting = _greeting.asStateFlow()

    val LogTag = BerandaViewModel::class.simpleName

    init {
        // Simulate loading data
        //        viewModelScope.launch {
        //            delay(1000)
        //            _greeting.value = "Welcome to Home Page 🏠"
        //        }
        fetchMainData()
        Log.d(LogTag, "BerandaViewModel initialized")
    }

    private fun fetchMainData() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMainData()
                _mainData.value = response.data

                Log.d(LogTag, "BerandaViewModel fetchMainData success")
                Log.d(LogTag, "Response tabs count: ${response.data?.size ?: 0}")
                response.data?.forEach { tab ->
                    // Check which type of content this tab has
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
                        tab.dramas.take(3).forEach { drama ->
                            Log.d(LogTag, "  Drama: ${drama.title}")
                        }
                        if (tab.dramas.size > 3) {
                            Log.d(LogTag, "  ... and ${tab.dramas.size - 3} more")
                        }
                    } else {
                        Log.d(LogTag, "Tab: ${tab.tabName}, empty")
                    }
                }
            } catch (e: Exception) {
                Log.e(LogTag, "BerandaViewModel fetchMainData error: ${e.message}")
            }
        }
    }
}
