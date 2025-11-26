package com.example.pcbuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pcbuilder.data.dao.UserDao
import com.example.pcbuilder.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(private val userDao: UserDao) : ViewModel(){

    private val _estado = MutableStateFlow(RegistroUIState())
    val estado: StateFlow<RegistroUIState> = _estado.asStateFlow()

    fun onNombreChange(valor: String){
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, errores = it.errores.copy(direccion = null)) }
    }

    fun onAceptaTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    fun obtenerUbicacion(context: android.content.Context) {
        onDireccionChange("Ubicación detectada por GPS")
    }

    fun guardarUsuario(onSuccess: () -> Unit){
        val estadoActual = _estado.value

        val nuevoUsuario = User(
            name = estadoActual.nombre,
            email = estadoActual.correo,
            password = estadoActual.clave,
            direccion = estadoActual.direccion, // Guardamos la dirección
            isAdmin = false, // Por defecto es cliente
            profileImageUri = null // Imagen vacía al inicio
        )

        viewModelScope.launch {
            userDao.insertUser(nuevoUsuario)
            onSuccess()
        }
    }

    fun validarForm(): Boolean{
        val estadoActual = _estado.value

        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "No puede estar vacío" else null,
            correo = when{
                estadoActual.correo.isBlank() -> "El correo es obligatorio"
                !estadoActual.correo.contains("@") -> "Formato de correo inválido"
                estadoActual.correo.length < 5 -> "El email es demasiado corto"
                else -> null },
            clave = if (estadoActual.clave.length < 8) "La clave debe tener al menos 8 caracteres" else null,
            direccion = if (estadoActual.direccion.isBlank()) "La dirección es requerida" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.clave,
            errores.direccion
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }

        return !hayErrores
    }
}