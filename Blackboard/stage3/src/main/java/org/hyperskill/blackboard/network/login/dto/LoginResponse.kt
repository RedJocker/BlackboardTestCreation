package org.hyperskill.blackboard.network.login.dto

import org.hyperskill.blackboard.data.model.Credential


sealed class LoginResponse {

    data class Success(val username: String, val token: String, val role: Credential.Role) : LoginResponse(){

        fun toCredential(): Credential {
            return Credential(username, token, role)
        }
    }
    data class Fail(val message: String, val code: Int) : LoginResponse()

}