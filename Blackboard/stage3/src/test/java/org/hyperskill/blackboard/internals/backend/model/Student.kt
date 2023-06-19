package org.hyperskill.blackboard.internals.backend.model

import org.hyperskill.blackboard.internals.backend.model.User.Role.ROLE_STUDENT

class Student(userName: String, plainPass: String, var grades: Grades) : User(userName, ROLE_STUDENT, plainPass) {
    override fun toString(): String {
        return "Student(grades=$username, plainPass=$plainPass)"
    }
}