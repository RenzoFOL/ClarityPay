package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.domain.usecases.ResetPasswordUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    // Flujo para enviar mensajes Toast a la pantalla
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent

    // Flujo para avisar a la pantalla que la contraseña se guardó y debe navegar
    private val _resetSuccess = MutableSharedFlow<Boolean>()
    val resetSuccess: SharedFlow<Boolean> = _resetSuccess

    fun resetPassword(email: String, nuevaContrasena: String, confirmarContrasena: String) {
        viewModelScope.launch {
            try {
                resetPasswordUseCase(email, nuevaContrasena, confirmarContrasena)

                // Mensaje de éxito solicitado
                _uiEvent.emit("Datos guardados correctamente")
                _resetSuccess.emit(true)

            } catch (e: Exception) {
                // Muestra las excepciones configuradas en el caso de uso
                _uiEvent.emit(e.message ?: "Ocurrió un error inesperado")
            }
        }
    }
}