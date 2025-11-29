package com.example.levelupgamer.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelupgamer.viewmodel.LoginViewModel
import com.example.levelupgamer.R
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import com.example.levelupgamer.data.user.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    var showPassword by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LEVEL-UP GAMER") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            var pressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (pressed) 1.2f else 1f,
                animationSpec = tween(durationMillis = 150),
                label = "logoPressScale"
            )

            Image(
                painter = painterResource(id = R.drawable.logo_levelup),
                contentDescription = "Logo Level-Up Gamer",
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clickable { pressed = !pressed },
                contentScale = ContentScale.Fit
            )

            Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)
            Text("Inicia sesión para continuar", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            if (uiState.error != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        text = uiState.error,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("👤 Usuarios de prueba:", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(4.dp))
                    Text("• damian@duoc.cl / 123456  (USER)")
                    Text("• jean@duoc.cl / 123456    (USER)")
                    Text("• damian@vendedor.cl / 123456  (VENDEDOR)")
                    Text("• jean@vendedor.cl / 123456    (VENDEDOR)")
                    Text("• damian@admin.cl / 123456  (ADMIN)")
                    Text("• jean@admin.cl / 123456    (ADMIN)")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.hacerLogin { nombreUsuario, esUsuarioDuoc, role ->
                        when (role) {
                            Role.USER -> navController.navigate("homeUsuario/$nombreUsuario") {
                                popUpTo("login") { inclusive = true }
                            }
                            Role.VENDEDOR -> {
                                val vendId = com.example.levelupgamer.data.session.SessionManager.currentVendedorId ?: 0L
                                navController.navigate("homeVendedor/$vendId") {                // 👈 usa el Long del Session
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                            Role.ADMIN -> {
                                navController.navigate("homeSupervisor") {                       // 👈 sin args
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }

                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("INICIAR SESIÓN")
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿No tienes cuenta? ")
                TextButton(onClick = { /* navController.navigate("registro") */ }) {
                    Text("Regístrate", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

/**
 * Demo: mapea un email de vendedor a un vendedorId (Long) para la ruta homeVendedor/{vendedorId}.
 * Cuando quieras, cambia esto para consultar Room (VendedorDao) y traer el ID real.
 */
private fun mapEmailToVendedorId(email: String): Long {
    // mapping de ejemplo
    return when (email.trim().lowercase()) {
        "damian@vendedor.cl" -> 1L
        "jean@vendedor.cl" -> 2L
        else -> 1L // fallback
    }
}
