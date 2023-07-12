package org.hyperskill.blackboard.network.student.dto


sealed class GradesResponse {
    data class Success(val grades: List<Int>, val exam: Int) : GradesResponse()
    data class Fail(val message: String, val code: Int) : GradesResponse()
}