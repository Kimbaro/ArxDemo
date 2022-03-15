package kr.co.aerix.plugins

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureJWT() {
    install(Authentication) {

    }
}