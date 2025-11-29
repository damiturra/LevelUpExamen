package com.example.levelupgamer.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.ui.product.getProductImageRes
import com.example.levelupgamer.view.ProductoViewModel
import com.example.levelupgamer.viewmodel.factories.ProductoVMFactory

// Carrito + Sesión
import com.example.levelupgamer.view.CarritoViewModel
import com.example.levelupgamer.viewmodel.factories.CarritoVMFactory
import com.example.levelupgamer.data.session.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    username: String,
    esDuoc: Boolean,
    viewModel: ProductoViewModel = viewModel(factory = ProductoVMFactory())
) {
    // Productos desde Room
    val productos by viewModel.productos.collectAsState(initial = emptyList())

    // Carrito + contador
    val carritoVM: CarritoViewModel = viewModel(factory = CarritoVMFactory())
    val count by carritoVM.cantidadItems.collectAsState(initial = 0)

    // Inicializa carrito con descuento DUOC
    LaunchedEffect(Unit) {
        val desc = if (SessionManager.esDuoc) 20 else 0
        carritoVM.inicializarCarrito(SessionManager.currentUserId, desc)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LEVEL-UP GAMER") },
                actions = {
                    // 🔹 Abrir mapa de sucursales (ruta sin parámetros)
                    IconButton(onClick = { navController.navigate("mapaSucursales") }) {
                        Icon(Icons.Default.Map, contentDescription = "Mapa sucursales")
                    }
                    // Si quieres pasar coordenadas específicas:
                    // IconButton(onClick = {
                    //     navController.navigate("mapaSucursales/-33.5435/-70.5750")
                    // }) { Icon(Icons.Default.Map, null) }   navController.navigat

                    // Carrito con badge
                    IconButton(onClick = { navController.navigate("carrito") }) {
                        BadgedBox(
                            badge = { if (count > 0) Badge { Text(count.toString()) } }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                    }

                    IconButton(onClick = { navController.navigate("perfil/$username") }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión")
                    }
                    IconButton(onClick = { navController.navigate("scanner") }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear QR")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header con bienvenida
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (esDuoc)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hola, $username 👋",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (esDuoc) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Usuario Duoc - 20% de descuento",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    // 🔹 CTA para abrir el mapa de sucursales
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { navController.navigate("mapaSucursales") }) {
                        Icon(Icons.Default.Map, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ver sucursales cercanas")
                    }
                }
            }

            // Grilla de productos
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(productos, key = { it.codigo }) { p ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("detalle_producto/${p.codigo}") },
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Image(
                                painter = painterResource(id = getProductImageRes(p.codigo)),
                                contentDescription = p.nombre,
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .align(Alignment.CenterHorizontally),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = p.nombre,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${"%,d".format(p.precio)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}
