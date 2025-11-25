package com.example.pcbuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcbuilder.data.api.RetrofitClient
import com.example.pcbuilder.data.dao.ProductDao
import com.example.pcbuilder.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow 
import kotlinx.coroutines.flow.asStateFlow    
import kotlinx.coroutines.launch    

class CatalogoViewModel(private val productDao: ProductDao) : ViewModel() {

    val productos: Flow<List<Product>> = productDao.getAllProducts()

    private val _valorDolar = MutableStateFlow<Double?>(null)
    val valorDolar = _valorDolar.asStateFlow()

    init {
        obtenerValorDolar()
    }

    private fun obtenerValorDolar() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.dollarApi.getDollarValue()
                if (response.serie.isNotEmpty()) {
                    _valorDolar.value = response.serie[0].valor
                }
            } catch (e: Exception) {
                _valorDolar.value = null 
                println("Error obteniendo dolar: ${e.message}")
            }
        }
    }
}