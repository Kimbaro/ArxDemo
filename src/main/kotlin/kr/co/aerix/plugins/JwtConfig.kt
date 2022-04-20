package kr.co.aerix.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import kr.co.aerix.model.UserDomain
import java.util.*

object JwtConfig {
    var secret = "1234" // use your own secret
    var issuer = "com.aerix"  // use your own issuer
    var audience = ""
    var validityInMs = 36_000_00 * 24 // 1 day
    var algorithm = Algorithm.HMAC512(secret)
    var myRealm = ""

    /**
     * Client 로부터 전달받은 id, pw로 JWT를 생성하는 생성기
     */
    fun generateToken(user: UserDomain): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("user_id", user.user_id)
        .withClaim("password", user.password)
        .withExpiresAt(getExpiration())  // optional
        .sign(algorithm)

    /**
     * Calculate the expiration Date based on current time + the given validity
     */
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

}