package com.mlg.dependency

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.config.tryGetString
import kotlinx.serialization.json.Json
import org.bson.Document

/**
 * Service responsible for managing MongoDB database connection.
 *
 * Initializes a MongoDB client and provides a connection to the specified database
 * using configuration values from the given [Application] environment.
 *
 * The connection is closed automatically when the application stops.
 *
 * @param app The Ktor [Application] instance to retrieve configuration and lifecycle events.
 */
class DatabaseService(app: Application) {
    /**
     * The connected [MongoDatabase] instance.
     */
    val connection: MongoDatabase

    init {
        val config = app.environment.config

        val user = config.tryGetString("db.mongo.user")
        val password = config.tryGetString("db.mongo.password")
        val host = config.tryGetString("db.mongo.host") ?: "127.0.0.1"
        val port = config.tryGetString("db.mongo.port") ?: "27017"
        val maxPoolSize = config.tryGetString("db.mongo.maxPoolSize")?.toInt() ?: 20
        val databaseName = config.tryGetString("db.mongo.database.name") ?: "pktouradmin-database-1"

        val credentials = user?.let { userVal -> password?.let { passwordVal -> "$userVal:$passwordVal@" } }.orEmpty()
        val uri = "mongodb://$credentials$host:$port/?maxPoolSize=$maxPoolSize&w=majority"

        val mongoClient = MongoClients.create(uri)
        connection = mongoClient.getDatabase(databaseName)

        app.monitor.subscribe(ApplicationStopped) {
            mongoClient.close()
        }
    }

    companion object {
        val json = Json { ignoreUnknownKeys = true }
        /**
         * Serializes an object of type [T] to a MongoDB [Document].
         *
         * @param data The object to serialize.
         * @return A [Document] representing the serialized JSON of the object.
         */
        inline fun <reified T> objectToDocument(data: T): Document =
            Document.parse(Json.Default.encodeToString(data))

        /**
         * Deserializes a MongoDB [Document] to an object of type [T].
         *
         * @param document The MongoDB [Document] to deserialize.
         * @return The deserialized object of type [T].
         */
        inline fun <reified T> objectFromDocument(document: Document): T =
            json.decodeFromString(document.toJson())
    }
}