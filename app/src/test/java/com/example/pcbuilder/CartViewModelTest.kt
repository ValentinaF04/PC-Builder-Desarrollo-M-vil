package com.example.pcbuilder.viewmodel

import com.example.pcbuilder.data.dao.CartDao
import com.example.pcbuilder.data.dao.ProductDao
import com.example.pcbuilder.data.model.CartItem
import com.example.pcbuilder.data.model.Product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private lateinit var viewModel: CartViewModel
    private lateinit var cartDao: CartDao
    private lateinit var productDao: ProductDao
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cartDao = mockk(relaxed = true)
        productDao = mockk(relaxed = true)
        viewModel = CartViewModel(cartDao, productDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `agregar item al carrito inserta nuevo item si no existe`() = runTest {
        val userId = 1
        val productId = 100
        coEvery { cartDao.findItemInCart(userId, productId) } returns null

        // WHEN (Cuando)
        viewModel.addItemToCart(userId, productId)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            cartDao.insertItem(match {
                it.userId == userId && it.productId == productId && it.quantity == 1
            })
        }
    }

    @Test
    fun `checkout exitoso limpia el carrito y actualiza stock`() = runTest {
        val userId = 1
        val cartItems = listOf(CartItem(id=1, userId=userId, productId=100, quantity=2))
        val product = Product(id=100, name="PC", description="", price=1000.0, stock=10)

        coEvery { cartDao.getCartItemsForUser(userId) } returns flowOf(cartItems)
        coEvery { productDao.getProductById(100) } returns flowOf(product)

        viewModel.performCheckout(userId) {}
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { productDao.updateProduct(match { it.stock == 8 }) }
        coVerify { cartDao.clearCart(userId) }
    }
}