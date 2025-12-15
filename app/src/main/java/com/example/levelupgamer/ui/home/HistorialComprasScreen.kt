package com.example.levelupgamer.ui.home

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.viewmodel.HistorialComprasViewModel
import com.example.levelupgamer.viewmodel.factories.HistorialComprasVMFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialComprasScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application

    // Si no hay usuario logueado, userId = 0 (invitado)
    val userId = SessionManager.currentUserId ?: 0

    val viewModel: HistorialComprasViewModel = viewModel(
        factory = HistorialComprasVMFactory(app, userId)
    )

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de compras") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Error al cargar historial",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.compras.isEmpty() -> {
                    Text(
                        text = "Aún no tienes compras registradas.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.compras) { compraConItems ->
                            val compra = compraConItems.compra
                            val items = compraConItems.items

                            val fecha = formatDate(compra.fechaMillis)

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Compra #${compra.id} - $fecha",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Total: \$${compra.total}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (compra.descuentoPorcentaje > 0) {
                                        Text(
                                            text = "Descuento: ${compra.descuentoPorcentaje}% (-\$${compra.descuentoMonto})",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Text(
                                        text = "IVA: ${compra.ivaPorcentaje}% (\$${compra.ivaMonto})",
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Productos:",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    items.forEach { item ->
                                        Text(
                                            text = "- ${item.productoNombre} x${item.cantidad} (\$${item.productoPrecio} c/u)",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}
