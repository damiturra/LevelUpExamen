package com.example.levelupgamer.viewmodel.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelupgamer.data.database.AppDatabase
import com.example.levelupgamer.data.repository.CompraRepository
import com.example.levelupgamer.viewmodel.HistorialComprasViewModel

class HistorialComprasVMFactory(
    private val application: Application,
    private val userId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistorialComprasViewModel::class.java)) {
            val db = AppDatabase.getDatabase(application)
            val compraDao = db.compraDao()
            val repo = CompraRepository(compraDao)
            @Suppress("UNCHECKED_CAST")
            return HistorialComprasViewModel(repo, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
