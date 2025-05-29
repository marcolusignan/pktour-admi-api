package com.mlg.plugin

import com.mlg.exception.DomainException
import com.mlg.exception.ErrorResponse
import com.mongodb.MongoException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import kotlinx.serialization.SerializationException
import org.slf4j.LoggerFactory


/**
 * Configures centralized error handling for the Ktor application.
 *
 * Installs the [StatusPages] feature with handlers for various exceptions:
 * - [RequestValidationException]: Returns HTTP 400 with error message.
 * - [DomainException]: Maps domain error codes to appropriate HTTP status.
 * - [BadRequestException]: Returns HTTP 400 with error message.
 * - [MongoException]: Logs the error and returns HTTP 500 with generic DB error message.
 * - General [Exception]: Logs the error and returns HTTP 500 with error message.
 *
 * @receiver The Ktor application instance.
 */
fun Application.configureErrorHandlers() {
    val logger = LoggerFactory.getLogger("main")

    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Unknow cause."))
        }
        exception<DomainException> { call, cause ->
            val statusCode = when (cause.code) {
                400 -> HttpStatusCode.BadRequest
                404 -> HttpStatusCode.NotFound
                409 -> HttpStatusCode.Conflict
                else -> HttpStatusCode.InternalServerError
            }
            call.respond(statusCode, ErrorResponse(cause.message))
        }
        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Unknow cause."))
        }
        exception<MongoException> { call, cause ->
            logger.error(cause.message)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Database error"))
        }
        exception<SerializationException> { call, cause ->
            logger.error(cause.message)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Database error"))
        }
        exception<Exception> { call, cause ->
            logger.error(cause.message)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(cause.message ?: "Unknow cause."))
        }
    }
}
