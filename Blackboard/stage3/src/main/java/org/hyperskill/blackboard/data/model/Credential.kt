package org.hyperskill.blackboard.data.model

import android.os.Bundle

data class Credential(val username: String, val token: String, val role: Role) {
    companion object {
        fun Bundle.putCredential(credential: Credential) {
            putString("username", credential.username)
            putString("token", credential.token)
            putString("role", credential.role.toString())
        }
        fun Bundle.getCredential(): Credential {
            val username = getString("username")!!
            val token = getString("token")!!
            val role = Role.valueOf(getString("role")!!)
            return Credential(username, token, role)
        }
    }

    enum class Role {
        TEACHER, STUDENT
    }
}