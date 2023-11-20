package org.hyperskill.blackboard.internals.backend.model

import org.hyperskill.blackboard.internals.backend.model.User.Role.ROLE_STUDENT

class Student(userName: String, plainPass: String, var grades: Grades) : User(userName, ROLE_STUDENT, plainPass) {
    override fun toString(): String {
        return "Student(grades=$username, plainPass=$plainPass)"
    }

    fun copy(
        userName: String = this.username,
        plainPass: String = this.plainPass,
        grades: Grades = this.grades
    ): Student {
        return Student(userName, plainPass, grades)
    }
}