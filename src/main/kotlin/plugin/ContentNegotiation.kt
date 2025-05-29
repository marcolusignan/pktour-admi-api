package com.mlg.plugin

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*


/**
 * Configures content negotiation for the Ktor application.
 *
 * Installs the [ContentNegotiation] plugin and configures it to use JSON serialization
 * with Kotlinx Serialization.
 */
fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json()
    }
}
