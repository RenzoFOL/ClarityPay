package com.example.claritypay.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.ResetPasswordViewModel

@Composable
fun ResetPasswordScreenRoute(onBackToLogin: () -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as ClarityPayApp).container
    val viewModel: ResetPasswordViewModel = viewModel(factory = AppViewModelFactory(container))

    ResetPasswordScreen(viewModel = viewModel, onBackToLogin = onBackToLogin)
}

@Composable
fun ResetPasswordScreen(viewModel: ResetPasswordViewModel, onBackToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Recuperar cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Ingresa tu correo y la nueva contraseña.")
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Nueva contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.resetPassword(email, newPassword) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restablecer Contraseña")
        }

        TextButton(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar")
        }
    }
}