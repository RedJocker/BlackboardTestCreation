package org.hyperskill.blackboard.internals.backend.model

data class Grades(val grades: List<Int>, val exam: Int) {
    val partialGrade = grades.sumOf { if (it < 0) 0 else if (it > 100) 100 else it } / grades.size

    private val hasMoreTest = grades.any { it < 0 }
    private val isExamPossible = partialGrade in 30 until 70
    val finalGrade = if (exam >= 0) {
        if (isExamPossible) {
            (partialGrade + (if (exam > 100) 100 else exam)) / 2
        } else { partialGrade }
    } else {
        if(hasMoreTest || isExamPossible) {
            -1
        } else { partialGrade }
    }
    val teacherFinalGrade = if (isExamPossible) {
        val examGrade = if (exam < 0) 0 else if (exam > 100) 100 else exam
        (partialGrade + examGrade) / 2
    } else { partialGrade }
}

/*
- negative values on request means the grade was not assigned yet
- partial grade is calculated with the average of grades, replacing missing grades by zero
- exam is only possible if the partial grade is is in the range [30 70)
- if there is an exam grade (exam grade is zero or positive)
      then all missing test grades are considered effectively zeroed, implying that there are no missing test grades anymore
- if there are missing test grades do not calculate finalGrade
- if there are no missing test grades and exam is possible do not calculate final grade
- if exam is not possible calculate final without taking exam value into consideration even if there is one
 */
