package com.mrapps.bloodmatch.ui.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mrapps.bloodmatch.data.model.AppUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data class Error(val message: String) : AuthState
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    val currentUid: String? get() = auth.currentUser?.uid

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _state.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { _state.value = AuthState.Idle; onSuccess() }
            .addOnFailureListener { _state.value = AuthState.Error(it.message ?: "Login failed") }
    }

    fun register(name: String, email: String, phone: String, bloodGroup: String, password: String, onSuccess: () -> Unit) {
        _state.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { res ->
                val uid = res.user?.uid ?: return@addOnSuccessListener
                val user = AppUser(uid, name.trim(), email.trim(), phone.trim(), bloodGroup, createdAt = System.currentTimeMillis())
                db.collection("users").document(uid).set(user)
                    .addOnSuccessListener { _state.value = AuthState.Idle; onSuccess() }
                    .addOnFailureListener { _state.value = AuthState.Error(it.message ?: "Failed to save profile") }
            }
            .addOnFailureListener { _state.value = AuthState.Error(it.message ?: "Registration failed") }
    }

    fun logout(onDone: () -> Unit) {
        auth.signOut()
        onDone()
    }

    fun clearError() { _state.value = AuthState.Idle }
}
