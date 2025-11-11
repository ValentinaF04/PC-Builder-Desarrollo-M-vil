package com.example.pcbuilder.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pcbuilder.ui.theme.PCBuilderTheme
import com.example.pcbuilder.viewmodel.AppViewModelFactory
import com.example.pcbuilder.viewmodel.RegistroViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun RegistroScreen(
    navController: NavController,
    factory: AppViewModelFactory
) {
    val viewModel: RegistroViewModel = viewModel(factory = factory)

    val context = LocalContext.current
    val estado by viewModel.estado.collectAsState()
    val scope = rememberCoroutineScope()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val geocoder = Geocoder(context, Locale.getDefault())
    val locationClient = LocationServices.getFusedLocationProviderClient(context)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                @SuppressLint("MissingPermission")
                locationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        try {
                            val addresses = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )
                            if (addresses != null && addresses.isNotEmpty()) {
                                val address = addresses[0].getAddressLine(0)
                                viewModel.onDireccionChange(address)
                            }
                        } catch (e: Exception) {

                        }
                    }
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text("Crea tu cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = estado.nombre,
            onValueChange = viewModel::onNombreChange,
            label = { Text("Nombre Completo") },
            isError = estado.errores.nombre != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        AnimatedVisibility(visible = estado.errores.nombre != null) {
            Text(estado.errores.nombre ?: "", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = estado.correo,
            onValueChange = viewModel::onCorreoChange,
            label = { Text("Correo") },
            isError = estado.errores.correo != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        val mensajeError = estado.errores.correo
        AnimatedVisibility(visible = mensajeError != null) {
            mensajeError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }

        OutlinedTextField(
            value = estado.clave,
            onValueChange = viewModel::onClaveChange,
            label = { Text("Contraseña (mín. 8 caracteres)") },
            isError = estado.errores.clave != null,
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
        AnimatedVisibility(visible = estado.errores.clave != null) {
            Text(estado.errores.clave ?: "", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = estado.direccion,
            onValueChange = viewModel::onDireccionChange,
            label = { Text("Dirección") },
            isError = estado.errores.direccion != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        AnimatedVisibility(visible = estado.errores.direccion != null) {
            Text(estado.errores.direccion ?: "", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Usar mi ubicación actual")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = estado.aceptaTerminos,
                onCheckedChange = viewModel::onAceptaTerminosChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Acepto los términos y condiciones", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (viewModel.validarForm()){
                    scope.launch{
                        viewModel.guardarUsuario{
                            navController.popBackStack()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = estado.aceptaTerminos
        ) {
            Text("Crear cuenta")
        }

        TextButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Ya tienes cuenta? Inicia Sesión")
        }
    }
}