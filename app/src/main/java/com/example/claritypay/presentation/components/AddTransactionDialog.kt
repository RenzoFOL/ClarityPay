package com.example.claritypay.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddTransactionDialog(
    title: String,
    defaultType: String,
    allowTypeSelection: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(if (defaultType == "INCOME") "Ingreso" else "Casa") }
    var dateLabel by remember { mutableStateOf("Hoy") }
    var type by remember { mutableStateOf(defaultType) }
    val categories = if (type == "INCOME") {
        listOf("Ingreso", "Nomina", "Venta", "Otro")
    } else {
        listOf("Casa", "Servicios", "Movilidad", "Personal", "Entretenimiento", "Otro")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (allowTypeSelection) {
                    Text("Tipo", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = type == "EXPENSE", onClick = {
                            type = "EXPENSE"
                            category = "Casa"
                        }, label = { Text("Gasto") })
                        FilterChip(selected = type == "INCOME", onClick = {
                            type = "INCOME"
                            category = "Ingreso"
                        }, label = { Text("Ingreso") })
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateLabel,
                    onValueChange = { dateLabel = it },
                    label = { Text("Fecha visible") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Categoria", style = MaterialTheme.typography.labelLarge)
                categories.chunked(2).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        rowItems.forEach { item ->
                            FilterChip(
                                selected = category == item,
                                onClick = { category = item },
                                label = { Text(item) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(name, amount.toDoubleOrNull() ?: 0.0, category, dateLabel, type)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
