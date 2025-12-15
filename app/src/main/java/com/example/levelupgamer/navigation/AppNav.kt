package com.example.levelupgamer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.levelupgamer.ui.login.LoginScreen
import com.example.levelupgamer.ui.login.RegistroScreen
import com.example.levelupgamer.ui.home.HomeScreen
import com.example.levelupgamer.ui.home.PerfilScreen
import com.example.levelupgamer.ui.product.DetalleProductoScreen
import com.example.levelupgamer.ui.product.CarritoScreen
import com.example.levelupgamer.ui.qr.QrScannerScreen
import com.example.levelupgamer.ui.map.BranchesMapScreen
import com.example.levelupgamer.ui.homevendedor.VendedorProductosScreen
import com.example.levelupgamer.ui.homesupervisor.AdminVendedoresScreen
import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.ui.home.HistorialComprasScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current.applicationContext
    LaunchedEffect(Unit) {
        SessionManager.init(context)
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { Splash(navController) }
        composable("login") { LoginScreen(navController) }
        composable("registro") { RegistroScreen(navController) }

        composable(
            route = "homeUsuario/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val esDuoc = SessionManager.esDuoc
            HomeScreen(
                navController = navController,
                username = username,
                esDuoc = esDuoc
            )
        }

        composable(
            route = "homeVendedor/{vendedorId}",
            arguments = listOf(navArgument("vendedorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vendedorId = backStackEntry.arguments?.getLong("vendedorId") ?: 0L
            VendedorProductosScreen(
                navController = navController,
                vendedorId = vendedorId
            )
        }

        composable("homeSupervisor") {
            AdminVendedoresScreen(navController = navController)
        }

        composable(
            route = "detalle_producto/{codigo}",
            arguments = listOf(navArgument("codigo") { type = NavType.StringType })
        ) { backStackEntry ->
            val codigo = backStackEntry.arguments?.getString("codigo") ?: ""
            DetalleProductoScreen(
                navController = navController,
                productoCodigo = codigo
            )
        }

        composable("carrito") { CarritoScreen(navController = navController) }
        composable("scanner") { QrScannerScreen(navController = navController) }

        // PERFIL SIN username, solo usa SessionManager.currentUserId
        composable("perfil") {
            PerfilScreen(navController = navController)
        }

        // 🧾 Historial de compras
        composable("historialCompras") {
            HistorialComprasScreen(navController = navController)
        }

        composable("mapaSucursales") {
            BranchesMapScreen(navController = navController)
        }

        composable(
            route = "mapaSucursales/{lat}/{lng}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments
                ?.getString("lat")?.toDoubleOrNull() ?: -33.5435
            val lng = backStackEntry.arguments
                ?.getString("lng")?.toDoubleOrNull() ?: -70.5750

            BranchesMapScreen(
                navController = navController,
                branchLat = lat,
                branchLng = lng
            )
        }
    }
}