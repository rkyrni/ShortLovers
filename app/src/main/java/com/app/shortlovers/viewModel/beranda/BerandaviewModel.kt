package com.app.shortlovers.viewModel.beranda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BerandaViewModel : ViewModel() {

    private val _greeting = MutableStateFlow("Loading...")
    val greeting = _greeting.asStateFlow()

    init {
        // Simulate loading data
        viewModelScope.launch {
            delay(1000)
            _greeting.value = "Welcome to Home Page 🏠"
        }
    }
}