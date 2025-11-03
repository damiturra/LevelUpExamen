package com.example.levelupgamer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.levelupgamer.data.model.QrResult
import com.example.levelupgamer.data.repository.QrRepository

data class QrUiState(
    val result: QrResult? = null,
    val isProcessing: Boolean = false,
    val error: String? = null
)

class QrViewModel : ViewModel() {

    private val repository = QrRepository()

    var uiState by mutableStateOf(QrUiState())
        private set

    fun onQrScanned(content: String) {
        // podrías validar el contenido aquí
        val processed = repository.processQrContent(content)
        uiState = uiState.copy(
            result = processed,
            isProcessing = false,
            error = null
        )
    }

    fun clearResult() {
        uiState = QrUiState()
    }
}
