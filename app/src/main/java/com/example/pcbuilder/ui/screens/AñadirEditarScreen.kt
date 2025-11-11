package com.example.pcbuilder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pcbuilder.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavController,
    viewModel: AdminViewModel,
    productId: Int?
) {
    val isEditMode = productId != null
    val title = if (isEditMode) "Editar Producto" else "Añadir Producto"

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") } // <-- NUEVO CAMPO

    if (isEditMode) {
        val productState by viewModel.getProduct(productId!!).collectAsState(initial = null)

        LaunchedEffect(productState) {
            productState?.let { product ->
                name = product.name
                description = product.description ?: ""
                price = product.price.toString()
                stock = product.stock.toString()
                imageUrl = product.imageUrl ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val priceDouble = price.toDoubleOrNull()
                val stockInt = stock.toIntOrNull()

                if (priceDouble != null && stockInt != null) {
                    viewModel.insertProduct(
                        name = name,
                        description = description,
                        price = priceDouble,
                        stock = stockInt,
                        imageUrl = imageUrl, // <-- NUEVO CAMPO
                        id = productId ?: 0
                    )
                    navController.popBackStack()
                }
            }) {
                Icon(Icons.Default.Done, contentDescription = "Guardar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // <-- Scroll añadido
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // --- ¡CAMPO AÑADIDO! ---
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL de la Imagen") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}