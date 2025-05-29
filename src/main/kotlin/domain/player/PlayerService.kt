package com.mlg.domain.player

import com.mlg.dependency.LoggerService
import com.mlg.domain.player.dto.PlayerUpdateRequest
import com.mlg.exception.DomainException


/**
 * Service layer responsible for player-related business logic.
 *
 * Uses [PlayerRepository] for data access and [LoggerService] for logging.
 *
 * @property playerRepository Repository for player data operations.
 * @property logger Logger service for logging important events.
 */
class PlayerService(val playerRepository: PlayerRepository, val logger: LoggerService) {

    /**
     * Creates a new player with the specified [name].
     *
     * Checks for existing player with the same name and throws a [DomainException] if found.
     * Logs creation event on success.
     *
     * @param name The name of the player to create.
     * @return The string ID of the newly created player.
     * @throws DomainException If a player with the given name already exists (HTTP 409).
     */
    suspend fun createPlayer(name: String): String {
        val playerExists = playerRepository.playerExistsByName(name)
        if (playerExists) throw DomainException("Player '$name' already exists.", 409)

        val createdPlayer = playerRepository.insertPlayer(name)
        logger.info("Player created: $createdPlayer.")

        return createdPlayer
    }

    /**
     * Updates the score of a player based on the [playerUpdate] request.
     *
     * Throws a [DomainException] if the player with the given ID does not exist.
     * Logs update event on success.
     *
     * @param playerUpdate The update request containing player ID and new score.
     * @throws DomainException If the player is not found (HTTP 404).
     */
    suspend fun updatePlayerScore(playerUpdate: PlayerUpdateRequest) {
        val isPlayerUpdated = playerRepository.updatePlayerScore(playerUpdate)
        if (!isPlayerUpdated) throw DomainException("Player with id '${playerUpdate.id}' not found.", 404)
        logger.info("Player $playerUpdate updated.")
    }

    /**
     * Clears the tournament by deleting all players from the repository.
     *
     * Logs the number of deleted players.
     */
    suspend fun clearTournament() {
        val deletedPlayersCount = playerRepository.clearCollection()
        logger.info("$deletedPlayersCount Players deleted.")
    }

    /**
     * Retrieves a player by their [playerId] and calculates their rank.
     *
     * Throws a [DomainException] if the player does not exist.
     *
     * @param playerId The unique string ID of the player.
     * @return The player as a [PlayerModel] including rank.
     * @throws DomainException If the player is not found (HTTP 404).
     */
    suspend fun getPlayerById(playerId: String): PlayerModel {
        val playerDocument = playerRepository.getPlayerById(playerId)
        if (playerDocument == null) throw DomainException("Player with id '${playerId}' not found.", 404)

        val playerRank = playerRepository.getRankByScore(playerDocument.score)
        val player = PlayerModel(playerId, playerDocument.name, playerDocument.score, playerRank)
        logger.info("Player $player found.")

        return player
    }

    /**
     * Lists all players sorted by rank descending (highest score first).
     *
     * Returns an empty list if no players exist.
     * Logs the list of players retrieved.
     *
     * @return A list of [PlayerModel] sorted by rank.
     */
    suspend fun listPlayersByRank(): List<PlayerModel> {
        val playersDocuments = playerRepository.findAllPlayers().toList()
        if (playersDocuments.isEmpty()) {
            logger.info("No player found.")
            return emptyList()
        }

        val sortedPlayersDocuments = playersDocuments.sortedByDescending { it.score }

        var currentRank = 0
        var currentScore = 0
        val players = mutableListOf<PlayerModel>()
        for(player in sortedPlayersDocuments) {
            if (player.score < currentScore || currentRank == 0) {
                currentRank ++
            }
            currentScore = player.score
            players.add(PlayerModel(player.id.toString(), player.name, player.score, currentRank))
        }

        logger.info("Players sorted by rank : $players")
        return players
    }
}