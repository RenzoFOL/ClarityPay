package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.repository.AuthRepository

class ResetPasswordUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, nuevaContrasena: String, confirmarContrasena: String) {

        // 1. Validar que los campos no estén vacíos
        if (email.isBlank() || nuevaContrasena.isBlank() || confirmarContrasena.isBlank()) {
            throw Exception("Por favor, llena todos los campos.")
        }

        // 2. Validar que las contraseñas coincidan
        if (nuevaContrasena != confirmarContrasena) {
            throw Exception("Las contraseñas no coinciden.")
        }

        // 3. Validación: Mínimo 6 caracteres
        if (nuevaContrasena.length < 6) {
            throw Exception("La contraseña debe tener mínimo 6 caracteres.")
        }

        // 4. Validación: El correo debe existir en la base de datos
        val user = authRepository.getUserByEmail(email)
        if (user == null) {
            throw Exception("El correo no existe.")
        }

        // 5. Si todo es correcto, guardamos la nueva contraseña
        authRepository.updatePassword(email, nuevaContrasena)
    }
}