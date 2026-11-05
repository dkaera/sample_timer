package com.example.myapplication.logic

import app.cash.turbine.test
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    private lateinit var viewModel: TimerViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TimerViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Focus mode with correct duration`() {
        val state = viewModel.state.value
        assertEquals(TimerMode.Focus, state.mode)
        assertEquals(TimerMode.Focus.defaultDurationSeconds, state.timeRemainingSeconds)
        assertEquals(false, state.isRunning)
    }

    @Test
    fun `toggleTimer starts timer and updates state`() {
        viewModel.toggleTimer()
        assertEquals(true, viewModel.state.value.isRunning)
    }

    @Test
    fun `toggleTimer pauses running timer`() {
        viewModel.toggleTimer() // start
        viewModel.toggleTimer() // pause
        assertEquals(false, viewModel.state.value.isRunning)
    }

    @Test
    fun `timer decrements correctly when running`() = runTest {
        viewModel.toggleTimer()

        advanceTimeBy(1000)
        runCurrent()

        val expectedTime = TimerMode.Focus.defaultDurationSeconds - 1
        assertEquals(expectedTime, viewModel.state.value.timeRemainingSeconds)
    }

    @Test
    fun `timer pauses correctly and stops decrementing`() = runTest {
        viewModel.toggleTimer()

        advanceTimeBy(1000)
        runCurrent()
        val timeAfterOneSecond = viewModel.state.value.timeRemainingSeconds

        viewModel.toggleTimer() // pause

        advanceTimeBy(2000)
        runCurrent()

        assertEquals(timeAfterOneSecond, viewModel.state.value.timeRemainingSeconds)
    }

    @Test
    fun `timer resumes correctly and continues decrementing`() = runTest {
        viewModel.toggleTimer()
        advanceTimeBy(1000)
        runCurrent()

        viewModel.toggleTimer() // pause
        advanceTimeBy(1000)
        runCurrent()
        val pausedTime = viewModel.state.value.timeRemainingSeconds

        viewModel.toggleTimer() // resume
        advanceTimeBy(1000)
        runCurrent()

        assertEquals(pausedTime - 1, viewModel.state.value.timeRemainingSeconds)
    }

    @Test
    fun `resetTimer resets to correct duration for current mode`() = runTest {
        viewModel.toggleTimer()
        advanceTimeBy(5000)
        runCurrent()

        viewModel.resetTimer()

        assertEquals(TimerMode.Focus.defaultDurationSeconds, viewModel.state.value.timeRemainingSeconds)
        assertEquals(false, viewModel.state.value.isRunning)
    }

    @Test
    fun `changeMode updates mode and resets time`() {
        viewModel.changeMode(TimerMode.ShortBreak)

        assertEquals(TimerMode.ShortBreak, viewModel.state.value.mode)
        assertEquals(TimerMode.ShortBreak.defaultDurationSeconds, viewModel.state.value.timeRemainingSeconds)
        assertEquals(false, viewModel.state.value.isRunning)
    }

    @Test
    fun `timer completion event is emitted when hits zero`() = runTest {
        viewModel.changeMode(TimerMode.ShortBreak)
        viewModel.toggleTimer()
        
        val duration = TimerMode.ShortBreak.defaultDurationSeconds
        
        viewModel.timerCompleted.test {
            advanceTimeBy(duration * 1000L)
            advanceUntilIdle()
            
            assertEquals(Unit, awaitItem())
            assertEquals(0, viewModel.state.value.timeRemainingSeconds)
            assertEquals(false, viewModel.state.value.isRunning)
        }
    }
}
