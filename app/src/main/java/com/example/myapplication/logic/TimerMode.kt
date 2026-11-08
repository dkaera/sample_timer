package com.example.myapplication.logic

/**
 * Represents the different modes of the Pomodoro timer.
 * Each mode defines its default duration in seconds.
 */
enum class TimerMode(val defaultDurationSeconds: Int) {
    Focus(25 * 60),
    ShortBreak(5 * 60),
    LongBreak(15 * 60)
}
