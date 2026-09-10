package com.mrapps.bloodmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mrapps.bloodmatch.ui.auth.AuthViewModel
import com.mrapps.bloodmatch.ui.navigation.Routes
import com.mrapps.bloodmatch.ui.screens.auth.LoginScreen
import com.mrapps.bloodmatch.ui.screens.auth.RegisterScreen
import com.mrapps.bloodmatch.ui.screens.home.HomeScreen
import com.mrapps.bloodmatch.ui.theme.BloodMatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloodMatchTheme {
                BloodMatchApp()
            }
        }
    }
}
@Composable
fun BloodMatchApp(vm: AuthViewModel = viewModel()) {
    val nav = rememberNavController()
    val start = if (FirebaseAuth.getInstance().currentUser != null) Routes.HOME else Routes.LOGIN
    NavHost(navController = nav, startDestination = start) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onRegisterClick = { nav.navigate(Routes.REGISTER) },
                vm = vm
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { nav.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onLoginClick = { nav.popBackStack() },
                vm = vm
            )
        }
        composable(Routes.HOME) {
            HomeScreen(onLogout = { vm.logout { nav.navigate(Routes.LOGIN) { popUpTo(Routes.HOME) { inclusive = true } } } })
        }
    }
}
