package com.example.levelupgamer.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelupgamer.App
import com.example.levelupgamer.data.database.AppDatabase
import com.example.levelupgamer.data.repository.CarritoRepository
import com.example.levelupgamer.view.CarritoViewModel

class CarritoVMFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getDatabase(App.instance)
        val repo = CarritoRepository(db.carritoDao())
        return CarritoViewModel(repo) as T
    }
}
