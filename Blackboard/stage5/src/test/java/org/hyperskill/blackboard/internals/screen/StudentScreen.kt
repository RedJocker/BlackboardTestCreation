package org.hyperskill.blackboard.internals.screen

import android.app.Activity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.model.Student
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

open class StudentScreen<T: Activity>(
    val test: BlackboardUnitTest<T>,
    val screenName : String,
    initViews: Boolean = true
) {


    companion object {
        const val ID_STUDENT_NAME_TV = "student_name_tv"
        const val ID_STUDENT_GRADES_LABEL = "student_grades_label"
        const val ID_STUDENT_GRADES_RV = "student_grades_rv"
        const val ID_STUDENT_PARTIAL_RESULT_TV = "student_partial_result_tv"
        const val ID_STUDENT_EXAM_LABEL = "student_exam_label"
        const val ID_STUDENT_EXAM_ET = "student_exam_et"
        const val ID_STUDENT_FINAL_RESULT_TV = "student_final_result_tv"
        const val ID_GRADE_VALUE_ET = "grade_value_et"
        const val ID_GRADE_HEADER_TV = "grade_header_tv"
        const val PATH_STUDENTS = "/student/"
    }

    val descriptionInitialization = "On $screenName initialization"

    val blackboardTitle by lazy {
        BlackboardTitle(screenName, test).blackboardTitle
    }

    val studentNameTv: TextView by lazy {
        with(test) {
            activity.findViewByString<TextView>(ID_STUDENT_NAME_TV).apply {}
        }
    }
    val studentGradesLabel: TextView by lazy {
        with(test) {
            activity.findViewByString<TextView>(ID_STUDENT_GRADES_LABEL).apply {
                assertText("Grades:", ID_STUDENT_GRADES_LABEL, descriptionInitialization)
            }
        }
    }
    val studentGradesRv: RecyclerView by lazy {
        with(test) {
            activity.findViewByString<RecyclerView>(ID_STUDENT_GRADES_RV).apply {}
        }
    }
    val studentPartialResultTv: TextView by lazy {
        with(test) {
            activity.findViewByString<TextView>(ID_STUDENT_PARTIAL_RESULT_TV).apply {}
        }
    }
    val studentExamLabel: TextView by lazy {
        with(test) {
            activity.findViewByString<TextView>(ID_STUDENT_EXAM_LABEL).apply {
                assertText("Exam:", ID_STUDENT_EXAM_LABEL, descriptionInitialization)
            }
        }
    }
    val studentExamEt: EditText by lazy {
        with(test) {
            activity.findViewByString<EditText>(ID_STUDENT_EXAM_ET).apply {}
        }
    }
    val studentFinalResultTv: TextView by lazy {
        with(test) {
            activity.findViewByString<TextView>(ID_STUDENT_FINAL_RESULT_TV).apply {}
        }
    }

    init {
        if(initViews) {
            blackboardTitle
            studentNameTv
            studentGradesLabel
            studentGradesRv
            studentPartialResultTv
            studentExamLabel
            studentExamEt
            studentFinalResultTv
        }
    }

    open fun assertStudentDetails(student: Student, caseDescription: String) = with(test) {
        studentNameTv.assertText(student.username, ID_STUDENT_NAME_TV, caseDescription)
        studentGradesRv.doActionOnEachListItem(student.grades.grades, caseDescription)
        { viewSupplier, position, grade  ->
            val expectedHeader = "T:${position + 1}"
            val gradeNorm = when {
                grade > 0 -> grade
                student.grades.exam > 0 -> 0
                else -> -1
            }
            val expectedGrade = if (gradeNorm < 0) "" else "$gradeNorm"
            val item = ItemGrade(viewSupplier())
            item.gradeHeaderTv.assertText(expectedHeader, ID_GRADE_HEADER_TV, caseDescription)
            item.gradeValueEt.assertText(expectedGrade, ID_GRADE_VALUE_ET, caseDescription)
        }
        val expectedPartial = "Partial Result: ${student.grades.partialGrade}"
        studentPartialResultTv
            .assertText(expectedPartial, ID_STUDENT_PARTIAL_RESULT_TV, caseDescription)

        val expectedExam = if(student.grades.exam < 0 ) "" else "${student.grades.exam}"
        studentExamEt.assertText(expectedExam, ID_STUDENT_EXAM_ET, caseDescription)

        val finalString = if(student.grades.finalGrade < 0) "" else "${student.grades.finalGrade}"
        val expectedFinal = "Final Result: $finalString"
        studentFinalResultTv.assertText(expectedFinal, ID_STUDENT_FINAL_RESULT_TV, caseDescription)
    }

    fun assertStudentNetworkError(caseDescription: String, expectedError: String) = with(test) {
        blackboardTitle.assertError(expectedError, BlackboardTitle.BLACKBOARD_TITLE_ID, caseDescription)
    }

    fun assertGradesEditTextEnabledDisabled(caseDescription: String, student: Student) = with(test) {
        studentGradesRv.doActionOnEachListItem(student.grades.grades, caseDescription)
        { itemViewSupplier, position, grade ->
            if (grade < 0 && student.grades.exam < 0) {
                val messageEnabledEditText =
                    "$caseDescription on grade at index $position " +
                            "with value $grade expected $ID_GRADE_VALUE_ET to be enabled"
                val itemGrade = ItemGrade(itemViewSupplier())
                assertTrue(messageEnabledEditText, itemGrade.gradeValueEt.isEnabled)
            } else {
                val messageDisabledEditText =
                    "$caseDescription on grade at index $position " +
                            "with value $grade expected $ID_GRADE_VALUE_ET to be disabled"
                val itemGrade = ItemGrade(itemViewSupplier())
                assertFalse(messageDisabledEditText, itemGrade.gradeValueEt.isEnabled)
            }
        }
        if (student.grades.exam < 0){
            val messageEnabledEditText =
                "$caseDescription on exam grade with value ${student.grades.exam} " +
                        "expected $ID_STUDENT_EXAM_ET to be enabled"
            assertTrue(messageEnabledEditText, studentExamEt.isEnabled)
        } else {
            val messageDisabledEditText =
                "$caseDescription on exam grade with value ${student.grades.exam} " +
                        "expected $ID_STUDENT_EXAM_ET to be disabled"
            assertFalse(messageDisabledEditText, studentExamEt.isEnabled)
        }
    }


    inner class ItemGrade(private val root: View) {

        val gradeHeaderTv : TextView = with(test) {
            root.findViewByString(ID_GRADE_HEADER_TV)
        }

        val gradeValueEt : EditText = with(test) {
            root.findViewByString(ID_GRADE_VALUE_ET)
        }
    }
}