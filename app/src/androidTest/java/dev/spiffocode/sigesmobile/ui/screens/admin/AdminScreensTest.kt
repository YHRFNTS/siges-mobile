package dev.spiffocode.sigesmobile.ui.screens.admin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.spiffocode.sigesmobile.data.remote.dto.NoteItem
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableDto
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationResponse
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationType
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import dev.spiffocode.sigesmobile.viewmodel.AdminReservationListUiState
import dev.spiffocode.sigesmobile.viewmodel.AdminReservationTab
import dev.spiffocode.sigesmobile.viewmodel.AdminReviewUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── AdminReservationListScreen ─────────────────────────────────────────────

    @Test
    fun adminReservationListScreen_showsTabs() {
        composeTestRule.setContent {
            SigesmobileTheme {
                AdminReservationListScreen(state = AdminReservationListUiState())
            }
        }

        composeTestRule.onNodeWithText("Todas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pendientes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Resueltas").assertIsDisplayed()
    }

    @Test
    fun adminReservationListScreen_showsEmptyState_whenNoReservations() {
        composeTestRule.setContent {
            SigesmobileTheme {
                AdminReservationListScreen(state = AdminReservationListUiState(isLoading = false))
            }
        }

        composeTestRule.onNodeWithText("No se encontraron solicitudes").assertIsDisplayed()
    }

    @Test
    fun adminReservationListScreen_showsRequestCard() {
        val state = AdminReservationListUiState(
            isLoading = false,
            reservations = listOf(
                ReservationResponse(
                    id        = 1,
                    reservable = ReservableDto(
                        id = 1,
                        name = "Sala de Juntas A",
                        reservableType = ReservableType.SPACE,
                        status = ReservableStatus.AVAILABLE,
                        availableForStudents = true
                    ),
                    date      = LocalDate(2026, 1, 28).toJavaLocalDate(),
                    startTime = kotlinx.datetime.LocalTime(10, 0).toJavaLocalTime(),
                    endTime   = kotlinx.datetime.LocalTime(12, 0).toJavaLocalTime(),
                    status    = ReservationStatus.PENDING,
                    type      = ReservationType.GROUP
                )
            )
        )

        composeTestRule.setContent {
            SigesmobileTheme {
                AdminReservationListScreen(state = state)
            }
        }

        composeTestRule.onNodeWithText("Sala de Juntas A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pendiente").assertIsDisplayed()
    }

    // ── AdminReviewDetailScreen ────────────────────────────────────────────────

    @Test
    fun adminReviewDetailScreen_showsApproveAndDenyButtons_whenPending() {
        val state = AdminReviewUiState(
            reservation = ReservationResponse(
                id        = 1,
                status    = ReservationStatus.PENDING,
                date      = LocalDate(2026, 1, 28).toJavaLocalDate(),
                startTime = kotlinx.datetime.LocalTime(10, 0).toJavaLocalTime(),
                endTime   = kotlinx.datetime.LocalTime(12, 0).toJavaLocalTime(),
                type      = ReservationType.GROUP,
                reservable = ReservableDto(
                    id = 1,
                    name = "Sala de Juntas A",
                    reservableType = ReservableType.SPACE,
                    status = ReservableStatus.AVAILABLE,
                    availableForStudents = true
                ),
                notes = listOf(NoteItem(1, "Propósito de la reunión.", null, null, null))
            )
        )

        composeTestRule.setContent {
            SigesmobileTheme {
                AdminReviewDetailScreenContent(state = state)
            }
        }

        composeTestRule.onNodeWithText("Sala de Juntas A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Denegar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aprobar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Denegar").assertIsEnabled()
        composeTestRule.onNodeWithText("Aprobar").assertIsEnabled()
    }

    @Test
    fun adminReviewDetailScreen_hideButtons_whenApproved() {
        val state = AdminReviewUiState(
            reservation = ReservationResponse(
                id        = 2,
                status    = ReservationStatus.APPROVED,
                date      = LocalDate(2026, 1, 28).toJavaLocalDate(),
                startTime = kotlinx.datetime.LocalTime(10, 0).toJavaLocalTime(),
                endTime   = kotlinx.datetime.LocalTime(12, 0).toJavaLocalTime(),
                type      = ReservationType.SINGLE,
                reservable = ReservableDto(
                    id = 1,
                    name = "Sala Biblioteca",
                    reservableType = ReservableType.SPACE,
                    status = ReservableStatus.AVAILABLE,
                    availableForStudents = true
                )
            )
        )

        composeTestRule.setContent {
            SigesmobileTheme {
                AdminReviewDetailScreenContent(state = state)
            }
        }

        composeTestRule.onNodeWithText("Sala Biblioteca").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aprobada").assertIsDisplayed()
        // Action buttons should NOT appear for resolved reservations
        composeTestRule.onNodeWithText("Aprobar").assertDoesNotExist()
        composeTestRule.onNodeWithText("Denegar").assertDoesNotExist()
    }

    @Test
    fun adminReviewDetailScreen_showsObservationField_whenPending() {
        val state = AdminReviewUiState(
            observation = "Traiga su credencial.",
            reservation = ReservationResponse(
                id        = 3,
                status    = ReservationStatus.PENDING,
                date      = LocalDate(2026, 2, 5).toJavaLocalDate(),
                startTime = kotlinx.datetime.LocalTime(9, 0).toJavaLocalTime(),
                endTime   = kotlinx.datetime.LocalTime(11, 0).toJavaLocalTime(),
                type      = ReservationType.GROUP,
                reservable = ReservableDto(
                    id = 2,
                    name = "Sala Innovación",
                    reservableType = ReservableType.SPACE,
                    status = ReservableStatus.AVAILABLE,
                    availableForStudents = true
                )
            )
        )

        composeTestRule.setContent {
            SigesmobileTheme {
                AdminReviewDetailScreenContent(state = state)
            }
        }

        composeTestRule.onNodeWithText("Traiga su credencial.").assertIsDisplayed()
    }
}
