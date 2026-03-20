package dev.spiffocode.sigesmobile.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.ui.screens.applicant.ApplicantHomeScreen
import dev.spiffocode.sigesmobile.ui.screens.login.LoginScreen
import dev.spiffocode.sigesmobile.ui.screens.passwordRecovery.ExpiredLinkScreen
import dev.spiffocode.sigesmobile.ui.screens.passwordRecovery.ForgotPasswordScreen
import dev.spiffocode.sigesmobile.ui.screens.passwordRecovery.ResetPasswordScreen
import dev.spiffocode.sigesmobile.ui.screens.passwordRecovery.UsedLinkScreen
import dev.spiffocode.sigesmobile.ui.screens.profile.ProfileScreen
import dev.spiffocode.sigesmobile.ui.theme.Lav
import dev.spiffocode.sigesmobile.ui.theme.Plum
import dev.spiffocode.sigesmobile.ui.theme.TextSecondary
import dev.spiffocode.sigesmobile.viewmodel.EditReservationViewModel
import dev.spiffocode.sigesmobile.viewmodel.ReservationDetailViewModel
import dev.spiffocode.sigesmobile.viewmodel.ResetPasswordError
import dev.spiffocode.sigesmobile.viewmodel.ResetPasswordViewModel


object Routes {
    const val LOGIN            = "login"
    const val FORGOT_PASSWORD  = "forgot_password"
    const val RESET_PASSWORD   = "reset_password/{token}"
    const val EXPIRED_LINK     = "expired_link"
    const val USED_LINK        = "used_link"
    fun resetPassword(token: String) = "reset_password/$token"

    const val HOME           = "home"
    const val AVAILABILITY   = "availability"
    const val MY_REQUESTS    = "requests"
    const val NEW_REQUEST    = "new_request"
    const val REQUEST_DETAIL = "request_detail/{reservationId}"
    const val EDIT_REQUEST   = "edit_request/{reservationId}"
    fun requestDetail(id: Long) = "request_detail/$id"
    fun editRequest(id: Long)   = "edit_request/$id"

    const val ADMIN_HOME         = "admin_home"
    const val ADMIN_ALL_REQUESTS = "admin_all_requests"

    const val PROFILE            = "profile"
    const val EDIT_PROFILE       = "edit_profile"
    const val NOTIFICATION_PREFS = "notification_prefs"
    const val CHANGE_PASSWORD    = "change_password/{token}"
    fun changePassword(token: String) = "change_password/$token"
}


sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home         : BottomNavItem(Routes.HOME, "Inicio", Icons.Default.Home)
    object Availability : BottomNavItem(Routes.AVAILABILITY, "Buscar", Icons.Default.Search)
    object Requests     : BottomNavItem(Routes.MY_REQUESTS, "Solicitudes", Icons.Default.List)
    object Profile      : BottomNavItem(Routes.PROFILE, "Perfil", Icons.Default.Person)
}

sealed class AdminBottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home     : AdminBottomNavItem(Routes.ADMIN_HOME, "Inicio", Icons.Default.Home)
    object Requests : AdminBottomNavItem(Routes.ADMIN_ALL_REQUESTS, "Solicitudes", Icons.Default.List)
    object Profile  : AdminBottomNavItem(Routes.PROFILE, "Perfil", Icons.Default.Person)
}

private val noBottomBarPrefixes = setOf(
    Routes.LOGIN,
    Routes.FORGOT_PASSWORD,
    Routes.EXPIRED_LINK,
    Routes.USED_LINK,
    "reset_password",
    "change_password",
)


@Composable
fun AppNavigation(sessionManager: SessionManager, deepLinkIntent: Intent? = null) {
    val navController = rememberNavController()

    LaunchedEffect(deepLinkIntent) {
        deepLinkIntent?.data?.let { uri ->
            when (uri.scheme) {
                "siges" -> when (uri.host) {
                    "reset-password" -> {
                        val token = uri.getQueryParameter("token") ?: return@LaunchedEffect
                        navController.navigate(Routes.resetPassword(token))
                    }
                    "expired-link" -> navController.navigate(Routes.EXPIRED_LINK)
                    "used-link"    -> navController.navigate(Routes.USED_LINK)
                }
            }
        }
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isAdmin = sessionManager.role == "ADMIN"
    val showBottomBar = currentRoute != null &&
            noBottomBarPrefixes.none { currentRoute.startsWith(it) }

    val startDestination = when {
        sessionManager.isLoggedIn && isAdmin -> Routes.ADMIN_HOME
        sessionManager.isLoggedIn            -> Routes.HOME
        else                                 -> Routes.LOGIN
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                if (isAdmin) AdminBottomNavigationBar(navController, currentRoute)
                else ApplicantBottomNavigationBar(navController, currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding)
        ) {


            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel                  = hiltViewModel(),
                    onNavigateToHome           = {
                        val dest = if (sessionManager.role == "ADMIN") Routes.ADMIN_HOME else Routes.HOME
                        navController.navigate(dest) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
                )
            }

            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    viewModel      = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route     = Routes.RESET_PASSWORD,
                arguments = listOf(navArgument("token") { type = NavType.StringType })
            ) { backStack ->
                val token     = backStack.arguments?.getString("token") ?: ""
                val viewModel = hiltViewModel<ResetPasswordViewModel>()
                val state     by viewModel.uiState.collectAsState()

                LaunchedEffect(state.tokenError) {
                    when (state.tokenError) {
                        ResetPasswordError.EXPIRED_TOKEN ->
                            navController.navigate(Routes.EXPIRED_LINK) { popUpTo(Routes.FORGOT_PASSWORD) }
                        ResetPasswordError.ALREADY_USED_TOKEN ->
                            navController.navigate(Routes.USED_LINK) { popUpTo(Routes.FORGOT_PASSWORD) }
                        else -> Unit
                    }
                }

                ResetPasswordScreen(
                    token             = token,
                    viewModel         = viewModel,
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable(Routes.EXPIRED_LINK) {
                ExpiredLinkScreen(
                    onNavigateToForgotPassword = {
                        navController.navigate(Routes.FORGOT_PASSWORD) { popUpTo(Routes.LOGIN) }
                    }
                )
            }

            composable(Routes.USED_LINK) {
                UsedLinkScreen(
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable(Routes.HOME) {
                ApplicantHomeScreen(
                    viewModel                = hiltViewModel(),
                    onNavigateToAvailability = { navController.navigate(Routes.AVAILABILITY) },
                    onNavigateToNewRequest   = { navController.navigate(Routes.NEW_REQUEST) },
                    onNavigateToMyRequests   = { navController.navigate(Routes.MY_REQUESTS) },
                    onNavigateToDetail       = { id -> navController.navigate(Routes.requestDetail(id)) }
                )
            }

            composable(Routes.AVAILABILITY) {
                // AvailabilityScreen(viewModel = hiltViewModel())
                Text("Disponibilidad (en construcción)", modifier = Modifier.padding(24.dp))
            }

            composable(Routes.MY_REQUESTS) {
                // MyReservationsScreen(viewModel = hiltViewModel())
                Text("Mis Solicitudes (en construcción)", modifier = Modifier.padding(24.dp))
            }

            composable(Routes.NEW_REQUEST) {
                // CreateReservationScreen(viewModel = hiltViewModel())
                Text("Nueva Solicitud (en construcción)", modifier = Modifier.padding(24.dp))
            }

            composable(
                route     = Routes.REQUEST_DETAIL,
                arguments = listOf(navArgument("reservationId") { type = NavType.LongType })
            ) { backStack ->
                val reservationId = backStack.arguments?.getLong("reservationId") ?: return@composable
                val viewModel     = hiltViewModel<ReservationDetailViewModel>()
                LaunchedEffect(reservationId) { viewModel.loadReservation(reservationId) }
                // ReservationDetailScreen(viewModel = viewModel)
                Text("Detalle de Solicitud (en construcción)", modifier = Modifier.padding(24.dp))
            }

            composable(
                route     = Routes.EDIT_REQUEST,
                arguments = listOf(navArgument("reservationId") { type = NavType.LongType })
            ) { backStack ->
                val reservationId = backStack.arguments?.getLong("reservationId") ?: return@composable
                val viewModel     = hiltViewModel<EditReservationViewModel>()
                // EditReservationScreen(viewModel = viewModel)
                Text("Editar Solicitud (en construcción)", modifier = Modifier.padding(24.dp))
            }

            // ── Admin ─────────────────────────────────────────────────────────

            composable(Routes.ADMIN_HOME) {
                // AdminHomeScreen(viewModel = hiltViewModel())
                Text("Panel Admin (en construcción)", modifier = Modifier.padding(24.dp))
            }

            composable(Routes.ADMIN_ALL_REQUESTS) {
                // AdminReservationListScreen(viewModel = hiltViewModel())
                Text("Todas las Solicitudes (en construcción)", modifier = Modifier.padding(24.dp))
            }

            // ── Profile ───────────────────────────────────────────────────────

            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel                  = hiltViewModel(),
                    onNavigateToEditProfile    = { navController.navigate(Routes.EDIT_PROFILE) },
                    onNavigateToNotifications  = { navController.navigate(Routes.NOTIFICATION_PREFS) },
                    onNavigateToChangePassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                    onLogoutSuccess            = {
                        navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable(Routes.EDIT_PROFILE) {
                // EditProfileScreen(viewModel = hiltViewModel(), onNavigateBack = { navController.popBackStack() })
                Text("Editar Perfil (en construcción)", modifier = Modifier.padding(24.dp))
            }

            composable(Routes.NOTIFICATION_PREFS) {
                // NotificationPrefsScreen(viewModel = hiltViewModel(), onNavigateBack = { navController.popBackStack() })
                Text("Notificaciones (en construcción)", modifier = Modifier.padding(24.dp))
            }

            composable(
                route     = Routes.CHANGE_PASSWORD,
                arguments = listOf(navArgument("token") { type = NavType.StringType })
            ) { backStack ->
                val token = backStack.arguments?.getString("token") ?: ""
                // ChangePasswordScreen(token = token, viewModel = hiltViewModel())
                Text("Cambiar Contraseña (en construcción)", modifier = Modifier.padding(24.dp))
            }
        }
    }
}

// ─── Bottom Navigation Bars ───────────────────────────────────────────────────

@Composable
fun ApplicantBottomNavigationBar(navController: NavController, currentRoute: String?) {
    SigesBottomBar(
        navController, currentRoute,
        listOf(BottomNavItem.Home, BottomNavItem.Availability, BottomNavItem.Requests, BottomNavItem.Profile)
            .map { Triple(it.route, it.title, it.icon) }
    )
}

@Composable
fun AdminBottomNavigationBar(navController: NavController, currentRoute: String?) {
    SigesBottomBar(
        navController, currentRoute,
        listOf(AdminBottomNavItem.Home, AdminBottomNavItem.Requests, AdminBottomNavItem.Profile)
            .map { Triple(it.route, it.title, it.icon) }
    )
}

@Composable
private fun SigesBottomBar(
    navController: NavController,
    currentRoute: String?,
    items: List<Triple<String, String, ImageVector>>
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        items.forEach { (route, title, icon) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                icon     = { Icon(icon, contentDescription = title) },
                label    = { Text(title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                selected = isSelected,
                onClick  = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            navController.graph.startDestinationRoute?.let { popUpTo(it) { saveState = true } }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Plum,
                    selectedTextColor   = Plum,
                    indicatorColor      = Lav,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}