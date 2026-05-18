package com.example.claritypay.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
import com.example.claritypay.domain.models.PricingPlan
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.PlansViewModel
import com.example.claritypay.presentation.viewmodels.ProfileViewModel

@Composable
fun ProfileScreenRoute() {
    val context = LocalContext.current
    val container = (context.applicationContext as ClarityPayApp).container
    val profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelFactory(container))
    val plansViewModel: PlansViewModel = viewModel(factory = AppViewModelFactory(container))

    ProfileScreen(profileViewModel = profileViewModel, plansViewModel = plansViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel, plansViewModel: PlansViewModel) {
    val user by profileViewModel.user.collectAsState()
    val plans by plansViewModel.plans.collectAsState()
    var name by remember(user) { mutableStateOf(user?.fullName ?: "") }
    var bio by remember(user) { mutableStateOf(user?.bio ?: "") }
    var showPlansSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Perfil de Usuario", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Biografía") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

        Button(onClick = { profileViewModel.updateProfile(name, bio) }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
            Text("Guardar Cambios")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // AC-12 ACCESO A PLANES DESDE EL PERFIL (Diseño UX BBVA)
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