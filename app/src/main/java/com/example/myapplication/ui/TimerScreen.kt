package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.logic.TimerMode
import com.example.myapplication.logic.TimerState
import com.example.myapplication.logic.TimerViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun TimerScreen(
    viewModel: TimerViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TimerScreenWidget(
        state = state,
        timerCompleted = viewModel.timerCompleted,
        onChangeMode = viewModel::changeMode,
        onResetTimer = viewModel::resetTimer,
        onToggleTimer = viewModel::toggleTimer
    )
}

@Composable
internal fun TimerScreenWidget(
    state: TimerState,
    timerCompleted: Flow<Unit>,
    onChangeMode: (TimerMode) -> Unit,
    onResetTimer: () -> Unit,
    onToggleTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(timerCompleted) {
        timerCompleted.collect {
            snackbarHostState.showSnackbar("Time's up!")
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtoneUri?.let { uri ->
                MediaPlayer.create(context, uri).apply {
                    setOnCompletionListener { release() }
                    start()
                }
            }
        }
    }

    val remainingTime = state.timeRemainingSeconds
    val mode = state.mode
    val isRunning = state.isRunning
    val totalTime = mode.defaultDurationSeconds

    val progress = if (totalTime > 0) {
        remainingTime.toFloat() / totalTime.toFloat()
    } else {
        1f
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Mode Selection
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp)
                ) {
                    TimerMode.entries.forEach { timerMode ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = timerMode.ordinal,
                                count = TimerMode.entries.size
                            ),
                            onClick = { onChangeMode(timerMode) },
                            selected = mode == timerMode,
                            modifier = Modifier.testTag("ModeButton_${timerMode.name}")
                        ) {
                            Text(text = timerMode.name.replace("Break", " Break"))
                        }
                    }
                }

                // Timer Display
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(300.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationZ = 270f // Start from top
                            },
                        strokeWidth = 12.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = formatTime(remainingTime),
                        modifier = Modifier.testTag("TimerText"),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilledTonalButton(
                        onClick = onResetTimer,
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("ResetButton"),
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Reset",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    FloatingActionButton(
                        onClick = onToggleTimer,
                        modifier = Modifier
                            .size(100.dp)
                            .testTag("StartPauseButton"),
                        shape = MaterialTheme.shapes.large,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Start",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}
