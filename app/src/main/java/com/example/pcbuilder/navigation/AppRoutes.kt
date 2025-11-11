package com.example.pcbuilder.navigation

object AppRoutes{
    // Rutas existentes
    const val LOGIN_SCREEN = "login"
    const val REGISTRO_SCREEN = "registro"
    const val CATALOGO_SCREEN = "catalogo"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val CART_SCREEN = "cart"
    const val CHECKOUT_SCREEN = "checkout"



    // Argumento para la pantalla de edición
    const val PRODUCT_ID_ARG = "productId"

    // Ruta base para añadir/editar
    const val ADD_EDIT_PRODUCT_SCREEN = "add_edit_product"

    // Ruta completa con argumento opcional: "add_edit_product?productId={productId}"
    const val ADD_EDIT_PRODUCT_ROUTE = "$ADD_EDIT_PRODUCT_SCREEN?$PRODUCT_ID_ARG={$PRODUCT_ID_ARG}"




     // Esta es la función que AdminDashboardScreen usa.

    fun addEditProductRoute(productId: Int?): String {
        return if (productId != null) {
            "$ADD_EDIT_PRODUCT_SCREEN?$PRODUCT_ID_ARG=$productId"
        } else {
            ADD_EDIT_PRODUCT_SCREEN
        }
    }
}