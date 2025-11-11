package com.example.pcbuilder.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pcbuilder.data.dao.ProductDao
import com.example.pcbuilder.data.model.Product
import kotlinx.coroutines.flow.Flow

class CatalogoViewModel(private val productDao: ProductDao) : ViewModel() {

    val productos: Flow<List<Product>> = productDao.getAllProducts()
}