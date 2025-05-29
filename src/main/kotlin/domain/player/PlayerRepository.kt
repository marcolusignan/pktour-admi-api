package com.mlg.domain.player

import com.mlg.dependency.DatabaseService.Companion.objectFromDocument
import com.mlg.dependency.DatabaseService.Companion.objectToDocument
import com.mlg.domain.player.dto.PlayerDocument
import com.mlg.domain.player.dto.PlayerUpdateRequest
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.Document
import org.bson.types.ObjectId


/**
 * Repository class for managing player data in MongoDB.
 *
 * Provides operations for inserting, querying, updating, and clearing player documents.
 *
 * @param connection The [MongoDatabase] connection instance.
 */
class PlayerRepository(connection: MongoDatabase) {
    /**
     * MongoDB collection for player documents.
     */
    var collection: MongoCollection<Document>

    init {
        // Create "players" collection if it does not exist
        connection.createCollection("players")
        collection = connection.getCollection("players")
    }

    /**
     * Inserts a new player document with the given [name] and an initial score of zero.
     *
     * @param name The name of the player to insert.
     * @return The string representation of the newly created player's ObjectId.
     */
    suspend fun insertPlayer(name: String): String = withContext(Dispatchers.IO) {
        val doc = objectToDocument(PlayerDocument(name, 0))
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    /**
     * Checks whether a player with the specified [name] exists.
     *
     * @param name The player name to check.
     * @return `true` if a player with the given name exists, otherwise `false`.
     */
    suspend fun playerExistsByName(name: String) = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("name", name)).count() > 0
    }

    /**
     * Retrieves a player document by its string [id].
     *
     * @param id The string representation of the player's ObjectId.
     * @return The [PlayerDocument] if found; otherwise, `null`.
     */
    suspend fun getPlayerById(id: String): PlayerDocument? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).first()?.let { doc -> objectFromDocument(doc) }
    }

    /**
     * Updates the score of the player specified in [playerUpdateRequest].
     *
     * @param playerUpdateRequest The request containing the player ID and new score.
     * @return `true` if the update was successful (player found), otherwise `false`.
     */
    suspend fun updatePlayerScore(playerUpdateRequest: PlayerUpdateRequest): Boolean = withContext(Dispatchers.IO) {
        val updatedDoc = collection.findOneAndUpdate(
            Filters.eq("_id", ObjectId(playerUpdateRequest.id)),
            Updates.set("score", playerUpdateRequest.score)
        )
        updatedDoc != null
    }

    /**
     * Deletes all documents in the "players" collection.
     *
     * @return The number of documents deleted.
     */
    suspend fun clearCollection() = withContext(Dispatchers.IO) {
        collection.deleteMany(Filters.empty()).deletedCount
    }

    /**
     * Calculates the rank of a player based on their [playerScore].
     *
     * The rank is determined by counting how many players have a strictly higher score.
     *
     * @param playerScore The score of the player to rank.
     * @return The rank as an integer, where 1 means highest score.
     */
    suspend fun getRankByScore(playerScore: Int) = withContext(Dispatchers.IO) {
        val higherScoreCount = collection.countDocuments(Filters.gt("score", playerScore))
        higherScoreCount.toInt() + 1
    }

    /**
     * Retrieves all players from the collection as an iterable of [PlayerDocument].
     *
     * @return An iterable of all player documents.
     */
    suspend fun findAllPlayers(): Iterable<PlayerDocument> = withContext(Dispatchers.IO) {
        collection.find(Filters.empty()).map { doc -> objectFromDocument<PlayerDocument>(doc) }
    }
}