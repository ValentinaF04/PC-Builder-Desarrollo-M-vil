package com.example.pcbuilder.ui.screens

import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pcbuilder.PCBuilderApplication
import com.example.pcbuilder.data.SecureStorage
import com.example.pcbuilder.data.SessionManager
import com.example.pcbuilder.navigation.AppRoutes
import com.example.pcbuilder.ui.theme.*
import com.example.pcbuilder.viewmodel.AppViewModelFactory
import com.example.pcbuilder.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, factory: AppViewModelFactory) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val db = (context.applicationContext as PCBuilderApplication).database
    val vmFactory = AppViewModelFactory(db.userDao(), db.productDao(), db.cartDao())
    val viewModel: LoginViewModel = viewModel(factory = vmFactory)

    val estado by viewModel.estado.collectAsState()
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Configuración Biométrica
    val executor: Executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt = remember {
        BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val credentials = SecureStorage.getCredentials(context)
                if (credentials != null) {
                    val (email, pass) = credentials
                    scope.launch {
                        viewModel.onCorreoChange(email)
                        viewModel.onClaveChange(pass)
                        viewModel.iniciarSesion { user ->
                            if (user != null) {
                                scope.launch {
                                    sessionManager.saveUserId(user.uid)
                                    val ruta = if (user.isAdmin) AppRoutes.ADMIN_DASHBOARD else AppRoutes.CATALOGO_SCREEN
                                    navController.navigate(ruta) { popUpTo(AppRoutes.LOGIN_SCREEN) { inclusive = true } }
                                }
                            }
                        }
                    }
                }
            }
        })
    }
    val promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Login Biométrico").setNegativeButtonText("Cancelar").build()

    // --- DISEÑO VISUAL ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = BackgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BIENVENIDO",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = NeonBlue
            )

            Spacer(modifier = Modifier.height(32.dp))

            CyberTextField(
                value = estado.correo,
                onValueChange = viewModel::onCorreoChange,
                label = "Correo Electrónico"
            )

            Spacer(modifier = Modifier.height(16.dp))

            CyberTextField(
                value = estado.clave,
                onValueChange = viewModel::onClaveChange,
                label = "Contraseña",
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible }
            )

            AnimatedVisibility(visible = estado.error != null) {
                Text(
                    text = estado.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        viewModel.iniciarSesion { user ->
                            if(user != null){
                                scope.launch {
                                    SecureStorage.saveCredentials(context, estado.correo, estado.clave)
                                    sessionManager.saveUserId(user.uid)
                                    val ruta = if(user.isAdmin) AppRoutes.ADMIN_DASHBOARD else AppRoutes.CATALOGO_SCREEN
                                    navController.navigate(ruta){ popUpTo(AppRoutes.LOGIN_SCREEN) { inclusive = true } }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(brush = PrimaryGradient, shape = RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("INGRESAR", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (SecureStorage.hasCredentials(context)) {
                IconButton(
                    onClick = { biometricPrompt.authenticate(promptInfo) },
                    modifier = Modifier
                        .size(60.dp)
                        .border(1.dp, NeonBlue, RoundedCornerShape(50))
                ) {
                    Icon(
                        Icons.Default.Fingerprint,
                        contentDescription = "Huella",
                        tint = NeonBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate(AppRoutes.REGISTRO_SCREEN) }) {
                Text("¿No tienes cuenta? ", color = TextGray)
                Text("Regístrate", color = NeonPurple, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Componente Reutilizable Corregido
@Composable
fun CyberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonPurple,
            unfocusedBorderColor = NeonBlue.copy(alpha = 0.5f),
            focusedContainerColor = SurfaceViolet.copy(alpha = 0.4f),
            unfocusedContainerColor = SurfaceViolet.copy(alpha = 0.4f),
            focusedLabelColor = NeonPurple,
            unfocusedLabelColor = TextGray,
            cursorColor = NeonBlue,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = TextGray
                    )
                }
            }
        } else null
    )
}