package com.mlg.exception

/**
 * Exception representing a domain-specific error with an associated HTTP status code or error code.
 *
 * @property message The error message.
 * @property code The status or error code related to the exception (e.g., HTTP status code).
 */
data class DomainException(override val message: String, val code: Number): Exception(message)
