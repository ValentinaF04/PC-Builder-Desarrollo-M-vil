package com.example.pcbuilder.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.pcbuilder.PCBuilderApplication
import com.example.pcbuilder.data.SessionManager
import com.example.pcbuilder.data.model.User
import com.example.pcbuilder.navigation.AppRoutes
import com.example.pcbuilder.ui.theme.*
import com.example.pcbuilder.viewmodel.AppViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, factory: AppViewModelFactory) {
    val context = LocalContext.current
    val db = (context.applicationContext as PCBuilderApplication).database
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    val userId by sessionManager.userIdFlow.collectAsState(initial = null)
    var user by remember { mutableStateOf<User?>(null) }

    var isEditing by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Cargar usuario
    LaunchedEffect(userId) {
        userId?.let { id ->
            val u = db.userDao().getUserById(id)
            user = u
            if (u != null) {
                nombre = u.name
                email = u.email
                // Ahora estos campos SI existen en User
                direccion = u.direccion ?: ""
                if (u.profileImageUri != null) {
                    selectedImageUri = Uri.parse(u.profileImageUri)
                }
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) selectedImageUri = uri }
    )

    Scaffold(
        containerColor = DeepViolet,
        topBar = {
            TopAppBar(
                title = { Text("MI PERFIL", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (isEditing) {
                            scope.launch {
                                user?.let { currentUser ->
                                    // Ahora copiaremos los nuevos campos correctamente
                                    val updatedUser = currentUser.copy(
                                        name = nombre,
                                        direccion = direccion,
                                        profileImageUri = selectedImageUri?.toString()
                                    )
                                    db.userDao().insertUser(updatedUser)
                                    user = updatedUser
                                    isEditing = false
                                }
                            }
                        } else {
                            isEditing = true
                        }
                    }) {
                        Icon(
                            if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Guardar" else "Editar",
                            tint = if (isEditing) NeonBlue else TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepViolet,
                    titleContentColor = NeonBlue,
                    navigationIconContentColor = TextWhite,
                    actionIconContentColor = TextWhite
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // SECCIÓN FOTO
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .border(3.dp, Brush.sweepGradient(listOf(NeonPurple, NeonBlue, NeonPurple)), CircleShape)
                            .padding(5.dp)
                            .clip(CircleShape)
                            .background(SurfaceViolet)
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(20.dp),
                                tint = TextGray
                            )
                        }
                    }

                    // Botón cámara (Reemplazado AnimatedVisibility por if para evitar errores)
                    if (isEditing) {
                        SmallFloatingActionButton(
                            onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            containerColor = NeonBlue,
                            contentColor = DeepViolet,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, "Cambiar foto", modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // TARJETA DE DATOS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceViolet.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonBlue.copy(alpha=0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Box(
                            modifier = Modifier
                                .background(if (user?.isAdmin == true) NeonPurple else NeonBlue, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .align(Alignment.End)
                        ) {
                            Text(
                                text = if (user?.isAdmin == true) "ADMINISTRADOR" else "CLIENTE",
                                style = MaterialTheme.typography.labelSmall,
                                color = DeepViolet,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        ProfileInfoField(label = "Nombre Completo", value = nombre, isEditing = isEditing) { nombre = it }
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileInfoField(label = "Correo Electrónico", value = email, isEditing = false) { }
                        Spacer(modifier = Modifier.height(16.dp))
                        ProfileInfoField(label = "Dirección de Envío", value = direccion, isEditing = isEditing) { direccion = it }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            sessionManager.clearSession()
                            navController.navigate(AppRoutes.LOGIN_SCREEN) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCF6679)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCF6679))
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CERRAR SESIÓN")
                }
            }
        }
    }
}

@Composable
fun ProfileInfoField(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = TextGray)
        Spacer(modifier = Modifier.height(4.dp))

        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonBlue,
                    unfocusedBorderColor = TextGray,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = NeonBlue,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        } else {
            Text(
                text = value.ifEmpty { "No especificado" },
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.SemiBold
            )
            HorizontalDivider(color = NeonBlue.copy(alpha = 0.2f), modifier = Modifier.padding(top = 8.dp))
        }
    }
}