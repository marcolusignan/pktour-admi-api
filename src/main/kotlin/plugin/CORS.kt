package com.mlg.plugin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*


/**
 * Configures HTTP features for the Ktor application.
 *
 * Installs the [CORS] plugin with allowed HTTP methods and headers.
 * Note: `anyHost()` is enabled for development convenience;
 * restrict this in production environments for better security.
 */
fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
}
