package org.hyperskill.blackboard.data.model

import android.os.Bundle

class Student(val name: String) {

    companion object {
        fun Bundle.putStudent(student: Student) {
            putString("name", student.name)
        }
        fun Bundle.getStudent(): Student {
            val name = getString("name")!!
            return Student(name)
        }
    }
}