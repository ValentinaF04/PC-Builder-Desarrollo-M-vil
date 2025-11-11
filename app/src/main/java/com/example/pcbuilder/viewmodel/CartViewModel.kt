package com.example.pcbuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcbuilder.data.dao.CartDao
import com.example.pcbuilder.data.dao.ProductDao
import com.example.pcbuilder.data.model.CartItem
import com.example.pcbuilder.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) : ViewModel() {

    fun getCartItems(userId: Int): Flow<List<CartItem>> {
        return cartDao.getCartItemsForUser(userId)
    }

    fun getProductById(productId: Int): Flow<Product?> {
        return productDao.getProductById(productId)
    }

    fun addItemToCart(userId: Int, productId: Int) {
        viewModelScope.launch {
            val existingItem = cartDao.findItemInCart(userId, productId)

            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
                cartDao.insertItem(updatedItem)
            } else {
                val newItem = CartItem(userId = userId, productId = productId, quantity = 1)
                cartDao.insertItem(newItem)
            }
        }
    }

    suspend fun performCheckout(userId: Int, onCheckoutSuccess: () -> Unit) {
        val items = cartDao.getCartItemsForUser(userId).first()

        for (item in items) {
            val product = productDao.getProductById(item.productId).first()

            if (product != null) {
                if (product.stock >= item.quantity) {
                    val updatedProduct = product.copy(stock = product.stock - item.quantity)
                    productDao.updateProduct(updatedProduct)
                }
            }
        }

        cartDao.clearCart(userId)

        onCheckoutSuccess()
    }
}