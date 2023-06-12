package org.hyperskill.blackboard.internals.backend.database

import org.hyperskill.blackboard.internals.backend.model.Grades
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.backend.model.Teacher

object MockUserDatabase {
    const val GEORGE = "George"
    const val LUCAS = "Lucas"
    const val MARTIN = "Martin"
    const val HARRISON = "Harrison"
    const val MICHAEL = "Michael"
    const val ORWELL = "Orwell"
    const val BENSON = "Benson"


    val users = mapOf(
        GEORGE to Teacher(GEORGE, "1234"),
        LUCAS to Student(LUCAS, "32A1", Grades(listOf(60, -1, 50, 70, 99, 80, -1), -1)),
        MARTIN to Student(MARTIN, "2222", Grades(listOf(60, -1, 50, 70, 99, 80, -1), -1)),
        HARRISON to Student(HARRISON, "0909", Grades(listOf(60, -1, 50, 70, 99, 80, -1), -1)),
        MICHAEL to Student(MICHAEL, "mi", Grades(listOf(10, 0, 30, 40, 50, 0, 0), -1)),
        ORWELL to Student(ORWELL, "A$4@2ds", Grades(listOf(100, 80, 70, 90, 60, -1, 100), -1)),
        BENSON to Student(BENSON, "35799b8", Grades(listOf(60, -1, 50, 99, 99, 80, -1), 50)),
    )

    init{
        println("MockUserDatabase\n$users}")
    }
}