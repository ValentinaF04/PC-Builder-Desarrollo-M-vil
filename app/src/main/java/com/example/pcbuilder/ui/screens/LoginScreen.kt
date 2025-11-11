package com.example.pcbuilder.ui.screens


import com.example.pcbuilder.PCBuilderApplication
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pcbuilder.data.SecureStorage
import com.example.pcbuilder.data.SessionManager
import com.example.pcbuilder.navigation.AppRoutes
import com.example.pcbuilder.viewmodel.AppViewModelFactory
import com.example.pcbuilder.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

//interfaz visual

@Composable
fun LoginScreen(navController: NavController, factory: AppViewModelFactory){

    val context = LocalContext.current
    val activity = context as FragmentActivity
    val db = (context.applicationContext as PCBuilderApplication).database
    val factory = AppViewModelFactory(db.userDao(), db.productDao(), db.cartDao())
    val viewModel: LoginViewModel = viewModel (factory = factory)
    val estado by viewModel.estado.collectAsState()
    val scope = rememberCoroutineScope()
    val sessionManager = remember {SessionManager(context)}

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val executor: Executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt: BiometricPrompt

    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            val (email, pass) = SecureStorage.getCredentials(context)!!

            scope.launch {
                viewModel.onCorreoChange(email)
                viewModel.onClaveChange(pass)

                viewModel.iniciarSesion { user ->
                    if(user != null){
                        scope.launch {
                            sessionManager.saveUserId(user.uid)
                            val ruta = if(user.isAdmin){
                                AppRoutes.ADMIN_DASHBOARD
                            }else{
                                AppRoutes.CATALOGO_SCREEN
                            }
                            navController.navigate(ruta){
                                popUpTo(AppRoutes.LOGIN_SCREEN) { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }

    biometricPrompt = BiometricPrompt(activity, executor, callback)

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Inicio de Sesión Rápido")
        .setSubtitle("Usa tu huella para ingresar")
        .setNegativeButtonText("Cancelar")
        .build()


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Bienvenido", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = estado.clave,
            onValueChange = viewModel::onClaveChange,
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Default.VisibilityOff
                else
                    Icons.Default.Visibility

                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }){
                    Icon(imageVector = image, description)
                }
            }
        )

        AnimatedVisibility(visible = estado.error != null) {
            if (estado.error != null){
                Text(estado.error!!, color = MaterialTheme.colorScheme.error)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    scope.launch {
                        viewModel.iniciarSesion { user ->
                            if(user != null){
                                scope.launch {
                                    SecureStorage.saveCredentials(context, estado.correo, estado.clave)
                                    sessionManager.saveUserId(user.uid)

                                    val ruta = if(user.isAdmin){
                                        AppRoutes.ADMIN_DASHBOARD
                                    }else{
                                        AppRoutes.CATALOGO_SCREEN
                                    }
                                    navController.navigate(ruta){
                                        popUpTo(AppRoutes.LOGIN_SCREEN) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Ingresar")
            }

            if (SecureStorage.hasCredentials(context)) {
                IconButton(onClick = {
                    biometricPrompt.authenticate(promptInfo)
                }) {
                    Icon(Icons.Default.Fingerprint, contentDescription = "Login con huella")
                }
            }
        }

        TextButton(
            onClick = {
                navController.navigate(AppRoutes.REGISTRO_SCREEN)
            }
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}