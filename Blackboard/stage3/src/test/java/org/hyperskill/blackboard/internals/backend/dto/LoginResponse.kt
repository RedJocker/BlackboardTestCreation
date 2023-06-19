package org.hyperskill.blackboard.internals.backend.dto

data class LoginResponse(val username: String,val token: String, val role: String)