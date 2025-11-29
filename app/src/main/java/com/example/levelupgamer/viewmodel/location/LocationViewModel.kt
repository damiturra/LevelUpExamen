package com.example.levelupgamer.viewmodel.location

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LocationUiState(
    val lat: Double? = null,
    val lng: Double? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class LocationViewModel(app: Application) : AndroidViewModel(app) {
    private val _state = MutableStateFlow(LocationUiState())
    val state = _state.asStateFlow()

    private val fused by lazy {
        LocationServices.getFusedLocationProviderClient(getApplication<Application>())
    }

    @SuppressLint("MissingPermission")
    fun loadLastLocation() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        fused.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    _state.value = LocationUiState(lat = loc.latitude, lng = loc.longitude)
                } else {
                    _state.value = LocationUiState(error = "No hay última ubicación disponible")
                }
            }
            .addOnFailureListener { e ->
                _state.value = LocationUiState(error = e.message ?: "Error obteniendo ubicación")
            }
    }
}
