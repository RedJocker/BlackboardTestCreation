package org.hyperskill.blackboard.ui.teacher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.blackboard.data.model.Student
import org.hyperskill.blackboard.databinding.ListItemStudentBinding

class StudentsRecyclerAdapter(students : List<Student>, val onStudentClick: (Student) -> Unit)
    : RecyclerView.Adapter<StudentsRecyclerAdapter.StudentsViewHolder>() {

    var students = students
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    lateinit var listItemStudentBinding: ListItemStudentBinding

    inner class StudentsViewHolder(private val binding: ListItemStudentBinding)
        : RecyclerView.ViewHolder(binding.root
    ) {
        fun bind(position: Int) {
            val student = students[position]
            binding.apply {
                listItemStudentNameTv.text = student.name
                listItemStudentNameTv.setPadding(50)
                listItemStudentNameTv.setOnClickListener {
                    onStudentClick(student)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
        listItemStudentBinding = ListItemStudentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
        )
        return StudentsViewHolder(listItemStudentBinding)
    }

    override fun getItemCount(): Int {
        return students.size
    }

    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {
        holder.bind(position)
    }
}