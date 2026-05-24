package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.domain.models.User
import com.example.claritypay.domain.usecases.DeleteAccountUseCase
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.UpdateProfileUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    // Estado para mensajes de éxito (Snackbar/Toast)
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { _user.value = it }
        }
    }

    fun saveChanges(nombre: String, usuario: String, contrasena: String) {
        if (nombre.isBlank() || usuario.isBlank() || contrasena.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit("Por favor, completa todos los campos antes de guardar.")
            }
            return
        }

        val currentUser = _user.value ?: return
        viewModelScope.launch {
            val updatedUser = currentUser.copy(
                fullName = nombre.trim(),
                email = usuario.trim(),
                password = contrasena.trim()
            )
            updateProfileUseCase(updatedUser)
            _uiEvent.emit("Cambios guardados correctamente")
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        val currentUser = _user.value ?: return
        viewModelScope.launch {
            deleteAccountUseCase(currentUser.id)
            onDeleted()
        }
    }

    fun updateProfile(name: String, bio: String) {}
}