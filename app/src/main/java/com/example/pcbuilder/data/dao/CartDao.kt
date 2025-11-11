package com.example.pcbuilder.data.dao


import com.example.pcbuilder.data.model.CartItem
import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItem)

    @Delete
    suspend fun deleteItem(item: CartItem)

    //get para todos los items
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItemsForUser(userId: Int): Flow<List<CartItem>>

    //buscar por item
    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun findItemInCart(userId: Int, productId: Int): CartItem?

    //limpiar el carrito despuesde la compra
    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Int)
}