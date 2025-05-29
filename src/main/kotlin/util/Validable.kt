package com.mlg.util

import io.ktor.server.plugins.requestvalidation.ValidationResult


/**
 * Interface for classes that support validation.
 *
 * Implementing classes must define the [validate] method, which returns a [ValidationResult]
 * indicating whether the instance is valid or not.
 */
interface Validable {
    fun validate(): ValidationResult
}