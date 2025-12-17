package com.example.hometasker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.hometasker.MainActivity
import com.example.hometasker.R
import com.example.hometasker.domain.model.TrackingStatus
import com.example.hometasker.domain.usecase.tracking.GetActiveSessionUseCase
import com.example.hometasker.domain.usecase.tracking.PauseTrackingUseCase
import com.example.hometasker.domain.usecase.tracking.ResumeTrackingUseCase
import com.example.hometasker.domain.usecase.tracking.StopTrackingUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var getActiveSessionUseCase: GetActiveSessionUseCase

    @Inject
    lateinit var pauseTrackingUseCase: PauseTrackingUseCase

    @Inject
    lateinit var resumeTrackingUseCase: ResumeTrackingUseCase

    @Inject
    lateinit var stopTrackingUseCase: StopTrackingUseCase

    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private var currentTaskId: Long? = null
    private var currentTaskTitle: String = ""

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
                val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: ""
                if (taskId != -1L) {
                    startTimer(taskId, taskTitle)
                }
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_STICKY
    }

    private fun startTimer(taskId: Long, taskTitle: String) {
        currentTaskId = taskId
        currentTaskTitle = taskTitle
        _isRunning.value = true

        startForeground(NOTIFICATION_ID, createNotification())
        startTimerUpdates()
    }

    private fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        updateNotification()

        serviceScope.launch {
            currentTaskId?.let { pauseTrackingUseCase(it) }
        }
    }

    private fun resumeTimer() {
        _isRunning.value = true
        startTimerUpdates()
        updateNotification()

        serviceScope.launch {
            currentTaskId?.let { resumeTrackingUseCase(it) }
        }
    }

    private fun stopTimer() {
        _isRunning.value = false
        timerJob?.cancel()
        _elapsedSeconds.value = 0L

        serviceScope.launch {
            currentTaskId?.let { stopTrackingUseCase(it) }
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startTimerUpdates() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (_isRunning.value) {
                delay(1000)
                _elapsedSeconds.value++
                updateNotification()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows timer progress for task tracking"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseResumeIntent = Intent(this, TimerService::class.java).apply {
            action = if (_isRunning.value) ACTION_PAUSE else ACTION_RESUME
        }
        val pauseResumePendingIntent = PendingIntent.getService(
            this, 1, pauseResumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, TimerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 2, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentTaskTitle)
            .setContentText(formatTime(_elapsedSeconds.value))
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(
                if (_isRunning.value) R.drawable.ic_pause else R.drawable.ic_play,
                if (_isRunning.value) "Pause" else "Resume",
                pauseResumePendingIntent
            )
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
    }

    companion object {
        const val CHANNEL_ID = "timer_service_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.example.hometasker.action.START"
        const val ACTION_PAUSE = "com.example.hometasker.action.PAUSE"
        const val ACTION_RESUME = "com.example.hometasker.action.RESUME"
        const val ACTION_STOP = "com.example.hometasker.action.STOP"

        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"

        fun startTimer(context: Context, taskId: Long, taskTitle: String) {
            val intent = Intent(context, TimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_TASK_ID, taskId)
                putExtra(EXTRA_TASK_TITLE, taskTitle)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
