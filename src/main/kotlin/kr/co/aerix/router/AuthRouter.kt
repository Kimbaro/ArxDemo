package kr.co.aerix.router

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kr.co.aerix.model.UserDomain
import kr.co.aerix.plugins.JwtConfig
import kr.co.aerix.service.AuthService

fun Routing.auth() {

    route("") {
        post("/demo/login") {
            /*로그인 인증             수행*/

            //사용자 로그인 인증 서비스 구현부
            //사용자 로그인 인증 서비스 구현부
            //사용자 로그인 인증 서비스 구현부

            /*.........................*/
            val user = call.receive<UserDomain>()
            println("${user.user_id} , pwd= ${user.password}")
            val token = JwtConfig.generateToken(user)
            call.respond(token)
        }

        //유효하지 않는 인증서의 경우 401을 반환한다
        authenticate {
            //토큰의 유효성 검증 및 id pw 반환
            get("/authenticate") {
                val principal = call.principal<UserDomain>()
                val username = principal!!.user_id
                val userpass = principal!!.password
                call.respond(
                    "Access Token OK By ${username}  ${userpass}"
                )
            }
        }
    }
}