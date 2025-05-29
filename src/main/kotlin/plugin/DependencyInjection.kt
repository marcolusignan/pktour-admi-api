package com.mlg.plugin

import com.mlg.dependency.DatabaseService
import com.mlg.dependency.LoggerService
import com.mlg.domain.player.PlayerRepository
import com.mlg.domain.player.PlayerService
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger


/**
 * Configures dependency injection for the Ktor application using Koin.
 *
 * Installs the Koin plugin, sets up SLF4J logging for Koin, and defines
 * singleton dependencies for application services and database connection.
 *
 * The following singletons are provided:
 * - [LoggerService]
 * - [MongoDatabase] (from [DatabaseService])
 * - [DatabaseService]
 * - [PlayerService]
 * - [PlayerRepository]
 */
fun Application.configureDependencyInjection() {
    val app = this
    install(Koin) {
        slf4jLogger()
        modules(module {
            single { LoggerService() }
            single<MongoDatabase> { get<DatabaseService>().connection }
            single { DatabaseService(app)}
            single { PlayerService(get(), get()) }
            single { PlayerRepository(get()) }
        })
    }
}
