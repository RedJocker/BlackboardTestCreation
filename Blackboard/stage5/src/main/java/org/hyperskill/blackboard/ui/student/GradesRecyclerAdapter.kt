package org.hyperskill.blackboard.ui.student

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.databinding.ListItemGradeBinding
import org.hyperskill.blackboard.util.Util

class GradesRecyclerAdapter(
    val onImeNext: GradesRecyclerAdapter.(pos: Int) -> Unit,
    val onPredictionGradesChanged: (List<Int>) -> Unit
) : ListAdapter<Int, GradesRecyclerAdapter.GradesViewHolder>(Util.intDiffcallback) {


    private var predictionGrades : MutableList<Int> = this.currentList.toMutableList()
        set(value) {
            field = value
            for(i in value.indices) {
                value[i] = if(value[i] < 0) 0 else value[i]
            }
            onPredictionGradesChanged(predictionGrades.map { it })
        }

    override fun submitList(list: List<Int>?) {
        if (list == null)
            return
        predictionGrades = list.toMutableList()
        super.submitList(list)
    }

    inner class GradesViewHolder(val item: ListItemGradeBinding) : RecyclerView.ViewHolder(item.root) {
        fun bind(gradeValue: Int, gradeIndex: Int) {
            item.gradeHeaderTv.text = "T:${gradeIndex + 1}"
            if(gradeValue < 0) {
                item.gradeValueEt.setText("")
                item.gradeValueEt.isEnabled = true
                item.gradeValueEt.setOnEditorActionListener { v, actionId, event ->
                    val inputIntValue = item.gradeValueEt.text.toString().trim().toIntOrNull() ?: -1
                    val normalizedInputValue = if(inputIntValue > 100) {
                        item.gradeValueEt.setText("100")
                        100
                    } else if (inputIntValue < 0 ) {
                        item.gradeValueEt.setText("")
                        0
                    } else {
                        item.gradeValueEt.setText("$inputIntValue")
                        inputIntValue
                    }
                    predictionGrades[gradeIndex] = normalizedInputValue
                    onPredictionGradesChanged(predictionGrades)
                    if (actionId == EditorInfo.IME_ACTION_NEXT){
                        onImeNext(gradeIndex + 1)
                    }
                    true
                }
            } else {
                item.gradeValueEt.setText("$gradeValue")
                item.gradeValueEt.isEnabled = false
            }
            item.gradeValueEt.onFocusChangeListener = Util.onFocusEditTextPutCursorEnd
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