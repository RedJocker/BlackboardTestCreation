package org.hyperskill.blackboard.internals.backend.database

import org.hyperskill.blackboard.internals.backend.model.Grades
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.backend.model.Teacher

object MockUserDatabase {
    const val GEORGE = "George"         // Teacher
    const val LUCAS = "Lucas"           // no grades case
    const val MARTIN = "Martin"         //
    const val HARRISON = "Harrison"
    const val MICHAEL = "Michael"
    const val ORWELL = "Orwell"
    const val BENSON = "Benson"


    val users = mapOf(
        GEORGE to Teacher(GEORGE, "1234"),
        LUCAS to Student(LUCAS, "32A1", Grades(listOf(-1, -1, -1, -1, -1, -1, -1), -1)),
        MARTIN to Student(MARTIN, "2222", Grades(listOf(60, -1, 50, 70, 99, 80, -1), -1)),
        HARRISON to Student(HARRISON, "0909", Grades(listOf(50, -1, 80, 55, 75, 60, 50), 80)),
        MICHAEL to Student(MICHAEL, "mi", Grades(listOf(10, -1, -1, 60, 50, 30, -1), 100)),
        ORWELL to Student(ORWELL, "A$4@2ds", Grades(listOf(100, 70, 30, 50, 40, 50, 40), -1)),
        BENSON to Student(BENSON, "35799b8", Grades(listOf(100, 80, 95, 88, 75, 100, 100), -1)),
    )
}