package com.mlg

import com.mlg.plugin.configureContentNegotiation
import com.mlg.plugin.configureErrorHandlers
import com.mlg.plugin.configureDependencyInjection
import com.mlg.plugin.configureHTTP
import com.mlg.plugin.configureRequestLogging
import com.mlg.plugin.configureRequestValidation
import com.mlg.plugin.configureAuthentication
import io.ktor.server.application.*


/**
 * Entry point for the Ktor application using Netty.
 *
 * Delegates to Ktor's [io.ktor.server.netty.EngineMain] to launch the server.
 *
 * @param args Command-line arguments passed to the application.
 */
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

/**
 * Main Ktor application module.
 *
 * This function configures all required application modules such as:
 * - Content negotiation
 * - Request validation
 * - Global error handling
 * - Dependency injection with Koin
 * - Logging
 * - Security configuration
 * - CORS and other HTTP settings
 * - Routing
 *
 * This function is automatically invoked by Ktor when the application starts.
 */
fun Application.module() {
    configureContentNegotiation()
    configureRequestValidation()
    configureErrorHandlers()
    configureDependencyInjection()
    configureRequestLogging()
    configureAuthentication()
    configureHTTP()
    configureRouting()
}
