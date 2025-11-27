package com.example.pcbuilder.data.api

import com.example.pcbuilder.data.model.Product
import com.example.pcbuilder.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface PcBuilderApiService {

    //Microservicio 1: Catálogo de Productos
    @GET("api/v1/products")
    suspend fun getAllProducts(): Response<List<Product>>

    @POST("api/v1/products")
    suspend fun createProduct(@Body product: Product): Response<Product>

    //Microservicio 2: Gestión de Usuarios (Auth)
    @POST("api/v1/auth/login")
    suspend fun login(@Body loginData: Map<String, String>): Response<User>
}