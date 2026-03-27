package dev.spiffocode.sigesmobile.ui.components.homescreen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableStatus
import dev.spiffocode.sigesmobile.data.remote.dto.ReservableType
import dev.spiffocode.sigesmobile.data.remote.dto.ReservationStatus
import dev.spiffocode.sigesmobile.ui.theme.SigesmobileTheme
import kotlinx.datetime.LocalDateTime
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComponentTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun availableItemCard_displaysCorrectly() {
        composeTestRule.setContent {
            SigesmobileTheme {
                AvailableItemCard(
                    title = "Lab de Cómputo 2",
                    meta = "Capacidad para 30 personas",
                    status = ReservableStatus.AVAILABLE,
                    resourceCategory = "Aulas",
                    resourceType = ReservableType.SPACE
                )
            }
        }

        composeTestRule.onNodeWithText("Lab de Cómputo 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aulas - Capacidad para 30 personas").assertIsDisplayed()
        composeTestRule.onNodeWithText("Disponible").assertIsDisplayed() // Status translation should be "Disponible"
    }

    @Test
    fun requestCard_displaysCorrectly() {
        val start = LocalDateTime(2026, 1, 28, 10, 0)
        val end = LocalDateTime(2026, 1, 28, 12, 0)

        composeTestRule.setContent {
            SigesmobileTheme {
                RequestCard(
                    title = "Sala de Juntas A",
                    startDateTime = start,
                    endDateTime = end,
                    status = ReservationStatus.PENDING,
                    meta1 = "Espacio",
                    meta2 = "2 horas"
                )
            }
        }

        composeTestRule.onNodeWithText("Sala de Juntas A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pendiente").assertIsDisplayed() // Status translation
        composeTestRule.onNodeWithText("28 Ene · 10:00 - 12:00").assertIsDisplayed() // ToCardDateString translation
        composeTestRule.onNodeWithText("Espacio").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 horas").assertIsDisplayed()
    }
}
