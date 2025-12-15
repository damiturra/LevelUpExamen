package com.example.levelupgamer.viewmodel.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelupgamer.data.database.AppDatabase
import com.example.levelupgamer.data.repository.UsuarioRepository
import com.example.levelupgamer.viewmodel.RegistroViewModel

class RegistroVMFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            val db = AppDatabase.getDatabase(app)
            val repo = UsuarioRepository(db.usuarioDao())
            return RegistroViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}
