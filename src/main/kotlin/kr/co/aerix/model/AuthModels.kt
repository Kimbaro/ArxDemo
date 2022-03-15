package kr.co.aerix.model

import io.ktor.auth.*

data class AuthRequest(val user_id: String, val user_pw: String)
data class AuthResponse(val token: String)
data class User(val name: String, val password: String, val other:String="default"): Principal