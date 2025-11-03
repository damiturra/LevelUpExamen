package com.example.levelupgamer.navigation

import com.example.levelupgamer.ui.home.PerfilScreen
import com.example.levelupgamer.ui.login.LoginScreen
import com.example.levelupgamer.ui.product.CarritoScreen
import com.example.levelupgamer.ui.product.DetalleProductoScreen
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.levelupgamer.ui.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable(route = "login") {
            LoginScreen(navController = navController)
        }



        composable(
            route = "home/{username}/{esDuoc}",
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("esDuoc") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val esDuoc = backStackEntry.arguments?.getBoolean("esDuoc") ?: false
            HomeScreen(
                navController = navController,
                username = username,
                esDuoc = esDuoc
            )
        }

        composable(
            route = "detalle_producto/{codigo}",
            arguments = listOf(
                navArgument("codigo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val codigo = backStackEntry.arguments?.getString("codigo") ?: ""
            DetalleProductoScreen(
                navController = navController,
                productoCodigo = codigo
            )
        }

        composable(route = "carrito") {
            CarritoScreen(navController = navController)
        }

        composable(
            route = "perfil/{username}",
            arguments = listOf(
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            PerfilScreen(
                navController = navController,
                username = username
            )
        }
    }
}