package com.example.claritypay.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
import com.example.claritypay.presentation.components.InfoMessageCard
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.AuthUiState
import com.example.claritypay.presentation.viewmodels.AuthViewModel

@Composable
fun RegisterScreenRoute(onBackToLogin: () -> Unit) {
    val app = LocalContext.current.applicationContext as ClarityPayApp
    val viewModel: AuthViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RegisterScreen(
        state = state,
        onNameChange = viewModel::onNameChanged,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onCreateAccountClick = viewModel::register,
        onBackToLogin = onBackToLogin
    )
}

@Composable
private fun RegisterScreen(
    state: AuthUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onCreateAccountClick: () -> Unit,
    onBackToLogin: () -> Unit
) {
    // Variable local para el campo de confirmar contraseña
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Empieza con una base simple y deja lista tu app para crecer.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = onNameChange,
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(12.dp))
                // --- NUEVO CAMPO: Confirmar contraseña ---
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                state.errorMessage?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoMessageCard(it)
                }
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = {
                        // --- VALIDACIONES AL PRESIONAR EL BOTÓN ---
                        if (state.fullName.isBlank() || state.email.isBlank() || state.password.isBlank() || confirmPassword.isBlank()) {
                            Toast.makeText(context, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show()
                        } else if (state.password.length < 6) {
                            Toast.makeText(context, "La contraseña debe tener mínimo 6 caracteres.", Toast.LENGTH_SHORT).show()
                        } else if (state.password != confirmPassword) {
                            Toast.makeText(context, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Si todo es correcto, ejecuta la función original
                            onCreateAccountClick()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text(if (state.isLoading) "Creando..." else "Guardar y entrar")
                }
                TextButton(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) {
                    Text("Ya tengo una cuenta")
                }
            }
        }
    }
}