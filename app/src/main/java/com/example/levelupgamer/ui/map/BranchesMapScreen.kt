// app/src/main/java/com/example/levelupgamer/ui/map/BranchesMapScreen.kt
package com.example.levelupgamer.ui.map

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding   // 👈 IMPORT CLAVE
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchesMapScreen(
    navController: NavController,
    branchLat: Double = -33.5390,
    branchLng: Double = -70.5696
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sucursales (OSM)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // 👈 ahora sí compila
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx: Context ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        controller.setZoom(12.0)
                        controller.setCenter(GeoPoint(branchLat, branchLng))

                        // Marcadores demo (reemplaza por tu lista desde Room cuando quieras)
                        listOf(
                            Triple(-33.5390, -70.5696, "Sucursal La Florida" to "Vicuña Mackenna 8577"),
                            Triple(-33.6169, -70.5750, "Sucursal Puente Alto" to "Musa 2099")
                        ).forEach { (lat, lng, info) ->
                            val (title, sub) = info
                            overlays.add(
                                Marker(this).apply {
                                    position = GeoPoint(lat, lng)
                                    this.title = title
                                    subDescription = sub
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                            )
                        }

                        invalidate()
                    }
                },
                update = { map -> map.invalidate() }
            )
        }

        DisposableEffect(Unit) { onDispose { } }
    }
}
