package com.mlg.domain.player.dto

import com.mlg.util.Validable
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable

/**
 * Data class representing a request to create a player.
 *
 * @property name The name of the player to be created.
 */
@Serializable
data class PlayerCreateRequest(val name: String): Validable {
    /**
     * Validates the player creation request.
     *
     * Checks if the player name is not empty.
     *
     * @return [ValidationResult.Valid] if the name is valid; otherwise,
     *         [ValidationResult.Invalid] with an error message.
     */
    override fun validate() =
        if (name.isEmpty())
            ValidationResult.Invalid("Player name must not be empty.")
        else ValidationResult.Valid
}
