package com.example.myapplication.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State for the TimerViewModel, containing the current remaining time,
 * the current mode of the timer, and whether the timer is currently running.
 */
data class TimerState(
    val timeRemainingSeconds: Int = TimerMode.Focus.defaultDurationSeconds,
    val mode: TimerMode = TimerMode.Focus,
    val isRunning: Boolean = false
)

class TimerViewModel : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private val _timerCompleted = MutableSharedFlow<Unit>()
    val timerCompleted = _timerCompleted.asSharedFlow()

    private var timerJob: Job? = null

    /**
     * Toggles the timer between running and paused states.
     */
    fun toggleTimer() {
        if (_state.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    /**
     * Resets the timer to the default duration for the current mode.
     */
    fun resetTimer() {
        pauseTimer()
        _state.update { it.copy(timeRemainingSeconds = it.mode.defaultDurationSeconds) }
    }

    /**
     * Changes the timer mode and resets the remaining time to that mode's default.
     */
    fun changeMode(mode: TimerMode) {
        pauseTimer()
        _state.update { it.copy(
            mode = mode,
            timeRemainingSeconds = mode.defaultDurationSeconds
        ) }
    }

    private fun startTimer() {
        _state.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (_state.value.isRunning && _state.value.timeRemainingSeconds > 0) {
                delay(1000)
                _state.update { it.copy(timeRemainingSeconds = it.timeRemainingSeconds - 1) }
            }

            if (_state.value.timeRemainingSeconds <= 0) {
                pauseTimer()
                _timerCompleted.emit(Unit)
            }
        }
    }

    private fun pauseTimer() {
        _state.update { it.copy(isRunning = false) }
        timerJob?.cancel()
        timerJob = null
    }
}
