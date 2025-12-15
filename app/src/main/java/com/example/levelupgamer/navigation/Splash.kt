// navigation/Splash.kt
package com.example.levelupgamer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.levelupgamer.data.session.SessionManager
import com.example.levelupgamer.data.user.Role

@Composable
fun Splash(nav: NavController) {
    val app = LocalContext.current.applicationContext
    LaunchedEffect(Unit) {
        SessionManager.init(app) // por si acaso
        val uid = SessionManager.currentUserId
        if (uid != null) {
            when (SessionManager.role) {
                Role.USER -> {
                    val name = SessionManager.currentUserName ?: "user"
                    nav.navigate("homeUsuario/$name") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                Role.VENDEDOR -> {
                    val vendId = SessionManager.currentVendedorId ?: 0L
                    nav.navigate("homeVendedor/$vendId") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                Role.ADMIN -> {
                    nav.navigate("homeSupervisor") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        } else {
            nav.navigate("login") { popUpTo(0) { inclusive = true } }
        }
    }
}
