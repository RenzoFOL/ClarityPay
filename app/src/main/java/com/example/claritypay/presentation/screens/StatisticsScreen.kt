package com.example.claritypay.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
import com.example.claritypay.domain.models.Insight
import com.example.claritypay.domain.models.InsightType
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.InsightsViewModel
import com.example.claritypay.presentation.viewmodels.StatisticsViewModel

@Composable
fun StatisticsScreenRoute() {
    val context = LocalContext.current
    val container = (context.applicationContext as ClarityPayApp).container
    val statsViewModel: StatisticsViewModel = viewModel(factory = AppViewModelFactory(container))
    val insightsViewModel: InsightsViewModel = viewModel(factory = AppViewModelFactory(container))

    StatisticsScreen(statsViewModel = statsViewModel, insightsViewModel = insightsViewModel)
}

@Composable
fun StatisticsScreen(statsViewModel: StatisticsViewModel, insightsViewModel: InsightsViewModel) {
    val stats by statsViewModel.categoryStats.collectAsState()
    val insights by insightsViewModel.financialInsights.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Análisis Financiero", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // Selector elegante de sub-pantallas
        TabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Categorías") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Insights IA") })
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            // CÓDIGO ORIGINAL SIN ALTERAR LOGICA
            if (stats.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Registra gastos para ver estadísticas", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(stats) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item.category, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                Text("$${String.format("%.2f", item.totalAmount)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // VISTA NUEVA EXCLUSIVA DE INSIGHTS
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(insights) { insight ->
                    InsightCardItem(insight)
                }
            }
        }
    }
}

@Composable
fun InsightCardItem(insight: Insight) {
    val (bgColor, contentColor, icon) = when (insight.type) {
        InsightType.SUCCESS -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Icons.Default.CheckCircle)
        InsightType.WARNING -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Icons.Default.Warning)
        InsightType.INFO -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), Icons.Default.Info)
    }

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = insight.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = contentColor)
                    if (insight.valueLabel.isNotEmpty()) {
                        Text(text = insight.valueLabel, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black, color = contentColor)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = insight.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}