package org.hyperskill.blackboard.network.teacher.dto

data class GradeUpdateRequest(val grades: List<Int>, val exam: Int)