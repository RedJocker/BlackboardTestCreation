package org.hyperskill.blackboard.internals.screen

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.internals.BlackboardUnitTest
import org.hyperskill.blackboard.internals.backend.model.Student

class TeacherScreen<T: Activity>(
    val test: BlackboardUnitTest<T>,
    val screenName : String,
    initViews: Boolean = true
) {

    companion object {
        const val ID_TEACHER_STUDENTS_LIST_RV = "teacher_students_list_rv"
        const val ID_LIST_ITEM_STUDENT_NAME_TV = "list_item_student_name_tv"
        const val PATH_TEACHER_STUDENTS = "/teacher/student/"
    }

    val blackboardTitle by lazy {
        BlackboardTitle(screenName, test).blackboardTitle
    }

    val teacherStudentsListRv: RecyclerView by lazy {
        with(test) {
            activity.findViewByString<RecyclerView>(ID_TEACHER_STUDENTS_LIST_RV)
        }
    }

    init {
        if(initViews) {
            blackboardTitle
            teacherStudentsListRv
        }
    }

    fun assertStudentList(expectedStudentList : List<Student>, caseDescription: String) = with(test) {
        teacherStudentsListRv.doActionOnEachListItem(expectedStudentList, caseDescription)
        { viewSupplier, position, student  ->
            val item = ItemStudent(viewSupplier())
            item.listItemStudentNameTv.assertText(
                expectedText = student.username,
                idString = ID_LIST_ITEM_STUDENT_NAME_TV,
                caseDescription = caseDescription
            )
        }
    }

    fun clickStudent(studentName: Int, caseDescription: String) = with(test) {
        teacherStudentsListRv.doActionOnSingleListItem(studentName, caseDescription)
        { viewSupplier ->
            val item = ItemStudent(viewSupplier())
            item.root.clickAndRun()
        }
    }

    inner class ItemStudent(val root: View) {
        val listItemStudentNameTv : TextView = with(test) {
            root.findViewByString(ID_LIST_ITEM_STUDENT_NAME_TV)
        }
    }
}