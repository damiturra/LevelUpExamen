package com.example.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelupgamer.data.model.CompraConItems
import com.example.levelupgamer.data.repository.CompraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HistorialComprasUiState(
    val isLoading: Boolean = true,
    val compras: List<CompraConItems> = emptyList(),
    val error: String? = null
)

class HistorialComprasViewModel(
    private val compraRepository: CompraRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialComprasUiState())
    val uiState: StateFlow<HistorialComprasUiState> = _uiState.asStateFlow()

    init {
        cargarHistorial()
    }

    private fun cargarHistorial() {
        viewModelScope.launch {
            try {
                compraRepository
                    .observeHistorial(userId)
                    .collectLatest { lista ->
                        _uiState.value = HistorialComprasUiState(
                            isLoading = false,
                            compras = lista,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = HistorialComprasUiState(
                    isLoading = false,
                    compras = emptyList(),
                    error = e.message ?: "Error al cargar historial"
                )
            }
        }
    }
}
