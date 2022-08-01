package com.moraware.mango.logger

import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Singleton

open class MangoLogger(name: String = "MangoLogger", resourceBundleName: String? = null) : Logger(name, resourceBundleName) {

    override fun log(priority: Level?, msg: String?) {
        if(Level.SEVERE == priority) {
            reportToCrashAnalytics(msg)
        }

        super.log(priority, msg)
    }

    fun log(msg: String?) {
        super.log(Level.FINE, msg)
    }

    private fun reportToCrashAnalytics(msg: String?) {
        msg?.let {
            //report to analytics
        }
    }
}