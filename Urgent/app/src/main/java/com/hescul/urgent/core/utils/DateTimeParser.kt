package com.hescul.urgent.core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.format.DateTimeFormatter

class DateTimeParser {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun toDateTime(epoch: Long = System.currentTimeMillis()): String {
            return DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.ofEpochSecond(epoch))
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun toTime(epoch: Long = System.currentTimeMillis()): String {
            return DateTimeFormatter.ISO_LOCAL_TIME.format(Instant.ofEpochSecond(epoch))
        }
    }
}