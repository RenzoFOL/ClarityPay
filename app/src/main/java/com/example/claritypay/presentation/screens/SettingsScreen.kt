package com.example.claritypay.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.SettingsViewModel

@Composable
fun SettingsScreenRoute(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as ClarityPayApp).container
    val viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory(container))

    SettingsScreen(viewModel = viewModel, onNavigateBack = onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsStateWithLifecycle()

    // Estado local para controlar la visibilidad de la pantalla flotante (Dialog)
    var showHelpDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Preferencias de la aplicación",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF287C71), // Verde azulado de tu diseño
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // CU-21: Toggle Modo Oscuro
            SettingsToggleCard(
                icon = Icons.Default.DarkMode,
                title = "Modo Oscuro",
                subtitle = "Cambia la apariencia de la aplicación",
                isChecked = isDarkMode,
                onCheckedChange = { viewModel.setDarkMode(it) }
            )

            // CU-16: Toggle Notificaciones
            SettingsToggleCard(
                icon = Icons.Default.NotificationsActive,
                title = "Notificaciones",
                subtitle = "Recibe alertas de tus presupuestos",
                isChecked = isNotificationsEnabled,
                onCheckedChange = { viewModel.setNotifications(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Soporte",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF287C71),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // CU-17: Ayuda y Soporte (Fase 6)
            SettingsActionCard(
                icon = Icons.Default.HelpOutline,
                title = "Ayuda y soporte",
                subtitle = "Resuelve tus dudas sobre ClarityPay",
                onClick = { showHelpDialog = true }
            )
        }

        // Mostrar el diálogo flotante si el estado es verdadero
        if (showHelpDialog) {
            HelpAndSupportDialog(
                onDismiss = { showHelpDialog = false }
            )
        }
    }
}

// Tarjeta original para Switches (Modo oscuro, Notificaciones)
@Composable
fun SettingsToggleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF287C71),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF287C71)
                )
            )
        }
    }
}

// NUEVA Tarjeta para acciones clickeables (Ayuda y soporte)
@Composable
fun SettingsActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }, // Hace que toda la tarjeta sea clickeable con efecto visual
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF287C71),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Abrir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Componente flotante de Ayuda y Soporte
@Composable
fun HelpAndSupportDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, // Cierra al tocar fuera del cuadro
        title = {
            Text(
                text = "Centro de Ayuda",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF287C71)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Bienvenido al soporte de ClarityPay. Aquí tienes una guía rápida de las funciones principales:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "• Inicio: Visualiza el balance total y el resumen de tus ingresos y gastos del mes actual.\n" +
                            "• Estadísticas: Analiza en qué categorías gastas más dinero.\n" +
                            "• Suscripciones: Mantén el control de tus pagos recurrentes y recordatorios.\n" +
                            "• Perfil: Edita tus datos personales y configura la aplicación.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Si tienes un problema técnico con tu cuenta, por favor contacta al administrador del sistema.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aceptar", fontWeight = FontWeight.Bold, color = Color(0xFF287C71))
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}