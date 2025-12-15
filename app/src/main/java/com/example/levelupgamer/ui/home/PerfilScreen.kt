package com.example.levelupgamer.ui.home

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.viewmodel.PerfilViewModel
import com.example.levelupgamer.viewmodel.factories.PerfilVMFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    val app = LocalContext.current.applicationContext as Application
    val vm: PerfilViewModel = viewModel(factory = PerfilVMFactory(app))
    val ui = vm.uiState

    LaunchedEffect(Unit) { vm.cargarUsuario() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Nombre
            OutlinedTextField(
                value = ui.nombre,
                onValueChange = vm::onNombreChange,
                label = { Text("Nombre") },
                singleLine = true,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Email (solo lectura)
            OutlinedTextField(
                value = ui.email,
                onValueChange = {},
                label = { Text("Correo") },
                singleLine = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            // Indicador DUOC
            if (ui.esDuoc) {
                AssistChip(onClick = {}, enabled = false, label = { Text("Correo DUOC – descuento activo") })
            } else {
                Text(
                    "El descuento DUOC se detecta automáticamente con correo @duoc.cl",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            //prueba 7 final



            // CRUD Perfil
            OutlinedTextField(
                value = ui.telefono,
                onValueChange = vm::onTelefonoChange,
                label = { Text("Teléfono") },
                singleLine = true,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.direccion,
                onValueChange = vm::onDireccionChange,
                label = { Text("Dirección") },
                singleLine = true,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = ui.numero,
                    onValueChange = vm::onNumeroChange,
                    label = { Text("Número") },
                    singleLine = true,
                    enabled = !ui.isLoading,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = ui.comuna,
                    onValueChange = vm::onComunaChange,
                    label = { Text("Comuna") },
                    singleLine = true,
                    enabled = !ui.isLoading,
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = ui.region,
                onValueChange = vm::onRegionChange,
                label = { Text("Región") },
                singleLine = true,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Mensajes
            ui.error?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (ui.savedOk) {
                Text(
                    text = "Cambios guardados",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Botón Guardar
            Button(
                onClick = vm::guardar,
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Guardando…")
                } else {
                    Text("Guardar cambios")
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
