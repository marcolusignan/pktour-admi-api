package com.mlg.plugin

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.config.tryGetString


/**
 * Configures basic HTTP authentication for the Ktor application.
 *
 * This function installs the [Authentication] feature and sets up Basic authentication under the name `"auth-basic"`.
 * The expected login and password credentials are read from the application configuration under
 * `security.login` and `security.password`. If not provided, it falls back to the default values:
 * - Login: `"pktouradminlogin"`
 * - Password: `"pktouradminpwd"`
 *
 * Only requests with valid credentials will be granted access under routes requiring this authentication scheme.
 *
 * Example `application.yml` configuration:
 * ```
 * security:
 *   login: myadmin
 *   password: secret123
 * ```
 *
 * @receiver [Application] The Ktor application instance.
 */
fun Application.configureAuthentication() {
    val config = environment.config
    val apiLogin = config.tryGetString("security.login") ?: "pktouradminlogin"
    val apiPwd = config.tryGetString("security.password") ?: "pktouradminpwd"

    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.name == apiLogin && credentials.password == apiPwd) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
