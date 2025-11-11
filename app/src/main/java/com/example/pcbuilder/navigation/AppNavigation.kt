package com.example.pcbuilder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.example.pcbuilder.data.AppDatabase
import com.example.pcbuilder.viewmodel.AppViewModelFactory
import com.example.pcbuilder.ui.screens.LoginScreen
import com.example.pcbuilder.ui.screens.RegistroScreen
import com.example.pcbuilder.ui.screens.AddEditProductScreen
import com.example.pcbuilder.ui.screens.CartScreen
import com.example.pcbuilder.ui.screens.AdminDashboardScreen
import com.example.pcbuilder.ui.screens.CatalogScreen
import com.example.pcbuilder.viewmodel.AdminViewModel
import com.example.pcbuilder.viewmodel.CatalogoViewModel
import com.example.pcbuilder.ui.screens.CheckoutScreen
import com.example.pcbuilder.viewmodel.CartViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier){

    val navController = rememberNavController()

    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()
    val productDao = db.productDao()
    val cartDao = db.cartDao()

    val appViewModelFactory = AppViewModelFactory(userDao, productDao, cartDao)


    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN_SCREEN,
        modifier = modifier
    ) {
        composable(route = AppRoutes.LOGIN_SCREEN) {
            LoginScreen(navController = navController, factory = appViewModelFactory)
        }

        composable(route = AppRoutes.REGISTRO_SCREEN) {
            RegistroScreen(navController = navController, factory = appViewModelFactory)
        }

        composable(route = AppRoutes.CATALOGO_SCREEN) {
            val catalogoViewModel: CatalogoViewModel = viewModel(factory = appViewModelFactory)
            val cartViewModel: CartViewModel = viewModel(factory = appViewModelFactory)

            CatalogScreen(
                navController = navController,
                viewModel = catalogoViewModel,
                cartViewModel = cartViewModel
            )
        }

        composable(route = AppRoutes.ADMIN_DASHBOARD) {
            val viewModel: AdminViewModel = viewModel(factory = appViewModelFactory)
            AdminDashboardScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = AppRoutes.ADD_EDIT_PRODUCT_ROUTE,
            arguments = listOf(
                navArgument(AppRoutes.PRODUCT_ID_ARG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val viewModel: AdminViewModel = viewModel(factory = appViewModelFactory)
            val productIdString = backStackEntry.arguments?.getString(AppRoutes.PRODUCT_ID_ARG)

            AddEditProductScreen(
                navController = navController,
                viewModel = viewModel,
                productId = productIdString?.toIntOrNull()
            )
        }

        composable(route = AppRoutes.CART_SCREEN) {
            CartScreen(navController = navController, factory = appViewModelFactory)
        }

        composable(route = AppRoutes.CHECKOUT_SCREEN) {
            CheckoutScreen(navController = navController, factory = appViewModelFactory)
        }
    }
}