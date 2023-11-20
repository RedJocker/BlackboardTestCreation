package org.hyperskill.blackboard.internals.backend.dto

data class StudentsNamesResponse(val students: List<Names>) {
    data class Names(val name: String)
}

