package com.example.pcbuilder.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.pcbuilder.viewmodel.AppViewModelFactory
import com.example.pcbuilder.viewmodel.CartViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pcbuilder.data.SessionManager
import com.example.pcbuilder.navigation.AppRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {
    val viewModel: CartViewModel = viewModel(factory = factory)

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId by sessionManager.userIdFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simulación de Pago") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Número de Tarjeta") })
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre en Tarjeta") })

            Button(
                onClick = {
                    userId?.let { id ->
                        scope.launch {
                            viewModel.performCheckout(id) {

                                Toast.makeText(
                                    context,
                                    "¡Compra realizada con éxito!",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.navigate(AppRoutes.CATALOGO_SCREEN) {
                                    popUpTo(AppRoutes.CATALOGO_SCREEN) { inclusive = true }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Pagar Ahora")
            }
        }
    }
}