package com.example.levelupgamer.ui.homevendedor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.levelupgamer.data.model.Producto
import com.example.levelupgamer.viewmodel.vendedor.VendedorProductosViewModel   // 👈 vendor (no vendedor)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendedorProductosScreen(
    navController: NavController,
    vendedorId: Long
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val vm: VendedorProductosViewModel = viewModel(
        key = "vendor-$vendedorId",
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val app = context.applicationContext as android.app.Application
                val db = com.example.levelupgamer.data.database.AppDatabase.getDatabase(app)
                val repo = com.example.levelupgamer.data.repository.ProductoRepository(
                    db.productoDao(),
                    db.categoriaDao()
                )
                return com.example.levelupgamer.viewmodel.vendedor.VendedorProductosViewModel(
                    app = app,
                    repo = repo,
                    vendedorIdArg = vendedorId
                ) as T
            }
        }
    )

    val ui by vm.ui.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis productos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { vm.nuevo() }) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            if (ui.error != null) {
                AssistChip(onClick = { /* retry */ }, label = { Text(ui.error ?: "") })
                Spacer(Modifier.height(8.dp))
            }
            if (ui.isLoading && ui.productos.isEmpty()) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ui.productos, key = { it.codigo }) { p ->
                    ProductoRow(
                        p = p,
                        onEdit = { vm.editar(p) },
                        onDelete = { vm.eliminar(p.codigo) }
                    )
                }
            }
        }
    }

    // ✅ evita el error de smart cast
    ui.editando?.let { ed ->
        ProductoDialog(
            inicial = ed,
            onDismiss = { vm.cancelar() },
            onSave = { nombre, descripcion, precio, categoriaId, categoriaNombre, activo ->
                vm.guardar(
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    categoriaId = categoriaId,
                    categoriaNombre = categoriaNombre,
                    activo = activo
                )
            }
        )
    }
}

@Composable
private fun ProductoRow(
    p: Producto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(p.nombre, style = MaterialTheme.typography.titleMedium)
                Text("Código: ${p.codigo}", style = MaterialTheme.typography.bodyMedium)
                Text("Precio: ${p.precio}", style = MaterialTheme.typography.bodyMedium)
                Text("Categoría: ${p.categoriaNombre}", style = MaterialTheme.typography.labelMedium)
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductoDialog(
    inicial: Producto,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Int, String, Boolean) -> Unit
) {
    var nombre by remember { mutableStateOf(TextFieldValue(inicial.nombre)) }
    var descripcion by remember { mutableStateOf(TextFieldValue(inicial.descripcion)) }
    var precio by remember { mutableStateOf(TextFieldValue(inicial.precio.toString())) }
    var categoriaId by remember { mutableStateOf(TextFieldValue(inicial.categoriaId.toString())) }
    var categoriaNombre by remember { mutableStateOf(TextFieldValue(inicial.categoriaNombre)) }
    var activo by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val p = precio.text.toIntOrNull() ?: 0
                val cId = categoriaId.text.toIntOrNull() ?: 0
                onSave(nombre.text, descripcion.text, p, cId, categoriaNombre.text, activo)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text(if (inicial.nombre.isBlank()) "Nuevo producto" else "Editar producto") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio (entero)") }, singleLine = true)
                OutlinedTextField(value = categoriaId, onValueChange = { categoriaId = it }, label = { Text("Categoría Id") }, singleLine = true)
                OutlinedTextField(value = categoriaNombre, onValueChange = { categoriaNombre = it }, label = { Text("Categoría nombre") }, singleLine = true)
            }
        }
    )
}
