package org.hyperskill.blackboard.internals.screen

import android.app.Activity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.model.Student
import org.hyperskill.blackboard.internals.backend.model.Teacher
import org.hyperskill.blackboard.internals.screen.TeacherScreen.Companion.PATH_TEACHER_STUDENTS

class TeacherStudentScreen<T: Activity>(
    test: BlackboardUnitTest<T>,
    screenName : String,
    initViews: Boolean = true
) : StudentScreen<T>(test, screenName, initViews) {

    companion object {
        const val ID_DETAIL_SUBMIT_BTN = "detail_submit_btn"
    }

    val detailSubmitButton by lazy {
        with(test){
            activity.findViewByString<FloatingActionButton>(ID_DETAIL_SUBMIT_BTN)
        }
    }

    override fun assertStudentDetails(student: Student, caseDescription: String) = with(test) {
        studentNameTv.assertText(student.username, ID_STUDENT_NAME_TV, caseDescription)
        studentGradesRv.doActionOnEachListItem(student.grades.grades, caseDescription)
        { viewSupplier, position, grade  ->
            val expectedHeader = "T:${position + 1}"
            val expectedGrade = "$grade"
            val item = ItemGrade(viewSupplier())
            item.gradeHeaderTv.assertText(expectedHeader, ID_GRADE_HEADER_TV, caseDescription)
            item.gradeValueEt.assertText(expectedGrade, ID_GRADE_VALUE_ET, caseDescription)
        }

        assertExam(student, caseDescription)
        assertCalculation(student, caseDescription)
    }

    override fun assertExam(student: Student, caseDescription: String) = with(test) {
        val expectedExam = "${student.grades.exam}"
        studentExamEt.assertText(expectedExam, ID_STUDENT_EXAM_ET, caseDescription)
    }

    override fun assertCalculation(student: Student, caseDescription: String) = with(test) {
        val expectedPartial = "Partial Result: ${student.grades.partialGrade}"
        studentPartialResultTv
            .assertText(expectedPartial, ID_STUDENT_PARTIAL_RESULT_TV, caseDescription)

        val finalString = "${student.grades.teacherFinalGrade}"
        val expectedFinal = "Final Result: $finalString"
        studentFinalResultTv.assertText(expectedFinal, ID_STUDENT_FINAL_RESULT_TV, caseDescription)
    }

    fun submitChangesAssertPatch(caseDescription: String, teacher: Teacher, student: Student) = with(test) {
        detailSubmitButton.clickAndRun()
        shadowLooper.runToEndOfTasks()
        assertPatchRequestWithToken(caseDescription, teacher.token, "$PATH_TEACHER_STUDENTS${student.username}")
        assertPatchResponse(caseDescription, student)
    }
}