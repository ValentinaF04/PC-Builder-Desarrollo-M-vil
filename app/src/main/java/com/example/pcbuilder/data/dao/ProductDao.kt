package com.example.pcbuilder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pcbuilder.data.model.Product
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete

@Dao
interface ProductDao {

    //Insertamos nuevo producto o se reemplaza en caso de existir
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // actualizamos un producto que ya existe en la BD
    @Update
    suspend fun updateProduct(product: Product)

    // Lista de los productos de forma ASCENDENTE por nombre
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    //Buscamos productos por su ID
    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Int): Flow<Product?>

    //Eliminar producto

    @Delete
    suspend fun deleteProduct(product: Product)
}