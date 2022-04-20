package kr.co.aerix

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.routing.*
import kr.co.aerix.model.UserDomain
import kr.co.aerix.plugins.*
import kr.co.aerix.router.*
import kr.co.aerix.service.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()
    AuthService.secret = secret
    AuthService.issuer = issuer
    AuthService.audience = audience
    AuthService.myRealm = myRealm

    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            realm = "com.aerix"
            validate {
                val user_id = it.payload.getClaim("user_id").asString()
                val password = it.payload.getClaim("password").asString()
                if (user_id != null && password != null) {
                    //인증에 성공하면 User 객체를 반환한다
                    UserDomain(user_id, password)
                } else {
                    null
                }
            }
        }
    }

    configureMonitoring()
    configureSerialization()
    configureHTTP()

    //custom
    DatabaseInitializer.init()
    install(Routing) {
        workplace(WorkplaceService())
        facility(FacilityService())
        sensor(SensorService(), WorkplaceService())
        data(DataService(), SensorService())
        auth(AuthService())
    }
}
