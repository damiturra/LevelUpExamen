package com.example.levelupgamer.viewmodel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelupgamer.App
import com.example.levelupgamer.data.database.AppDatabase
import com.example.levelupgamer.data.repository.ProductoRepository
import com.example.levelupgamer.view.ProductoViewModel

class ProductoVMFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getDatabase(App.instance)
        val repo = ProductoRepository(db.productoDao(), db.categoriaDao())
        return ProductoViewModel(repo) as T
    }
}
