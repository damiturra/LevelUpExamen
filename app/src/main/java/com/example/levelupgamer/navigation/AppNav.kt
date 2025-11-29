package com.example.levelupgamer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Pantallas reales de tu app
import com.example.levelupgamer.ui.login.LoginScreen
import com.example.levelupgamer.ui.login.RegistroScreen
import com.example.levelupgamer.ui.home.HomeScreen            // Usuario
import com.example.levelupgamer.ui.home.PerfilScreen         // Perfil
import com.example.levelupgamer.ui.product.DetalleProductoScreen
import com.example.levelupgamer.ui.product.CarritoScreen
import com.example.levelupgamer.ui.qr.QrScannerScreen
import com.example.levelupgamer.ui.map.BranchesMapScreen

// 🔹 Home reales Vendedor / Supervisor (estas son las que tienes importadas)
import com.example.levelupgamer.ui.homevendedor.VendedorProductosScreen
import com.example.levelupgamer.ui.homesupervisor.AdminVendedoresScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login + Registro
        composable("login") { LoginScreen(navController = navController) }
        composable("registro") { RegistroScreen(navController = navController) }

        // Home USUARIO
        composable(
            route = "homeUsuario/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStack ->
            val username = backStack.arguments?.getString("username") ?: ""
            val esDuoc = com.example.levelupgamer.data.session.SessionManager.esDuoc
            HomeScreen(
                navController = navController,
                username = username,
                esDuoc = esDuoc
            )
        }

        // 🔹 Panel VENDEDOR: productos del vendedor (pasamos vendedorId)
        composable(
            route = "homeVendedor/{vendedorId}",
            arguments = listOf(navArgument("vendedorId") { type = NavType.LongType })
        ) { backStack ->
            val vendedorId = backStack.arguments?.getLong("vendedorId") ?: 0L
            VendedorProductosScreen(
                navController = navController,
                vendedorId = vendedorId
            )
        }

        // 🔹 Panel SUPERVISOR/ADMIN: CRUD de vendedores
        composable("homeSupervisor") {
            com.example.levelupgamer.ui.homesupervisor.AdminVendedoresScreen(navController)
        }


        // Detalle de producto
        composable(
            route = "detalle_producto/{codigo}",
            arguments = listOf(navArgument("codigo") { type = NavType.StringType })
        ) { backStack ->
            val codigo = backStack.arguments?.getString("codigo") ?: ""
            DetalleProductoScreen(
                navController = navController,
                productoCodigo = codigo
            )
        }

        // Carrito, Scanner, Perfil
        composable("carrito") { CarritoScreen(navController = navController) }
        composable("scanner") { QrScannerScreen(navController = navController) }
        composable(
            route = "perfil/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStack ->
            val username = backStack.arguments?.getString("username") ?: ""
            PerfilScreen(navController = navController, username = username)
        }

        // 🔹 MAPA SUCURSALES (sin args)
        composable("mapaSucursales") {
            BranchesMapScreen(navController = navController)
        }

        // 🔹 MAPA SUCURSALES con coordenadas
        composable(
            route = "mapaSucursales/{lat}/{lng}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType }
            )
        ) { backStack ->
            val lat = backStack.arguments?.getString("lat")?.toDoubleOrNull() ?: -33.5435
            val lng = backStack.arguments?.getString("lng")?.toDoubleOrNull() ?: -70.5750
            BranchesMapScreen(
                navController = navController,
                branchLat = lat,
                branchLng = lng
            )
        }
    }
}
