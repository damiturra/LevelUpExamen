package com.example.levelupgamer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.levelupgamer.data.model.Categoria

@Composable
fun DrawerContent(
    nombreUsuario: String,
    categorias: List<Categoria>,
    onCategoriaClick: (Int?) -> Unit,
    onPerfilClick: () -> Unit,
    onCarritoClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.BottomStart
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hola, $nombreUsuario",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Divider()

            NavigationDrawerItem(
                label = { Text("Perfil") },
                selected = false,
                onClick = onPerfilClick,
                icon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            NavigationDrawerItem(
                label = { Text("Carrito") },
                selected = false,
                onClick = onCarritoClick,
                icon = { Icon(Icons.Default.ShoppingCart, null) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "CATEGORÍAS",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            NavigationDrawerItem(
                label = { Text("Todos los productos") },
                selected = false,
                onClick = { onCategoriaClick(null) },
                icon = { Icon(Icons.Default.Apps, null) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(categorias) { categoria ->
                    NavigationDrawerItem(
                        label = { Text(categoria.nombre) },
                        selected = false,
                        onClick = { onCategoriaClick(categoria.id) },
                        icon = {
                            Icon(
                                getIconForCategory(categoria.nombre),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Divider()

            NavigationDrawerItem(
                label = { Text("Cerrar Sesión") },
                selected = false,
                onClick = onCerrarSesion,
                icon = { Icon(Icons.Default.Logout, null) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.error,
                    unselectedTextColor = MaterialTheme.colorScheme.error
                )
            )
        }
    }
}

@Composable
private fun getIconForCategory(categoryName: String) = when {
    categoryName.contains("Mesa", ignoreCase = true) -> Icons.Default.Casino
    categoryName.contains("Accesorio", ignoreCase = true) -> Icons.Default.Headset
    categoryName.contains("Consola", ignoreCase = true) -> Icons.Default.VideogameAsset
    categoryName.contains("Computador", ignoreCase = true) -> Icons.Default.Computer
    categoryName.contains("Silla", ignoreCase = true) -> Icons.Default.EventSeat
    categoryName.contains("Mouse", ignoreCase = true) && !categoryName.contains("pad") -> Icons.Default.Mouse
    categoryName.contains("Mousepad", ignoreCase = true) -> Icons.Default.CropSquare
    categoryName.contains("Polera", ignoreCase = true) -> Icons.Default.Checkroom
    categoryName.contains("Poleron", ignoreCase = true) -> Icons.Default.DryCleaning
    else -> Icons.Default.Category
}