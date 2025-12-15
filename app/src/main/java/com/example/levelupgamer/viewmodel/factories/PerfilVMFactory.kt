package com.example.levelupgamer.viewmodel.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.levelupgamer.data.database.AppDatabase
import com.example.levelupgamer.data.repository.UsuarioRepository
import com.example.levelupgamer.viewmodel.PerfilViewModel

class PerfilVMFactory(
    private val app: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            val db   = AppDatabase.getDatabase(app)
            val repo = UsuarioRepository(db.usuarioDao())
            return PerfilViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}
