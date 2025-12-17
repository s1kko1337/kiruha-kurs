package com.example.hometasker.util

object Constants {
    const val DATABASE_NAME = "hometasker_database"
    const val DATABASE_VERSION = 1

    const val PREFERENCES_NAME = "hometasker_preferences"

    const val DEFAULT_REMINDER_MINUTES_BEFORE = 30

    const val MAX_TASK_TITLE_LENGTH = 200
    const val MAX_TASK_DESCRIPTION_LENGTH = 2000
    const val MAX_CATEGORY_NAME_LENGTH = 50

    const val ANIMATION_DURATION_SHORT = 150
    const val ANIMATION_DURATION_MEDIUM = 300
    const val ANIMATION_DURATION_LONG = 500

    const val DEBOUNCE_DELAY_MS = 300L
    const val SEARCH_DEBOUNCE_MS = 500L

    object DeepLinks {
        const val SCHEME = "hometasker"
        const val HOST = "app"
        const val TASK_PATH = "task"
        const val CATEGORY_PATH = "category"
    }

    object WorkManager {
        const val WIDGET_UPDATE_WORK = "widget_update_work"
        const val DAILY_SUMMARY_WORK = "daily_summary_work"
        const val DATA_SYNC_WORK = "data_sync_work"
    }

    object Notifications {
        const val TASK_REMINDER_CHANNEL = "task_reminders"
        const val DAILY_SUMMARY_CHANNEL = "daily_summary"
        const val TIMER_CHANNEL = "timer"
    }
}
