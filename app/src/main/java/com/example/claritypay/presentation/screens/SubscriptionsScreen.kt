package com.example.claritypay.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
import com.example.claritypay.domain.models.Subscription
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.SubscriptionsViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun SubscriptionsScreenRoute() {
    val context = LocalContext.current
    val container = (context.applicationContext as ClarityPayApp).container
    val viewModel: SubscriptionsViewModel = viewModel(factory = AppViewModelFactory(container))

    SubscriptionsScreen(viewModel = viewModel)
}

@Composable
fun SubscriptionsScreen(viewModel: SubscriptionsViewModel) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Suscripción")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Gestión de Suscripciones", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Detalles y próximos pagos recurrentes", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(20.dp))

            if (subscriptions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay suscripciones activas. Pulsa + para empezar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(subscriptions) { sub ->
                        SubscriptionItem(sub, onDelete = { viewModel.deleteSubscription(sub) })
                    }
                }
            }
        }

        if (showDialog) {
            AddSubscriptionDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, amount, category, date, period ->
                    viewModel.addSubscription(name, amount, category, date, period)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AddSubscriptionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Entretenimiento") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var period by remember { mutableStateOf("Mensual") }

    val categories = listOf("Entretenimiento", "Servicios", "Telefonía", "Otro")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Suscripción") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre (ej. Netflix)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Monto mensual ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Fecha de cobro (aaaa-mm-dd)") }, modifier = Modifier.fillMaxWidth())

                Text("Categoría:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Text("Periodo:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = period == "Mensual", onClick = { period = "Mensual" }, label = { Text("Mensual") })
                    FilterChip(selected = period == "Anual", onClick = { period = "Anual" }, label = { Text("Anual") })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amountDouble = amount.toDoubleOrNull() ?: 0.0
                onConfirm(name, amountDouble, category, date, period)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun SubscriptionItem(subscription: Subscription, onDelete: () -> Unit) {
    val daysRemaining = remember(subscription.nextPaymentDate) {
        try {
            val today = LocalDate.now()
            val paymentDate = LocalDate.parse(subscription.nextPaymentDate)
            ChronoUnit.DAYS.between(today, paymentDate)
        } catch (e: Exception) { 999L }
    }

    val (statusText, statusColor) = when {
        daysRemaining < 0 -> "Vencido" to MaterialTheme.colorScheme.error
        daysRemaining == 0L -> "Se paga hoy" to MaterialTheme.colorScheme.error
        daysRemaining <= 3 -> "Pago Cerca" to MaterialTheme.colorScheme.error
        else -> "" to Color(0xFF2E7D32) // Un verde más oscuro y legible
    }

    val categoryIcon: ImageVector = when (subscription.category.lowercase()) {
        "entretenimiento" -> Icons.Default.Tv
        "servicios" -> Icons.Default.Lightbulb
        "telefonía" -> Icons.Default.Smartphone
        else -> Icons.Default.Star
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ENCABEZADO: Icono y Nombre
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = categoryIcon, contentDescription = null, modifier = Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = subscription.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    // CAMBIO: Categoría ahora en color onSurface (Oscuro)
                    Text(text = subscription.category, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // DETALLES: Fecha y Monto
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    // CAMBIO: Etiqueta PRÓXIMO COBRO más oscura
                    Text(text = "PRÓXIMO COBRO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    // CAMBIO: Fecha ahora más prominente
                    Text(text = subscription.nextPaymentDate, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = statusColor.copy(alpha = 0.15f),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(text = statusText.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Black)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "$${subscription.amount}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                    // CAMBIO: Periodo ahora más oscuro
                    Text(text = "Cobro ${subscription.period}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // BARRA DE PROGRESO (CU-19)
            if (daysRemaining in 0..31) {
                Spacer(modifier = Modifier.height(12.dp))
                val progress = 1f - (daysRemaining.toFloat() / 31f).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = if (daysRemaining <= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    strokeCap = StrokeCap.Round,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                // CAMBIO: Texto de días faltantes ahora más oscuro y en negrita si es urgente
                Text(
                    text = "Faltan $daysRemaining días para el cargo",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (daysRemaining <= 3) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                    color = if (daysRemaining <= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}