package com.app.shortlovers.viewModel.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class StartedViewModel : ViewModel() {

    // State untuk menyimpan status centang checkbox
    private val _isChecked = mutableStateOf(false)
    val isChecked: State<Boolean> = _isChecked

    // Fungsi untuk mengubah status centang
    fun onCheckboxChange(checked: Boolean) {
        _isChecked.value = checked
    }
}
