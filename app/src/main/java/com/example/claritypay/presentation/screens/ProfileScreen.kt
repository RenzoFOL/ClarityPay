package com.example.claritypay.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.ProfileViewModel
import com.example.claritypay.presentation.viewmodels.PlansViewModel
import com.example.claritypay.domain.models.PricingPlan

@Composable
fun ProfileScreenRoute(onNavigateToSettings: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as ClarityPayApp).container

    // Instanciamos los dos ViewModels que necesita esta pantalla
    val viewModel: ProfileViewModel = viewModel(factory = AppViewModelFactory(container))
    val plansViewModel: PlansViewModel = viewModel(factory = AppViewModelFactory(container))

    ProfileScreen(
        viewModel = viewModel,
        plansViewModel = plansViewModel,
        onNavigateToSettings = onNavigateToSettings,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    plansViewModel: PlansViewModel,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val user by viewModel.user.collectAsState()

    // Recolectamos la lista de planes desde tu PlansViewModel
    // Nota: Asegúrate de que la variable en tu ViewModel se llame "plans". Si se llama distinto, cámbialo aquí.
    val plans by plansViewModel.plans.collectAsState(initial = emptyList())

    val context = LocalContext.current

    // Estados para los campos
    var nombre by remember(user) { mutableStateOf(user?.fullName ?: "") }
    var usuario by remember(user) { mutableStateOf(user?.email ?: "") }
    var contrasena by remember(user) { mutableStateOf(user?.password ?: "") }

    // Estados para los diálogos y paneles
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPlansSheet by remember { mutableStateOf(false) }

    // Escuchar eventos de éxito
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. Icono de perfil centrado
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF287C71).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color(0xFF287C71)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Campo Usuario (Email)
            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario / Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Campo Contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 5. Botón Guardar Cambios
            Button(
                onClick = {
                    // --- VALIDACIÓN AÑADIDA AQUÍ ---
                    if (contrasena.isNotBlank() && contrasena.length < 6) {
                        Toast.makeText(context, "La contraseña debe tener mínimo 6 caracteres.", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.saveChanges(nombre, usuario, contrasena)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF287C71))
            ) {
                Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AC-12 ACCESO A PLANES DESDE EL PERFIL (Diseño UX BBVA original)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                onClick = { showPlansSheet = true }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Suscripción de Cuenta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text("Ver planes disponibles y beneficios pro", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Botón Eliminar Cuenta
            TextButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Eliminar cuenta", color = Color.Red, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // PANEL EXPANDIBLE DE PLANES (CU-12)
    if (showPlansSheet) {
        ModalBottomSheet(onDismissRequest = { showPlansSheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)) {
                Text("Planes Disponibles", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f, fill = false)) {
                    items(plans) { plan ->
                        PlanCardItem(plan)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // 7. Diálogo de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar cuenta?", fontWeight = FontWeight.Bold) },
            text = { Text("Tu cuenta se eliminará definitivamente al dar clic en aceptar. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount {
                            showDeleteDialog = false
                            onLogout() // Redirige al login
                        }
                    }
                ) {
                    Text("Aceptar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PlanCardItem(plan: PricingPlan) {
    val borderColor = if (plan.isPopular) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(if (plan.isPopular) 2.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = plan.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                if (plan.isCurrent) {
                    // Texto indicativo de que este es el plan inicial activo
                    Text("Plan Actual", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            }

            // CAMBIO: Si es gratis muestra "Gratis", de lo contrario muestra "$69 MXN / Mes" o "$690 MXN / Año"
            Text(
                text = if (plan.price == 0.0) "Gratis" else "$${String.format("%.0f", plan.price)} MXN / ${plan.period}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            plan.features.forEach {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}