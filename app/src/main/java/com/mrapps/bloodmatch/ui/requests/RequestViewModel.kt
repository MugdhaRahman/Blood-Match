package com.mrapps.bloodmatch.ui.requests

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mrapps.bloodmatch.data.model.AppUser
import com.mrapps.bloodmatch.data.model.BloodRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RequestViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _requests = MutableStateFlow<List<BloodRequest>>(emptyList())
    val requests: StateFlow<List<BloodRequest>> = _requests

    private val _posting = MutableStateFlow(false)
    val posting: StateFlow<Boolean> = _posting

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        db.collection("requests")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snap, e ->
                if (e != null) { _error.value = e.message; return@addSnapshotListener }
                _requests.value = snap?.documents?.mapNotNull { d ->
                    d.toObject(BloodRequest::class.java)?.copy(id = d.id)
                } ?: emptyList()
            }
    }

    fun postRequest(
        bloodGroup: String, units: Int, hospital: String,
        city: String, urgency: String, note: String,
        onSuccess: () -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) { _error.value = "Not logged in"; return }
        _posting.value = true
        db.collection("users").document(uid).get()
            .addOnSuccessListener { u ->
                val name = u.toObject(AppUser::class.java)?.name ?: "Someone"
                val req = BloodRequest(
                    requesterId = uid, requesterName = name,
                    bloodGroup = bloodGroup, units = units,
                    hospital = hospital.trim(), city = city.trim(),
                    urgency = urgency, note = note.trim()
                )
                db.collection("requests").add(req)
                    .addOnSuccessListener { _posting.value = false; _error.value = null; onSuccess() }
                    .addOnFailureListener { _posting.value = false; _error.value = it.message }
            }
            .addOnFailureListener { _posting.value = false; _error.value = it.message }
    }

    fun clearError() { _error.value = null }
}
