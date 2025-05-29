package com.mlg

import com.mlg.domain.player.PlayerModel
import com.mlg.domain.player.dto.PlayerCreateRequest
import com.mlg.domain.player.dto.PlayerCreateResponse
import com.mlg.domain.player.dto.PlayerUpdateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.junit.jupiter.api.assertInstanceOf


/**
 * Integration tests for the Ktor application, focusing on player-related REST API endpoints.
 *
 * This test class sets up an embedded test application with a test configuration,
 * including a mocked MongoDB connection on a custom port.
 *
 * It covers:
 * - Creating players
 * - Updating player scores
 * - Fetching players individually and as a list with correct ranking logic
 * - Deleting all players
 *
 * Players are inserted asynchronously with concurrent coroutines.
 * Authorization headers use Basic Auth with configured test credentials.
 */
class ApplicationTest {

    /**
     * Application configuration used in tests.
     * Sets up module, deployment port, security credentials, and MongoDB connection info.
     */
    val appConfig = MapApplicationConfig(
        "ktor.application.modules[0]" to "com.mlg.ApplicationKt.module",
        "ktor.deployment.port" to "8095",
        "security.login" to "pktouradminlogin",
        "security.password" to "pktouradminpwd",
        "db.mongo.user" to "pktouradmin",
        "db.mongo.password" to "pwd",
        "db.mongo.host" to "127.0.0.1",
        "db.mongo.port" to "27019",  // Make sure your local DB test instance matches this
        "db.mongo.database.name" to "pktouradmin"
    )

    /** Basic Authorization header value encoded from test credentials. */
    val authHeader = "Basic " + Base64.getEncoder().encodeToString("pktouradminlogin:pktouradminpwd".toByteArray())

    /** Map of test players with their names and initial scores for batch insertions. */
    val testPlayers = mapOf("pierre" to 10, "paul" to 5, "lea" to 80, "lucie" to 0, "alain" to 10)

    /**
     * Creates a new player by calling the POST /players endpoint.
     *
     * @param client The HttpClient used for making the request.
     * @param name The name of the player to create.
     * @return HttpResponse from the server.
     */
    private suspend fun createPlayer(client: HttpClient, name: String): HttpResponse {
        return client.post("/players") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
            setBody(PlayerCreateRequest(name))
        }

    }

    /**
     * Updates an existing player's score by calling PUT /players endpoint.
     *
     * @param client The HttpClient used for making the request.
     * @param playerId The ID of the player to update.
     * @param playerScore The new score to assign to the player.
     * @return HttpResponse from the server.
     */
    private suspend fun updatePlayerScore(client: HttpClient, playerId: String, playerScore: Int): HttpResponse {
        return client.put("/players") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
            setBody(PlayerUpdateRequest(playerId, playerScore))
        }
    }

    /**
     * Retrieves a player by ID via GET /players/{playerId} endpoint.
     *
     * @param client The HttpClient used for making the request.
     * @param playerId The ID of the player to fetch.
     * @return HttpResponse from the server.
     */
    private suspend fun getPlayerById(client: HttpClient, playerId: String): HttpResponse {
        return client.get("/players/$playerId") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
        }
    }

    /**
     * Inserts the predefined test players asynchronously, creating each player and updating their scores.
     *
     * @param client The HttpClient used for making the requests.
     */
    private suspend fun insertTestPlayers(client: HttpClient) = coroutineScope {
        val jobs = testPlayers.map { (name, score) ->
            async {
                val id = createPlayer(client, name).body<PlayerCreateResponse>().id
                updatePlayerScore(client, id, score)
            }
        }

        jobs.awaitAll()
    }

    /**
     * Retrieves all players from GET /players endpoint.
     *
     * @param client The HttpClient used for making the request.
     * @return HttpResponse from the server.
     */
    private suspend fun getPlayers(client: HttpClient): HttpResponse {
        return client.get("/players") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
        }
    }

    /**
     * Deletes all players by calling DELETE /players endpoint.
     *
     * @param client The HttpClient used for making the request.
     * @return HttpResponse from the server.
     */
    private suspend fun deletePlayers(client: HttpClient): HttpResponse {
        return client.delete("/players") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
        }
    }


    /**
     * Main integration test for player CRUD operations and ranking logic.
     *
     * This test:
     * - Creates a player and asserts creation
     * - Updates player's score and asserts update
     * - Fetches the player by ID and verifies properties
     * - Inserts a batch of test players concurrently
     * - Fetches all players and verifies their ranks and order
     * - Deletes all players and asserts the tournament is empty
     */
    @Test
    fun testRoot() = testApplication {
        // Setup test application instance.
        environment {
            config = appConfig
        }
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Clear the database to prevent collisions on test error.
        val clearedDatabaseResponse = deletePlayers(client)
        assertEquals(HttpStatusCode.NoContent, clearedDatabaseResponse.status)

        // ** Player CRUD test scenario ** :
        // - Create Player
        val createPlayerResponse = createPlayer(client, "freddy")
        assertEquals(HttpStatusCode.Created, createPlayerResponse.status)
        val createPlayerResponseBody = createPlayerResponse.body<PlayerCreateResponse>()
        assertInstanceOf<PlayerCreateResponse>(createPlayerResponseBody)

        // - Update Player score
        val updatePlayerScoreResponse = updatePlayerScore(client, createPlayerResponseBody.id, 150)
        assertEquals(HttpStatusCode.NoContent, updatePlayerScoreResponse.status)

        // - Get Player by id
        val getPlayerResponse = getPlayerById(client, createPlayerResponseBody.id)
        assertEquals(HttpStatusCode.OK, getPlayerResponse.status)
        val getPlayerByIdResponseBody = getPlayerResponse.body<PlayerModel>()
        assertEquals("freddy", getPlayerByIdResponseBody.name)
        assertEquals(150, getPlayerByIdResponseBody.score)
        assertEquals(1, getPlayerByIdResponseBody.rank)

        // - Insert new test players
        insertTestPlayers(client)

        // - List players ordered by rank
        val getPlayersResponse = getPlayers(client)
        assertEquals(HttpStatusCode.OK, getPlayerResponse.status)
        val getPlayersResponseBody = getPlayersResponse.body<List<PlayerModel>>()
        assertInstanceOf<List<PlayerModel>>(getPlayersResponseBody)
        assertEquals(6, getPlayersResponseBody.size)

        val freddy = getPlayersResponseBody[0]
        assertEquals(1, freddy.rank)
        assertEquals("freddy", freddy.name)
        assertEquals(150, freddy.score)
        val lea = getPlayersResponseBody[1]
        assertEquals(2, lea.rank)
        assertEquals("lea", lea.name)
        assertEquals(80, lea.score)
        val pierreOrAlain = getPlayersResponseBody[2]
        assertEquals(3, pierreOrAlain.rank)
        assertEquals(10, pierreOrAlain.score)
        assertTrue(pierreOrAlain.name in setOf("pierre", "alain"))
        val alainOrPierre = getPlayersResponseBody[3]
        assertEquals(3, alainOrPierre.rank)
        assertEquals(10, alainOrPierre.score)
        assertTrue(alainOrPierre.name in setOf("pierre", "alain"))
        val paul = getPlayersResponseBody[4]
        assertEquals(4, paul.rank)
        assertEquals("paul", paul.name)
        assertEquals(5, paul.score)
        val lucie = getPlayersResponseBody[5]
        assertEquals(5, lucie.rank)
        assertEquals("lucie", lucie.name)
        assertEquals(0, lucie.score)

        // - Clear the tournament
        val deletePlayersResponse = deletePlayers(client)
        assertEquals(HttpStatusCode.NoContent, deletePlayersResponse.status)

        // - Ensure that tournament is empty
        val getEmptyPlayersResponse = getPlayers(client)
        assertEquals(HttpStatusCode.OK, getEmptyPlayersResponse.status)
        assertEquals(0, getEmptyPlayersResponse.body<List<PlayerModel>>().size)
    }
}
