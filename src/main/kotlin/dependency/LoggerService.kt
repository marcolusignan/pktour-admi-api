package com.mlg.dependency

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Service providing a simple logger wrapper for logging messages at different levels.
 *
 * Uses SLF4J's [Logger] internally with a logger named `"main"`.
 */
class LoggerService {
    /**
     * The SLF4J [Logger] instance used for logging.
     */
    val logger: Logger = LoggerFactory.getLogger("main")

    fun error(msg: String) = logger.error(msg)
    fun warn(msg: String) = logger.warn(msg)
    fun info(msg: String) = logger.info(msg)
    fun debug(msg: String) = logger.debug(msg)
}