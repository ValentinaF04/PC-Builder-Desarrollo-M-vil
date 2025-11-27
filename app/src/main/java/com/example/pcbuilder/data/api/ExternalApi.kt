package com.example.pcbuilder.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


//API EXTERNA (INDICADORES ECONÓMICOS - DÓLAR)


data class DollarResponse(
    val serie: List<SerieInfo>
)
data class SerieInfo(
    val valor: Double,
    val fecha: String
)

interface DollarApiService {
    @GET("api/dolar")
    suspend fun getDollarValue(): DollarResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://mindicador.cl/"

    val dollarApi: DollarApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DollarApiService::class.java)
    }
}


//API PROPIA (MICROSERVICIO EN AWS EC2)


object BackendClient {
    private const val BASE_URL = "http://50.16.251.38:8080/"

    val service: PcBuilderApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PcBuilderApiService::class.java)
    }
}