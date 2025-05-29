package com.mlg.domain.player.dto

import com.mlg.util.MongoObjectIdSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

/**
 * Data class representing a player document stored in MongoDB.
 *
 * @property name The name of the player.
 * @property score The player's score.
 * @property id The MongoDB ObjectId of the document, serialized using [MongoObjectIdSerializer].
 *              This corresponds to the `_id` field in MongoDB.
 */
@Serializable
data class PlayerDocument(
    val name: String,
    val score: Int,
    @SerialName("_id")
    @Serializable(with = MongoObjectIdSerializer::class)
    val id: ObjectId? = null
)
