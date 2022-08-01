package com.moraware.mango.util

import android.util.Log
import java.util.logging.*

/**
 * Make JUL work on Android.
 */
class AndroidLoggingHandler : Handler() {

    override fun close() {}

    override fun flush() {}

    override fun publish(record: LogRecord) {
        if (!super.isLoggable(record))
            return

        val name = record.loggerName
        val maxLength = 30
        val tag = if (name.length > maxLength) name.substring(name.length - maxLength) else name

        try {
            val level = getAndroidLevel(record.level)
            Log.println(level, tag, record.message)
            if (record.thrown != null) {
                Log.println(level, tag, Log.getStackTraceString(record.thrown))
            }
        } catch (e: RuntimeException) {
            Log.e("AndroidLoggingHandler", "Error logging message.", e)
        }

    }

    companion object {

        fun reset(rootHandler: Handler) {
            val rootLogger = LogManager.getLogManager().getLogger("")
            val handlers = rootLogger.handlers
            for (handler in handlers) {
                rootLogger.removeHandler(handler)
            }
            rootLogger.addHandler(rootHandler)
        }

        internal fun getAndroidLevel(level: Level): Int {
            val value = level.intValue()

            return if (value >= Level.SEVERE.intValue()) {
                Log.ERROR
            } else if (value >= Level.WARNING.intValue()) {
                Log.WARN
            } else if (value >= Level.INFO.intValue()) {
                Log.INFO
            } else {
                Log.DEBUG
            }
        }
    }
}