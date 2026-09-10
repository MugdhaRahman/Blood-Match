package com.mrapps.bloodmatch.ui.screens.requests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrapps.bloodmatch.data.model.BloodRequest
import com.mrapps.bloodmatch.ui.requests.RequestViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RequestsTab(vm: RequestViewModel = viewModel()) {
    val requests by vm.requests.collectAsState()
    val error by vm.error.collectAsState()
    var showCreate by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Filled.Add, contentDescription = "New request")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }
            if (requests.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No blood requests yet.\nTap + to post one.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(requests, key = { it.id }) { RequestCard(it) }
                }
            }
        }
        if (showCreate) {
            CreateRequestDialog(vm = vm, onDismiss = { showCreate = false })
        }
    }
}

@Composable
private fun RequestCard(r: BloodRequest) {
    val fmt = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Bloodtype, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("${r.bloodGroup}  •  ${r.units} unit(s)  •  ${r.urgency}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Text("${r.hospital} — ${r.city}", style = MaterialTheme.typography.bodyMedium)
                Text("by ${r.requesterName} • ${fmt.format(Date(r.createdAt))}", style = MaterialTheme.typography.bodySmall)
                if (r.note.isNotBlank()) Text(r.note, style = MaterialTheme.typography.bodySmall)
            }
            AssistChip(onClick = {}, label = { Text(r.status) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRequestDialog(vm: RequestViewModel, onDismiss: () -> Unit) {
    var bloodGroup by remember { mutableStateOf("O+") }
    var bgExpanded by remember { mutableStateOf(false) }
    var units by remember { mutableStateOf("1") }
    var hospital by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("Normal") }
    var urgencyExpanded by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }
    val posting by vm.posting.collectAsState()
    val groups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    val urgencies = listOf("Normal", "Urgent", "Critical")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Blood") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(expanded = bgExpanded, onExpandedChange = { bgExpanded = !bgExpanded }, modifier = Modifier.weight(1f)) {
                        OutlinedTextField(value = bloodGroup, onValueChange = {}, readOnly = true, label = { Text("Group") }, modifier = Modifier.menuAnchor().fillMaxWidth())
                        ExposedDropdownMenu(expanded = bgExpanded, onDismissRequest = { bgExpanded = false }) {
                            groups.forEach { g -> DropdownMenuItem(text = { Text(g) }, onClick = { bloodGroup = g; bgExpanded = false }) }
                        }
                    }
                    OutlinedTextField(value = units, onValueChange = { units = it.filter(Char::isDigit).take(2) }, label = { Text("Units") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                OutlinedTextField(value = hospital, onValueChange = { hospital = it }, label = { Text("Hospital") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                ExposedDropdownMenuBox(expanded = urgencyExpanded, onExpandedChange = { urgencyExpanded = !urgencyExpanded }, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = urgency, onValueChange = {}, readOnly = true, label = { Text("Urgency") }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = urgencyExpanded, onDismissRequest = { urgencyExpanded = false }) {
                        urgencies.forEach { u -> DropdownMenuItem(text = { Text(u) }, onClick = { urgency = u; urgencyExpanded = false }) }
                    }
                }
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note (optional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    vm.postRequest(bloodGroup, units.toIntOrNull() ?: 1, hospital, city, urgency, note, onDismiss)
                },
                enabled = !posting && hospital.isNotBlank() && city.isNotBlank()
            ) { Text(if (posting) "Posting..." else "Post") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
