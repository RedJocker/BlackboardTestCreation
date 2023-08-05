package org.hyperskill.blackboard.ui.teacher.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.databinding.ListItemGradeBinding
import org.hyperskill.blackboard.util.Util

class TeacherGradesStudentAdapter(
    val onEditedGradesChanged: (grades: List<Int>) -> Unit
) : ListAdapter<Int, TeacherGradesStudentAdapter.GradesViewHolder>(Util.intDiffcallback) {

    private var editedGrades : MutableList<Int> = currentList.toMutableList()

    inner class GradesViewHolder(val item: ListItemGradeBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(gradeValue: Int, gradeIndex: Int) {
            item.gradeHeaderTv.text = "T:${gradeIndex + 1}"
            item.gradeValueEt.setText("$gradeValue")
//            item.gradeValueEt.setOnEditorActionListener { v, actionId, event ->
//                val inputIntValue = item.gradeValueEt.text.toString().toIntOrNull() ?: -1
//                val normalizedInputValue = if(inputIntValue > 100) {
//                    item.gradeValueEt.setText("100")
//                    100
//
//                } else inputIntValue
//
//                editedGrades[adapterPosition] = normalizedInputValue
//                onEditedGradesChanged(editedGrades)
//                true
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradesViewHolder {
        val itemGradeBinding =
            ListItemGradeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return GradesViewHolder(itemGradeBinding)
    }

    override fun onBindViewHolder(holder: GradesViewHolder, position: Int) {
        holder.bind(currentList[position], position)
    }
}