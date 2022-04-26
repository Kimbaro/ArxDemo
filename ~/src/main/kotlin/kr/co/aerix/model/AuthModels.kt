package kr.co.aerix.model

import io.ktor.auth.*

data class AuthRequest(val user_id: String, val user_pw: String)
data class AuthResponse(val token: String)
data class UserDomain(val user_id: String, val password: String, val level:String="default"): Principal