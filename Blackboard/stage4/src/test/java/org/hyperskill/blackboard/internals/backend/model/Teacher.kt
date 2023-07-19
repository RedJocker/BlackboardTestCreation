package org.hyperskill.blackboard.internals.backend.model

import org.hyperskill.blackboard.internals.backend.model.User.Role.ROLE_TEACHER

class Teacher(userName: String, plainPass: String) : User(userName, ROLE_TEACHER, plainPass) {
    override fun toString(): String {
        return "Teacher(grades=$username, plainPass=$plainPass)"
    }
}