package com.example.hometasker.util

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(onEvent)
        }
    }
}

fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

fun <T> List<T>.replace(index: Int, item: T): List<T> {
    return this.toMutableList().apply { set(index, item) }
}

fun <T> List<T>.replaceIf(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
    return this.map { if (predicate(it)) transform(it) else it }
}

inline fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    val index = this.indexOfFirst(predicate)
    return if (index == -1) null else index
}

fun Int.toOrdinal(): String {
    val suffix = when {
        this % 100 in 11..13 -> "th"
        this % 10 == 1 -> "st"
        this % 10 == 2 -> "nd"
        this % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$this$suffix"
}
