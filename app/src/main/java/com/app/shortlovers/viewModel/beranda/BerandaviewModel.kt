package com.app.shortlovers.viewModel.beranda

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shortlovers.core.models.MainResponse
import com.app.shortlovers.core.network.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BerandaViewModel : ViewModel() {
    private val _mainData = mutableStateOf<List<MainResponse>>(emptyList())
    val mainData: State<List<MainResponse>> = _mainData

    private val _greeting = MutableStateFlow("Loading...")
    val greeting = _greeting.asStateFlow()

    init {
        // Simulate loading data
//        viewModelScope.launch {
//            delay(1000)
//            _greeting.value = "Welcome to Home Page 🏠"
//        }
        fetchMainData()
    }

    private fun fetchMainData() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMainData()
                _mainData.value = response

                print("berhasil")
            } catch (e: Exception) {
                print("error")
            }
        }
    }
}