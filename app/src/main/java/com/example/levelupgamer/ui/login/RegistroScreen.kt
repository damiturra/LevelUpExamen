package com.example.levelupgamer.ui.login

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.data.user.Role
import com.example.levelupgamer.viewmodel.RegistroViewModel
import com.example.levelupgamer.viewmodel.factories.RegistroVMFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController) {
    val app = LocalContext.current.applicationContext as Application
    val vm: RegistroViewModel = viewModel(factory = RegistroVMFactory(app))
    val ui = vm.uiState

    val isAdmin = SessionManager.role == Role.ADMIN
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(isAdmin) {
        if (!isAdmin && ui.role != Role.USER) vm.onRoleChange(Role.USER)
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Crear cuenta") }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Completa tus datos", style = MaterialTheme.typography.titleLarge)

                    OutlinedTextField(
                        value = ui.nombre,
                        onValueChange = vm::onNombreChange,
                        label = { Text("Nombre") },
                        singleLine = true,
                        enabled = !ui.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = ui.email,
                        onValueChange = vm::onEmailChange,
                        label = { Text("Correo") },
                        singleLine = true,
                        enabled = !ui.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Indicador automático DUOC
                    if (ui.esDuoc) {
                        AssistChip(
                            onClick = { },
                            enabled = false,
                            label = { Text("Correo DUOC detectado – descuento aplicado") }
                        )
                    } else {
                        Text(
                            "Si registras tu correo @duoc.cl, aplicaremos el descuento automáticamente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = ui.password,
                        onValueChange = vm::onPasswordChange,
                        label = { Text("Contraseña") },
                        singleLine = true,
                        enabled = !ui.isLoading,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { showPassword = !showPassword }, enabled = !ui.isLoading) {
                                Text(if (showPassword) "Ocultar" else "Mostrar")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Sólo ADMIN elige rol / vendedorId
                    if (isAdmin) {
                        Text("Rol", style = MaterialTheme.typography.titleMedium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = ui.role == Role.USER,
                                onClick = { vm.onRoleChange(Role.USER) },
                                label = { Text("USER") }
                            )
                            FilterChip(
                                selected = ui.role == Role.VENDEDOR,
                                onClick = { vm.onRoleChange(Role.VENDEDOR) },
                                label = { Text("VENDEDOR") }
                            )
                            FilterChip(
                                selected = ui.role == Role.ADMIN,
                                onClick = { vm.onRoleChange(Role.ADMIN) },
                                label = { Text("ADMIN") }
                            )
                        }
                        if (ui.role == Role.VENDEDOR) {
                            OutlinedTextField(
                                value = ui.vendedorIdText,
                                onValueChange = vm::onVendedorIdChange,
                                label = { Text("Vendedor ID (número)") },
                                singleLine = true,
                                enabled = !ui.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    ui.error?.let { msg ->
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            vm.registrar { nombre, _, role ->
                                when (role) {
                                    Role.ADMIN -> navController.navigate("homeSupervisor") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                    Role.VENDEDOR -> {
                                        val vendId = SessionManager.currentVendedorId ?: 0L
                                        navController.navigate("homeVendedor/$vendId") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                    else -> navController.navigate("homeUsuario/$nombre") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        },
                        enabled = !ui.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (ui.isLoading) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Creando cuenta…")
                        } else {
                            Text("Crear cuenta")
                        }
                    }

                    Text(
                        text = "Tus datos se almacenan localmente para esta demo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
