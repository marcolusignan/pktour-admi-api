package com.mlg.domain.player.dto

import com.mlg.util.Validable
import io.ktor.server.plugins.requestvalidation.ValidationResult
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

/**
 * Data class representing a request to update a player's information.
 *
 * @property id The unique identifier of the player, expected to be a valid BSON ObjectId string.
 * @property score The player's score, which must be a non-negative integer.
 */
@Serializable
data class PlayerUpdateRequest(val id: String, val score: Int): Validable {
    /**
     * Validates the player update request.
     *
     * - Checks if the [id] is a valid BSON ObjectId.
     * - Ensures [score] is zero or positive.
     *
     * @return [ValidationResult.Valid] if validation succeeds;
     *         otherwise [ValidationResult.Invalid] with an appropriate error message.
     */
    override fun validate() =
        try {
            ObjectId(id)
            if (score < 0)
                ValidationResult.Invalid("Player score must be a positive integer")
            else ValidationResult.Valid
        }
        catch (err: IllegalArgumentException) {
            ValidationResult.Invalid("Invalid format for 'id': $err")
        }
}
