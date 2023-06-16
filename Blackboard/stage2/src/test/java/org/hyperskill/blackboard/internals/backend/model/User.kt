package org.hyperskill.blackboard.internals.backend.model

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.hyperskill.blackboard.internals.backend.dto.LoginResponse
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

abstract class User(val username: String, val role: String, val plainPass: String) {

    object Role {
        const val ROLE_TEACHER = "TEACHER"
        const val ROLE_STUDENT = "STUDENT"
    }

    companion object {
        private val messageDigest = MessageDigest.getInstance("SHA-256")
    }

    private val plainPassBytes = plainPass.toByteArray(StandardCharsets.UTF_8)
    private val sha256HashPass = messageDigest.digest(plainPassBytes)
    val base64sha256HashPass = Base64.getEncoder().encodeToString(sha256HashPass)

    private val jwtAlg = Algorithm.HMAC256("testSecret")

    val token = JWT.create()
        .withIssuer("blackBoardApp")
        .withSubject(username)
        .withClaim("ROLE", role)
        .sign(jwtAlg)

    fun toLoginResponse() : LoginResponse {
        return LoginResponse(username, token, role)
    }
}