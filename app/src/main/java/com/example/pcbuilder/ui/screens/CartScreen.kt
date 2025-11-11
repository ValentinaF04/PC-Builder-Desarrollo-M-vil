package com.example.pcbuilder.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.pcbuilder.viewmodel.AppViewModelFactory
import com.example.pcbuilder.viewmodel.CartViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pcbuilder.data.SessionManager
import com.example.pcbuilder.data.model.CartItem
import com.example.pcbuilder.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {
    val viewModel: CartViewModel = viewModel(factory = factory)

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId by sessionManager.userIdFlow.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu Carrito") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Usamos el paddingValues del Scaffold
        Column(Modifier.padding(paddingValues).padding(16.dp)) {
            userId?.let { id ->
                val items by viewModel.getCartItems(id).collectAsState(initial = emptyList())

                if (items.isEmpty()) {
                    Text("Tu carrito está vacío.")
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(items) { cartItem ->
                            CartItemRow(item = cartItem, viewModel = viewModel)
                        }
                    }

                    Button(
                        onClick = { navController.navigate(AppRoutes.CHECKOUT_SCREEN) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Proceder al Pago")
                    }
                }
            } ?: Text("Inicia sesión para ver tu carrito.")
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, viewModel: CartViewModel) {
    val product by viewModel.getProductById(item.productId).collectAsState(initial = null)

    product?.let { p ->
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(p.name, modifier = Modifier.weight(1f))
            Text("Cant: ${item.quantity}")
            Text("$$${"%.2f".format(p.price * item.quantity)}", modifier = Modifier.padding(start = 16.dp))
        }
    } ?: Text("Cargando producto...")
}