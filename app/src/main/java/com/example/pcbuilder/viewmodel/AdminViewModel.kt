package com.example.pcbuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcbuilder.data.api.BackendClient
import com.example.pcbuilder.data.dao.ProductDao
import com.example.pcbuilder.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdminViewModel(private val productDao: ProductDao) : ViewModel() {

    val productos: Flow<List<Product>> = productDao.getAllProducts()

    fun getProduct(id: Int): Flow<Product?> {
        return productDao.getProductById(id)
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productDao.deleteProduct(product)
        }
    }

    fun insertProduct(
        name: String,
        description: String,
        price: Double,
        stock: Int,
        imageUrl: String,
        id: Int = 0
    ) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                val product = Product(
                    id = if (id != 0) id else 0,
                    name = name,
                    description = description,
                    price = price,
                    stock = stock,
                    imageUrl = imageUrl
                )


                try {
                    println("🚀 Admin: Intentando subir producto a AWS...")

                    val response = BackendClient.service.createProduct(product)

                    if (response.isSuccessful) {
                        println("✅ ÉXITO: Producto '${product.name}' guardado en la Nube AWS.")
                    } else {
                        println("❌ ERROR NUBE: El servidor respondió con código ${response.code()}")
                    }
                } catch (e: Exception) {
                    println("⚠️ MODO OFFLINE: No se pudo conectar a AWS (${e.message}). Se guardará solo localmente.")
                }

                productDao.insertProduct(product)
            }
        }
    }
}