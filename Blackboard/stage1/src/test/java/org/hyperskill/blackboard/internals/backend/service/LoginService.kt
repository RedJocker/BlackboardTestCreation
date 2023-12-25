package org.hyperskill.blackboard.internals.backend.service

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hyperskill.blackboard.internals.backend.database.MockUserDatabase
import org.hyperskill.blackboard.internals.backend.dto.LoginResponse
import org.hyperskill.blackboard.internals.backend.response.Response
import org.hyperskill.blackboard.internals.backend.response.Response.withBody

class LoginService(val moshi: Moshi): Service {

    val mapType = Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        String::class.java
    )

    val responseAdapter = moshi.adapter(LoginResponse::class.java)

    override fun serve(request: RecordedRequest): MockResponse {
        return if (request.method == "POST") {
            val bodyString = request.body.readUtf8()
            val mapBody = moshi.adapter<Map<String, String>>(mapType).fromJson(bodyString)
            println(mapBody)

            val requestPass = mapBody?.get("pass")
            val username = mapBody?.get("username")

            if (requestPass == null || username == null) {
                println("Expected login request to have both username and pass fields")
                Response.badRequest400
            } else if(username == "error") {
                println("Testing network error")
                Response.gatewayTimeout504
            } else MockUserDatabase.users[username].let { user ->
                when {
                    user == null -> {
                        println("401 No user with username '$username'")
                        Response.unauthorized401
                    }
                    user.base64sha256HashPass == requestPass ->
                        Response.ok200.withBody(responseAdapter.toJson(user.toLoginResponse()))
                    else -> {
                        println("401, Expected base64sha256HashPass '${user.base64sha256HashPass}', " +
                                "actual base64sha256HashPass '$requestPass'")
                        Response.unauthorized401
                    }
                }
            }
        } else { Response.notFound404 }
    }
}