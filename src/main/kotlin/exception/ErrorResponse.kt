package com.mlg.exception

import kotlinx.serialization.Serializable


/**
 * Serializable class representing a standard error response.
 *
 * @property message The error message to be sent to clients.
 */
@Serializable
class ErrorResponse(val message: String)
