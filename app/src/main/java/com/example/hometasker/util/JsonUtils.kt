package com.example.hometasker.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object JsonUtils {

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    val compactJson = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    inline fun <reified T> encode(value: T): String {
        return json.encodeToString(value)
    }

    inline fun <reified T> encodeCompact(value: T): String {
        return compactJson.encodeToString(value)
    }

    inline fun <reified T> decode(jsonString: String): T {
        return json.decodeFromString(jsonString)
    }

    inline fun <reified T> decodeOrNull(jsonString: String): T? {
        return try {
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            null
        }
    }
}
