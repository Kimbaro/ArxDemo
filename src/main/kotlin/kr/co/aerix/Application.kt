package kr.co.aerix

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.network.tls.certificates.*
import io.ktor.routing.*
import kr.co.aerix.model.UserDomain
import kr.co.aerix.plugins.*
import kr.co.aerix.router.*
import kr.co.aerix.service.*
import java.io.File

/**
 * --------------------------------------------------------------------------------
 * @TODO 매우 중요
 * 본 프로젝트는 개발중입니다!!!
 * SSL 적용 시 아래 경로에 정식발급받은 .jks 키파일을 대체해 줘야합니다
 * 이후 아래 SSL 키 생성코드는 제거해줘야합니다
 * --------------------------------------------------------------------------------
 * */
val keyStoreFile = File("./keystore.jks")
val keystore = generateCertificate(
    file = keyStoreFile,
    keyAlias = "sampleAlias",
    keyPassword = "foobar",
    jksPassword = "foobar"
)

/**
 * --------------------------------------------------------------------------------
 * */

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    //인증키 할당
    JwtConfig.secret = environment.config.property("jwt.secret").getString()
    JwtConfig.issuer = environment.config.property("jwt.issuer").getString()
    JwtConfig.audience = environment.config.property("jwt.audience").getString()
    JwtConfig.myRealm = environment.config.property("jwt.realm").getString()
    JwtConfig.algorithm = Algorithm.HMAC512(JwtConfig.secret)
    /**
     * 클라이언트 요청으로 받은 JWT 토큰을 검증함
     * */
    install(Authentication) {
        jwt {
            realm = JwtConfig.myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC512(JwtConfig.secret))
                    .withAudience(JwtConfig.audience)
                    .withIssuer(JwtConfig.issuer)
                    .build()
            )
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
        auth()
    }
}
