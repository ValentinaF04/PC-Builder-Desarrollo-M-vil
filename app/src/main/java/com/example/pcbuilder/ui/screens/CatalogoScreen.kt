package com.example.pcbuilder.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pcbuilder.data.SessionManager
import com.example.pcbuilder.data.model.Product
import com.example.pcbuilder.navigation.AppRoutes
import com.example.pcbuilder.ui.theme.*
import com.example.pcbuilder.viewmodel.CartViewModel
import com.example.pcbuilder.viewmodel.CatalogoViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navController: NavController,
    viewModel: CatalogoViewModel,
    cartViewModel: CartViewModel
) {
    val valorDolar by viewModel.valorDolar.collectAsState()
    val productos by viewModel.productos.collectAsState(initial = emptyList())
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val userId by sessionManager.userIdFlow.collectAsState(initial = null)

    // Estado para el Menú Desplegable
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = DeepViolet,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CATÁLOGO",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepViolet,
                    titleContentColor = NeonBlue,
                    actionIconContentColor = TextWhite
                ),
                scrollBehavior = scrollBehavior,
                actions = {
                    // Botón Carrito
                    IconButton(onClick = { navController.navigate(AppRoutes.CART_SCREEN) }) {
                        Icon(Icons.Default.ShoppingCart, "Carrito")
                    }

                    // Botón Menú de Usuario (Icono de Persona)
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.AccountCircle, "Mi Cuenta", modifier = Modifier.size(28.dp), tint = NeonPurple)
                        }

                        // Menú Desplegable Estilizado
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier
                                .background(SurfaceViolet)
                                .border(1.dp, NeonBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        ) {
                            // Opción 1: Ver Perfil
                            DropdownMenuItem(
                                text = { Text("Mi Perfil", color = TextWhite) },
                                onClick = {
                                    showMenu = false
                                    navController.navigate(AppRoutes.PROFILE_SCREEN)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = NeonBlue)
                                }
                            )

                            HorizontalDivider(color = NeonBlue.copy(alpha = 0.2f))

                            // Opción 2: Cerrar Sesión
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión", color = Color(0xFFCF6679)) }, // Rojo suave
                                onClick = {
                                    showMenu = false
                                    scope.launch {
                                        sessionManager.clearSession()
                                        navController.navigate(AppRoutes.LOGIN_SCREEN) {
                                            popUpTo(navController.graph.id) { inclusive = true }
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFCF6679))
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
            .padding(padding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 170.dp),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productos) { producto ->
                    ProductCard(
                        producto = producto,
                        valorDolar = valorDolar,
                        onClick = { /* Navegación a detalle */ },
                        onAddToCartClick = {
                            val currentUserId = userId
                            if (currentUserId != null) {
                                cartViewModel.addItemToCart(currentUserId, producto.id)
                                Toast.makeText(context, "Añadido: ${producto.name}", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Inicia sesión primero", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    producto: Product,
    valorDolar: Double?,
    onClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, Brush.linearGradient(listOf(NeonPurple.copy(alpha=0.5f), NeonBlue.copy(alpha=0.5f))), RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceViolet.copy(alpha = 0.8f)
        )
    ) {
        Column {
            Box(modifier = Modifier.height(150.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(producto.imageUrl)
                        .crossfade(true)
                        .error(android.R.drawable.ic_menu_gallery)
                        .build(),
                    contentDescription = producto.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(
                        colors = listOf(Color.Transparent, SurfaceViolet),
                        startY = 100f
                    ))
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "HARDWARE",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonBlue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = producto.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "$${String.format(Locale.getDefault(), "%,d", producto.price.toLong())}",
                            style = MaterialTheme.typography.titleLarge,
                            color = NeonPurple,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (valorDolar != null && valorDolar > 0) {
                            val precioUsd = producto.price / valorDolar
                            Text(
                                text = "US$ ${String.format(Locale.US, "%.2f", precioUsd)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }
                    }

                    FilledIconButton(
                        onClick = onAddToCartClick,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = NeonBlue,
                            contentColor = DeepViolet
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Agregar",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}