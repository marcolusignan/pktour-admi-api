package com.mlg.domain.player
import kotlinx.serialization.Serializable

/**
 * Data class representing a player model used in the application layer.
 *
 * @property id The unique identifier of the player as a string.
 * @property name The name of the player.
 * @property score The current score of the player.
 * @property rank The current rank of the player.
 */
@Serializable
data class PlayerModel(val id: String, val name: String, var score: Int, var rank: Int)
