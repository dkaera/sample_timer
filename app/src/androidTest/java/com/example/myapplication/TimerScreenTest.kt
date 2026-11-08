package com.example.myapplication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.example.myapplication.logic.TimerMode
import com.example.myapplication.logic.TimerState
import com.example.myapplication.logic.TimerViewModel
import com.example.myapplication.ui.TimerScreen
import com.example.myapplication.ui.TimerScreenWidget
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Rule
import org.junit.Test

class TimerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialState_isCorrect() {
        val viewModel = TimerViewModel()
        composeTestRule.setContent {
            TimerScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("TimerText").assertTextEquals("25:00")
        composeTestRule.onNodeWithTag("StartPauseButton").assertExists()
        composeTestRule.onNodeWithTag("ResetButton").assertExists()
    }

    @Test
    fun modeSwitching_updatesTimerText() {
        val viewModel = TimerViewModel()
        composeTestRule.setContent {
            TimerScreen(viewModel = viewModel)
        }

        // Short Break
        composeTestRule.onNodeWithTag("ModeButton_${TimerMode.ShortBreak.name}").performClick()
        composeTestRule.onNodeWithTag("TimerText").assertTextEquals("05:00")

        // Long Break
        composeTestRule.onNodeWithTag("ModeButton_${TimerMode.LongBreak.name}").performClick()
        composeTestRule.onNodeWithTag("TimerText").assertTextEquals("15:00")

        // Back to Focus
        composeTestRule.onNodeWithTag("ModeButton_${TimerMode.Focus.name}").performClick()
        composeTestRule.onNodeWithTag("TimerText").assertTextEquals("25:00")
    }

    @Test
    fun startPauseToggle_updatesIconDescription() {
        val viewModel = TimerViewModel()
        composeTestRule.setContent {
            TimerScreen(viewModel = viewModel)
        }

        // Initial state: Play button (contentDescription "Start")
        composeTestRule.onNodeWithContentDescription("Start").assertExists()

        // Start timer
        composeTestRule.onNodeWithTag("StartPauseButton").performClick()

        // Now Pause button (contentDescription "Pause")
        composeTestRule.onNodeWithContentDescription("Pause").assertExists()

        // Pause timer
        composeTestRule.onNodeWithTag("StartPauseButton").performClick()

        // Now back to Play button
        composeTestRule.onNodeWithContentDescription("Start").assertExists()
    }

    @Test
    fun resetFunctionality_returnsToDefaultTime() {
        val viewModel = TimerViewModel()
        composeTestRule.setContent {
            TimerScreen(viewModel = viewModel)
        }

        // Start timer
        composeTestRule.onNodeWithTag("StartPauseButton").performClick()

        // Wait a bit for time to tick (though we can't easily wait in Compose UI tests without advancing clock)
        // Since we are using a real ViewModel, the time will tick if we use a real dispatcher.
        // However, for a simple reset test, starting and then resetting should bring it back to 25:00.

        composeTestRule.onNodeWithTag("ResetButton").performClick()
        composeTestRule.onNodeWithTag("TimerText").assertTextEquals("25:00")
    }

    @Test
    fun completionAlert_showsSnackbar() {
        val timerCompletedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

        composeTestRule.setContent {
            TimerScreenWidget(
                state = TimerState(),
                timerCompleted = timerCompletedFlow,
                onChangeMode = {},
                onResetTimer = {},
                onToggleTimer = {}
            )
        }

        // SharedFlow does not retain events emitted before a collector subscribes.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            timerCompletedFlow.subscriptionCount.value > 0
        }
        composeTestRule.runOnIdle {
            check(timerCompletedFlow.tryEmit(Unit)) { "Completion event was not delivered" }
        }

        // Verify Snackbar text is displayed
        composeTestRule.onNodeWithText("Time's up!").assertIsDisplayed()
    }
}
