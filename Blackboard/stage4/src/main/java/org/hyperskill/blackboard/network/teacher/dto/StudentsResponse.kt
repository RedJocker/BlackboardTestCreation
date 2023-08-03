package org.hyperskill.blackboard.network.teacher.dto

import org.hyperskill.blackboard.data.model.Student

sealed class StudentsResponse {
    data class Success(val students: List<Student>) : StudentsResponse()
    data class Fail(val message: String, val code: Int) : StudentsResponse()
}