package com.mlg

import com.mlg.domain.player.PlayerService
import com.mlg.domain.player.dto.PlayerCreateRequest
import com.mlg.domain.player.dto.PlayerCreateResponse
import com.mlg.domain.player.dto.PlayerUpdateRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject


/**
 * Configures the application's HTTP routes.
 *
 * Defines RESTful endpoints for player-related operations:
 * - `POST /players`: Create a new player.
 * - `PUT /players`: Update a player's score.
 * - `DELETE /players`: Clear all players (reset tournament).
 * - `GET /players`: List all players ranked by score.
 * - `GET /players/{id}`: Get player details by ID.
 *
 * /players routes are secured by basic authentication
 *
 * Also serves:
 * - OpenAPI documentation at `/openapi`.
 * - Static resources from the `/static` path.
 */
fun Application.configureRouting() {
    val playerService by inject<PlayerService>()

    routing {
        authenticate("auth-basic") {
            route("/players") {
                post {
                    val playerName = call.receive<PlayerCreateRequest>().name
                    val playerId = playerService.createPlayer(playerName)
                    call.respond(HttpStatusCode.Created, PlayerCreateResponse(playerId))
                }
                put {
                    val playerUpdate = call.receive<PlayerUpdateRequest>()
                    playerService.updatePlayerScore(playerUpdate)
                    call.respond(HttpStatusCode.NoContent)
                }
                delete {
                    playerService.clearTournament()
                    call.respond(HttpStatusCode.NoContent)
                }
                get {
                    call.respond(playerService.listPlayersByRank())
                }
                get("/{id}"){
                    val playerId = call.parameters["id"]
                    try {
                        ObjectId(playerId)
                    }
                    catch (err: IllegalArgumentException) {
                        val msg = "Invalid id format: ${err.message}"
                        throw BadRequestException(msg)
                    }
                    call.respond(playerService.getPlayerById(playerId!!))
                }
            }
        }
        openAPI(path = "openapi")
        staticResources("/static", "static")
    }
}
