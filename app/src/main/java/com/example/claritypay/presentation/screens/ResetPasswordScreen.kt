package com.example.claritypay.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.ResetPasswordViewModel

@Composable
fun ResetPasswordScreenRoute(
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as ClarityPayApp).container
    val viewModel: ResetPasswordViewModel = viewModel(factory = AppViewModelFactory(container))

    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") } // 1. Nuevo campo añadido

    val tealColor = Color(0xFF287C71)

    // Escuchar mensajes de excepción y éxito (Toasts)
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Escuchar redirección exitosa
    LaunchedEffect(Unit) {
        viewModel.resetSuccess.collect { success ->
            if (success) {
                onBackToLogin() // Te regresa al inicio de sesión
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Recuperar Contraseña", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = tealColor)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Ingresa tu correo y nueva contraseña", color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newPassword, onValueChange = { newPassword = it },
            label = { Text("Nueva contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it },
            label = { Text("Confirmar nueva contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            // 4. El botón ahora guarda los cambios y valida
            onClick = { viewModel.resetPassword(email, newPassword, confirmPassword) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = tealColor)
        ) {
            Text("Restablecer contraseña", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Regresar al inicio de sesión", color = tealColor)
        }
    }
}