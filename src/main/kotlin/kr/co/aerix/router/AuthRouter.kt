package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kr.co.aerix.model.User
import kr.co.aerix.plugins.JwtConfig
import kr.co.aerix.service.AuthService

fun Routing.auth(service: AuthService) {

    route("/demo/login") {
        post {
            //val body = call.receive<AuthRequest>()
            /*사용자 로그인 인증 서비스 수행*/
            /*.........................*/
            val user = call.receive<User>()
            print("${user.name} , pwd= ${user.password}")
            val token = JwtConfig.generateToken(user)
            call.respond(token)
        }

        authenticate {
            get("/authenticate") {
                val principal = call.principal<User>()
                val username = principal!!.name
                val userpass = principal!!.password
                call.respond(
                    "get authenticated value from token username=${username} userpass=${userpass}"
                )
            }
        }
    }
}