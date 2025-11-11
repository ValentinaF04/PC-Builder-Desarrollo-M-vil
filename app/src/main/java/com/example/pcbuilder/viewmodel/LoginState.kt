package com.example.pcbuilder.viewmodel

data class LoginUIState(
    val correo: String = "",
    val clave: String = "",
    val error: String? = null
)