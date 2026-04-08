package dev.spiffocode.sigesmobile.ui.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import androidx.navigation.navDeepLink
import dev.spiffocode.sigesmobile.data.local.SessionManager
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.ui.screens.admin.AdminHomeScreen
import dev.spiffocode.sigesmobile.ui.screens.admin.AdminReservationListScreen
import dev.spiffocode.sigesmobile.ui.screens.admin.AdminReviewDetailScreen
import dev.spiffocode.sigesmobile.ui.screens.applicant.ApplicantHomeScreen
import dev.spiffocode.sigesmobile.ui.screens.applicant.RescheduleScreen
import dev.spiffocode.sigesmobile.ui.screens.applicant.ReservationDetailScreen
import dev.spiffocode.sigesmobile.ui.screens.applicant.ResourceCalendarScreen
import dev.spiffocode.sigesmobile.ui.screens.login.LoginScreen
import dev.spiffocode.sigesmobile.ui.screens.passwordRecovery.ExpiredLinkScreen
import dev.spiffocode.sigesmobile.ui.screens.passwordRecovery.ForgotPasswordScreen
import dev.spiffocode.sigesmobile.ui.screens.passwordRecovery.ResetPasswordScreen
import dev.spiffocode.sigesmobile.ui.screens.passwordRecovery.UsedLinkScreen
import dev.spiffocode.sigesmobile.ui.screens.profile.ChangePasswordScreen
import dev.spiffocode.sigesmobile.ui.screens.profile.EditProfileScreen
import dev.spiffocode.sigesmobile.ui.screens.profile.NotificationPrefsScreen
import dev.spiffocode.sigesmobile.ui.screens.profile.ProfileScreen
import dev.spiffocode.sigesmobile.viewmodel.ExternalNavigationEvent
import dev.spiffocode.sigesmobile.viewmodel.MainViewModel
import dev.spiffocode.sigesmobile.viewmodel.ResetPasswordError
import dev.spiffocode.sigesmobile.viewmodel.ResetPasswordViewModel


object Routes {
    const val LOGIN            = "login"
    const val FORGOT_PASSWORD  = "forgot_password"
    const val RESET_PASSWORD   = "reset_password/{token}/{email}"
    const val EXPIRED_LINK     = "expired_link"
    const val USED_LINK        = "used_link"
    fun resetPassword(token: String, email: String) = "reset_password/$token/$email"

    const val HOME           = "home"
    const val AVAILABILITY   = "availability?showBackButton={showBackButton}"
    fun availability(showBack: Boolean) = "availability?showBackButton=$showBack"
    const val MY_REQUESTS    = "requests?showBackButton={showBackButton}"
    fun myRequests(showBack: Boolean) = "requests?showBackButton=$showBack"
    const val NEW_REQUEST    = "new_request?reservableId={reservableId}&type={type}&name={name}&date={date}&startTime={startTime}&endTime={endTime}"
    fun newRequest() = "new_request?reservableId=&type=&name=&date=&startTime=&endTime="
    fun newRequestPrefilled(
        reservableId: Long, type: String, name: String, date: String, startTime: String, endTime: String
    ) = "new_request?reservableId=$reservableId&type=$type&name=${android.net.Uri.encode(name)}&date=$date&startTime=$startTime&endTime=$endTime"
    const val RESOURCE_CALENDAR = "resource_calendar/{reservableId}/{type}/{name}"
    fun resourceCalendar(reservableId: Long, type: String, name: String) =
        "resource_calendar/$reservableId/$type/${android.net.Uri.encode(name)}"
    const val SPACE_DETAIL   = "space_detail/{id}"
    const val EQUIPMENT_DETAIL = "equipment_detail/{id}"
    fun spaceDetail(id: Long) = "space_detail/$id"
    fun equipmentDetail(id: Long) = "equipment_detail/$id"
    const val REQUEST_DETAIL = "request_detail/{reservationId}"
    const val RESCHEDULE_REQUEST = "reschedule_request/{reservationId}"
    fun requestDetail(id: Long) = "request_detail/$id"
    fun rescheduleRequest(id: Long) = "reschedule_request/$id"

    const val ADMIN_HOME           = "admin_home"
    const val ADMIN_ALL_REQUESTS   = "admin_all_requests"
    const val ADMIN_REVIEW_DETAIL  = "admin_review/{reservationId}"
    fun adminReviewDetail(id: Long) = "admin_review/$id"

    const val PROFILE            = "profile"
    const val EDIT_PROFILE       = "edit_profile"
    const val NOTIFICATION_PREFS = "notification_prefs"
    const val CHANGE_PASSWORD    = "change_password"
}


sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home         : BottomNavItem(Routes.HOME, "Inicio", Icons.Default.Home)
    object Availability : BottomNavItem(Routes.availability(false), "Buscar", Icons.Default.Search)
    object Requests     : BottomNavItem(Routes.myRequests(false), "Solicitudes", Icons.Default.List)
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
    "admin_review",
)


@Composable
fun AppNavigation(
    sessionManager: SessionManager,
    windowSizeClass: WindowSizeClass,
    navController: NavController = rememberNavController()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
 
    // ── Double Back to Exit ──────────────────────────────────────────────────
    val context = LocalContext.current
    var lastBackPressTime by remember { mutableStateOf(0L) }
    
    val isAdmin = sessionManager.role == "ADMIN"
    val startDestination = when {
        sessionManager.isLoggedIn && !sessionManager.rememberMe -> Routes.LOGIN
        sessionManager.isLoggedIn && isAdmin -> Routes.ADMIN_HOME
        sessionManager.isLoggedIn            -> Routes.HOME
        else                                 -> Routes.LOGIN
    }

    val isStartDestination = currentRoute?.substringBefore("?") == startDestination.substringBefore("?")
    BackHandler(enabled = isStartDestination) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < 2000) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(context, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show()
        }
    }
 
    val mainViewModel: MainViewModel = hiltViewModel()
    val accessToken by sessionManager.accessTokenFlow.collectAsState(initial = sessionManager.accessToken)

    val guestRoutes = setOf(
        Routes.LOGIN,
        Routes.FORGOT_PASSWORD,
        Routes.EXPIRED_LINK,
        Routes.USED_LINK,
        "reset_password"
    )

    LaunchedEffect(accessToken) {
        if (accessToken == null) {
            val isGuestRoute = currentRoute != null && guestRoutes.any { currentRoute.startsWith(it) }
            if (!isGuestRoute && currentRoute != null) {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (sessionManager.isLoggedIn && !sessionManager.rememberMe) {
            sessionManager.clearSession()
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.navigationEvent.collect { event ->
            when (event) {
                is ExternalNavigationEvent.ReservationDetail -> {
                    val route = if (event.isAdmin) Routes.adminReviewDetail(event.id)
                                else Routes.requestDetail(event.id)
                    navController.navigate(route)
                }
            }
        }
    }
    val showBottomBar = currentRoute != null &&
            noBottomBarPrefixes.none { currentRoute.startsWith(it) }

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
                    onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                    windowSizeClass            = windowSizeClass
                )
            }

            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    windowSizeClass = windowSizeClass,
                    viewModel      = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.RESET_PASSWORD,
                arguments = listOf(
                    navArgument("token") { type = NavType.StringType },
                    navArgument("email") {type = NavType.StringType}
                ),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "https://siges.lat/app/reset-password?token={token}&email={email}"
                    }
                )
            ) { backStack ->
                val token     = backStack.arguments?.getString("token") ?: ""
                val email = backStack.arguments?.getString("email") ?: ""
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
                    windowSizeClass   = windowSizeClass,
                    token             = token,
                    emailFromLink     = email,
                    viewModel         = viewModel,
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable(
                route = Routes.EXPIRED_LINK,
                deepLinks = listOf(
                    navDeepLink { uriPattern = "https://siges.lat/app/expired-link" }
                )
            ) {
                ExpiredLinkScreen(
                    windowSizeClass = windowSizeClass,
                    onNavigateToForgotPassword = {
                        navController.navigate(Routes.FORGOT_PASSWORD) { popUpTo(Routes.LOGIN) }
                    }
                )
            }

            composable(
                route = Routes.USED_LINK,
                deepLinks = listOf(
                    navDeepLink { uriPattern = "https://siges.lat/app/used-link" }
                )
            ) {
                UsedLinkScreen(
                    windowSizeClass = windowSizeClass,
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable(Routes.HOME) {
                ApplicantHomeScreen(
                    windowSizeClass = windowSizeClass,
                    onNavigateToAvailability = { navController.navigate(Routes.AVAILABILITY) },
                    onNavigateToNewRequest = { navController.navigate(Routes.NEW_REQUEST) },
                    onNavigateToMyRequests = { navController.navigate(Routes.MY_REQUESTS) },
                    onNavigateToReservationDetail = { id -> 
                        navController.navigate(Routes.requestDetail(id)) 
                    },
                    onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                    onNavigateToResourceDetail = { id, type ->
                        when(type){
                            ReservableType.SPACE -> navController.navigate(Routes.spaceDetail(id))
                            ReservableType.EQUIPMENT -> navController.navigate(Routes.equipmentDetail(id))
                        }
                    }
                )
            }

            composable(
                route = Routes.AVAILABILITY,
                arguments = listOf(navArgument("showBackButton") { type = NavType.BoolType; defaultValue = false })
            ) { backStack ->
                val showBack = backStack.arguments?.getBoolean("showBackButton") ?: false
                dev.spiffocode.sigesmobile.ui.screens.applicant.AvailabilityScreen(
                    windowSizeClass = windowSizeClass,
                    showBackButton = showBack,
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSpaceDetail = { id -> navController.navigate(Routes.spaceDetail(id)) },
                    onNavigateToEquipmentDetail = { id -> navController.navigate(Routes.equipmentDetail(id)) }
                )
            }

            composable(
                route = Routes.MY_REQUESTS,
                arguments = listOf(navArgument("showBackButton") { type = NavType.BoolType; defaultValue = false })
            ) { backStack ->
                val showBack = backStack.arguments?.getBoolean("showBackButton") ?: false
                dev.spiffocode.sigesmobile.ui.screens.applicant.MyReservationsScreen(
                    windowSizeClass = windowSizeClass,
                    showBackButton = showBack,
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNewRequest = { navController.navigate(Routes.newRequest()) },
                    onNavigateToDetail = { id -> navController.navigate(Routes.requestDetail(id)) }
                )
            }

            composable(
                route     = Routes.NEW_REQUEST,
                arguments = listOf(
                    navArgument("reservableId") { type = NavType.StringType; defaultValue = "" },
                    navArgument("type")         { type = NavType.StringType; defaultValue = "" },
                    navArgument("name")         { type = NavType.StringType; defaultValue = "" },
                    navArgument("date")         { type = NavType.StringType; defaultValue = "" },
                    navArgument("startTime")    { type = NavType.StringType; defaultValue = "" },
                    navArgument("endTime")      { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStack ->
                val prefillResourceId = backStack.arguments?.getString("reservableId") ?: ""
                val prefillType       = backStack.arguments?.getString("type")         ?: ""
                val prefillDate      = backStack.arguments?.getString("date")      ?: ""
                val prefillStartTime = backStack.arguments?.getString("startTime")  ?: ""
                val prefillEndTime   = backStack.arguments?.getString("endTime")    ?: ""
                dev.spiffocode.sigesmobile.ui.screens.applicant.NewRequestScreen(
                    windowSizeClass  = windowSizeClass,
                    viewModel        = hiltViewModel(),
                    onNavigateBack   = { navController.popBackStack() },
                    onNavigateToDetail = { id -> navController.navigate(Routes.requestDetail(id)) },
                    prefillResourceId = prefillResourceId,
                    prefillType       = prefillType,
                    prefillDate      = prefillDate,
                    prefillStartTime = prefillStartTime,
                    prefillEndTime   = prefillEndTime
                )
            }

            composable(
                route     = Routes.RESOURCE_CALENDAR,
                arguments = listOf(
                    navArgument("reservableId") { type = NavType.LongType },
                    navArgument("type")         { type = NavType.StringType },
                    navArgument("name")         { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStack ->
                val reservableId   = backStack.arguments?.getLong("reservableId") ?: return@composable
                val type           = backStack.arguments?.getString("type") ?: "SPACE"
                val reservableName = backStack.arguments?.getString("name") ?: ""
                ResourceCalendarScreen(
                    reservableId   = reservableId,
                    type           = type,
                    reservableName = android.net.Uri.decode(reservableName),
                    viewModel      = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNewRequest = { id, date, start, end ->
                        navController.navigate(Routes.newRequestPrefilled(id, type, android.net.Uri.decode(reservableName), date, start, end))
                    }
                )
            }

            composable(
                route     = Routes.SPACE_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStack ->
                val spaceId = backStack.arguments?.getLong("id") ?: return@composable
                dev.spiffocode.sigesmobile.ui.screens.applicant.SpaceDetailScreen(
                    windowSizeClass      = windowSizeClass,
                    spaceId              = spaceId,
                    onNavigateBack       = { navController.popBackStack() },
                    onNavigateToReserve  = { id, name -> navController.navigate(Routes.newRequestPrefilled(id, "SPACE", name, "", "", "")) },
                    onNavigateToCalendar = { id, name -> navController.navigate(Routes.resourceCalendar(id, "SPACE", name)) }
                )
            }

            composable(
                route     = Routes.EQUIPMENT_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStack ->
                val equipmentId   = backStack.arguments?.getLong("id") ?: return@composable
                dev.spiffocode.sigesmobile.ui.screens.applicant.EquipmentDetailScreen(
                    windowSizeClass      = windowSizeClass,
                    equipmentId          = equipmentId,
                    onNavigateBack       = { navController.popBackStack() },
                    onNavigateToReserve  = { id, name -> navController.navigate(Routes.newRequestPrefilled(id, "EQUIPMENT", name, "", "", "")) },
                    onNavigateToCalendar = { id, name -> navController.navigate(Routes.resourceCalendar(id, "EQUIPMENT", name)) }
                )
            }

            composable(
                route     = Routes.REQUEST_DETAIL,
                arguments = listOf(navArgument("reservationId") { type = NavType.LongType })
            ) { backStack ->
                val reservationId = backStack.arguments?.getLong("reservationId") ?: return@composable
                ReservationDetailScreen(
                    windowSizeClass = windowSizeClass,
                    reservationId = reservationId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id -> navController.navigate(Routes.rescheduleRequest(id)) }
                )
            }

            composable(
                route     = Routes.RESCHEDULE_REQUEST,
                arguments = listOf(navArgument("reservationId") { type = NavType.LongType })
            ) { backStack ->
                val reservationId = backStack.arguments?.getLong("reservationId") ?: return@composable
                RescheduleScreen(
                    windowSizeClass = windowSizeClass,
                    reservationId = reservationId,
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            // ── Admin ─────────────────────────────────────────────────────────

            composable(Routes.ADMIN_HOME) {
                AdminHomeScreen(
                    windowSizeClass = windowSizeClass,
                    onNavigateToAllRequests = { navController.navigate(Routes.ADMIN_ALL_REQUESTS) },
                    onNavigateToDetail = { id -> 
                        navController.navigate(Routes.adminReviewDetail(id)) 
                    },
                    onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
                )
            }

            composable(Routes.ADMIN_ALL_REQUESTS) {
                AdminReservationListScreen(
                    windowSizeClass = windowSizeClass,
                    onNavigateToDetail = { id -> navController.navigate(Routes.adminReviewDetail(id)) }
                )
            }

            composable(
                route     = Routes.ADMIN_REVIEW_DETAIL,
                arguments = listOf(navArgument("reservationId") { type = NavType.LongType })
            ) { backStack ->
                val reservationId = backStack.arguments?.getLong("reservationId") ?: return@composable
                AdminReviewDetailScreen(
                    windowSizeClass = windowSizeClass,
                    reservationId  = reservationId,
                    viewModel      = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ── Profile ───────────────────────────────────────────────────────

            composable(Routes.PROFILE) {
                ProfileScreen(
                    windowSizeClass            = windowSizeClass,
                    viewModel                  = hiltViewModel(),
                    onNavigateToEditProfile    = { navController.navigate(Routes.EDIT_PROFILE) },
                    onNavigateToNotifications  = { navController.navigate(Routes.NOTIFICATION_PREFS) },
                    onNavigateToChangePassword = { navController.navigate(Routes.CHANGE_PASSWORD) },
                    onLogoutSuccess            = {
                        navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable(Routes.EDIT_PROFILE) {
                 EditProfileScreen(
                     windowSizeClass = windowSizeClass,
                     viewModel = hiltViewModel(),
                     onNavigateBack = { navController.popBackStack() }
                 )
            }

            composable(Routes.NOTIFICATION_PREFS) {
                 NotificationPrefsScreen(
                     windowSizeClass = windowSizeClass,
                     viewModel = hiltViewModel(),
                     onNavigateBack = { navController.popBackStack() }
                 )
            }

            composable(Routes.CHANGE_PASSWORD) {
                ChangePasswordScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
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
    NavigationBar(containerColor = MaterialTheme.colorScheme.background, tonalElevation = 8.dp) {
        items.forEach { (route, title, icon) ->
            val isSelected = currentRoute?.substringBefore("?") == route.substringBefore("?")
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
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    indicatorColor      = MaterialTheme.colorScheme.surface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}