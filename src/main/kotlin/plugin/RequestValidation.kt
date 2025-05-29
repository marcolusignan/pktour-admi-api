package com.mlg.plugin

import com.mlg.domain.player.dto.PlayerCreateRequest
import com.mlg.domain.player.dto.PlayerUpdateRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.RequestValidation


/**
 * Configures request validation for the Ktor application.
 *
 * Installs the [RequestValidation] feature and sets up validation rules
 * for [PlayerCreateRequest] and [PlayerUpdateRequest] types by invoking
 * their respective `validate` methods.
 */
fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<PlayerCreateRequest> { player ->
            player.validate()
        }
        validate<PlayerUpdateRequest> { player ->
            player.validate()
        }
    }
}
