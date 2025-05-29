package com.mlg.domain.player.dto

import kotlinx.serialization.Serializable

/**
 * Response data class returned after creating a player.
 *
 * @property id The unique identifier of the newly created player.
 */
@Serializable
data class PlayerCreateResponse(val id: String)
