package org.hyperskill.blackboard.internals.screen

import android.app.Activity
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.model.Student

class TeacherStudentScreen<T: Activity>(
    test: BlackboardUnitTest<T>,
    screenName : String,
    initViews: Boolean = true
) : StudentScreen<T>(test, screenName, initViews) {

    override fun assertStudentDetails(student: Student, caseDescription: String) = with(test) {
        studentNameTv.assertText(student.username, ID_STUDENT_NAME_TV, caseDescription)
        studentGradesRv.doActionOnEachListItem(student.grades.grades, caseDescription)
        { viewSupplier, position, grade  ->
            val expectedHeader = "T:${position + 1}"
            val expectedGrade = "$grade"
            val item = ItemGrade(viewSupplier())
            item.gradeHeaderTv.assertText(expectedHeader, ID_GRADE_HEADER_TV, caseDescription)
            item.gradeValueET.assertText(expectedGrade, ID_GRADE_VALUE_ET, caseDescription)
        }
        val expectedPartial = "Partial Result: ${student.grades.partialGrade}"
        studentPartialResultTv
            .assertText(expectedPartial, ID_STUDENT_PARTIAL_RESULT_TV, caseDescription)

        val expectedExam = "${student.grades.exam}"
        studentExamEt.assertText(expectedExam, ID_STUDENT_EXAM_ET, caseDescription)

        val finalString = "${student.grades.teacherFinalGrade}"
        val expectedFinal = "Final Result: $finalString"
        studentFinalResultTv.assertText(expectedFinal, ID_STUDENT_FINAL_RESULT_TV, caseDescription)
    }
}