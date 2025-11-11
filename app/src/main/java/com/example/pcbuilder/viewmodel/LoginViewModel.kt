package com.example.pcbuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcbuilder.data.dao.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.pcbuilder.data.model.User

class LoginViewModel(private val userDao: UserDao) : ViewModel() {
    private val _estado = MutableStateFlow(LoginUIState())
    val estado = _estado.asStateFlow()

    fun onCorreoChange(valor: String) { _estado.update { it.copy(correo = valor) } }
    fun onClaveChange(valor: String) { _estado.update { it.copy(clave = valor) } }

    fun iniciarSesion(onLoginSuccess: (user: User?) -> Unit) {
    val estadoActual = _estado.value
    viewModelScope.launch {
        val user = userDao.getUserByEmail(estadoActual.correo)
        if (user != null && user.password == estadoActual.clave) {
            // Éxito
            _estado.update { it.copy(error = null) }
            onLoginSuccess(user) // <-- 2. Devuelve el objeto 'user' completo
        } else {
            // Error
            _estado.update { it.copy(error = "Correo o contraseña inválidos") }
            onLoginSuccess(null) // <-- 3. Devuelve 'null' en caso de fallo
            }
        }
    }
}