package com.mrapps.bloodmatch.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class BottomTab(val title: String, val icon: ImageVector)

val tabs = listOf(
    BottomTab("Home", Icons.Filled.Home),
    BottomTab("Donors", Icons.Filled.Bloodtype),
    BottomTab("Requests", Icons.Filled.VolunteerActivism),
    BottomTab("Profile", Icons.Filled.Person)
)

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    var selected by remember { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { i, t ->
                    NavigationBarItem(selected = selected == i, onClick = { selected = i }, label = { Text(t.title) }, icon = { Icon(t.icon, contentDescription = t.title) })
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (selected) {
                0 -> Box(Modifier.padding(24.dp)) { HomeTab(onRequestClick = { selected = 2 }) }
                1 -> Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) { PlaceholderTab("Find blood donors near you.\nComing soon.") }
                2 -> com.mrapps.bloodmatch.ui.screens.requests.RequestsTab()
                3 -> Box(Modifier.padding(24.dp), contentAlignment = Alignment.Center) { ProfileTab(onLogout) }
            }
        }
    }
}

@Composable
private fun HomeTab(onRequestClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.Bloodtype, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(72.dp))
        Text("Welcome to BloodMatch", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text("Donate blood, save lives.", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(16.dp)) {
                Text("Emergency? Request blood now", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRequestClick, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.primary)) { Text("Request Blood") }
            }
        }
    }
}

@Composable
private fun PlaceholderTab(text: String) {
    Text(text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
private fun ProfileTab(onLogout: () -> Unit) {
    var profile by remember { mutableStateOf<com.mrapps.bloodmatch.data.model.AppUser?>(null) }
    LaunchedEffect(Unit) {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { profile = it.toObject(com.mrapps.bloodmatch.data.model.AppUser::class.java) }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
        Text(profile?.name ?: "Loading...", style = MaterialTheme.typography.headlineSmall)
        Text(profile?.email ?: "", style = MaterialTheme.typography.bodyMedium)
        Text("${profile?.bloodGroup ?: ""}  •  ${profile?.phone ?: ""}", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onLogout) { Text("Logout") }
    }
}
