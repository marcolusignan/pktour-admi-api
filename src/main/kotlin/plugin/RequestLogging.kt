package com.mlg.plugin

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.*


/**
 * Configures request logging for the Ktor application.
 *
 * Installs the [CallLogging] feature to log incoming HTTP requests at INFO level.
 * Only logs requests whose path starts with "/" (typically all application endpoints).
 */
fun Application.configureRequestLogging() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}
