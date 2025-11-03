package com.example.levelupgamer.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    productoCodigo: String
) {
    var cantidad by remember { mutableStateOf(1) }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Datos de ejemplo
    val producto = remember {
        mapOf(
            "JM001" to Triple("Catan", 29990, "Juego de estrategia clásico"),
            "AC001" to Triple("Controlador Xbox", 59990, "Control inalámbrico para Xbox"),
            "CO001" to Triple("PlayStation 5", 549990, "Consola de última generación"),
            "MS001" to Triple("Mouse Gamer", 49990, "Mouse de alta precisión"),
            "SG001" to Triple("Silla Gamer", 349990, "Silla ergonómica para gaming"),
            "PP001" to Triple("Polera Gamer", 14990, "Polera personalizable")
        )[productoCodigo] ?: Triple("Producto", 0, "Descripción")
    }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar("Producto agregado al carrito")
            showSnackbar = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    Icons.Default.Inventory,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = producto.first,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$${"%,d".format(producto.second)}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = producto.third,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cantidad:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilledIconButton(
                                onClick = { if (cantidad > 1) cantidad-- },
                                enabled = cantidad > 1
                            ) {
                                Icon(Icons.Default.Remove, "Disminuir")
                            }

                            Text(
                                text = cantidad.toString(),
                                style = MaterialTheme.typography.headlineSmall
                            )

                            FilledIconButton(onClick = { cantidad++ }) {
                                Icon(Icons.Default.Add, "Aumentar")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        showSnackbar = true
                        cantidad = 1
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.AddShoppingCart, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AGREGAR AL CARRITO")
                }
            }
        }
    }
}