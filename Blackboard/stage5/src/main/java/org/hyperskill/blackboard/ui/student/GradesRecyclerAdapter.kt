package org.hyperskill.blackboard.ui.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.databinding.ListItemGradeBinding
import org.hyperskill.blackboard.util.Util

class GradesRecyclerAdapter(
    /*val onPredictionGradesChanged: (List<Int>) -> Unit*/
) : ListAdapter<Int, GradesRecyclerAdapter.GradesViewHolder>(Util.intDiffcallback) {


//    private var predictionGrades : MutableList<Int> = grades.toMutableList()
//        set(value) {
//            field = value
//            for(i in value.indices) {
//                value[i] = if(value[i] < 0) 0 else value[i]
//            }
//            onPredictionGradesChanged(predictionGrades)
//        }

    inner class GradesViewHolder(val item: ListItemGradeBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(gradeValue: Int, gradeIndex: Int) {
            item.gradeHeaderTv.text = "T:${gradeIndex + 1}"
            if(gradeValue < 0) {
                item.gradeValueEt.setText("")
                item.gradeValueEt.isEnabled = true
//                item.gradeValueET.setOnEditorActionListener { _, _, _ ->
//                    val inputIntValue = item.gradeValueET.text.toString().toIntOrNull() ?: 0
//                    val normalizedInputValue = if(inputIntValue > 100) {
//                        item.gradeValueET.setText("100")
//                        100
//                    } else inputIntValue
//
//                    predictionGrades[gradeIndex] = normalizedInputValue
//                    onPredictionGradesChanged(predictionGrades)
//                    true
//                }
            } else {
                item.gradeValueEt.setText("$gradeValue")
                item.gradeValueEt.isEnabled = false
            }
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