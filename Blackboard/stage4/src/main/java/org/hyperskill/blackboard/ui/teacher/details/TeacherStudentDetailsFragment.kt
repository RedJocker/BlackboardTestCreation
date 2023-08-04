package org.hyperskill.blackboard.ui.teacher.details

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.hyperskill.blackboard.BlackboardApplication
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.data.model.Credential.Companion.getCredential
import org.hyperskill.blackboard.data.model.Student
import org.hyperskill.blackboard.data.model.Student.Companion.getStudent
import org.hyperskill.blackboard.databinding.FragmentTeacherStudentDetailsBinding
import org.hyperskill.blackboard.databinding.StudentDetailBinding
import org.hyperskill.blackboard.util.Extensions.showToast

class TeacherStudentDetailsFragment : Fragment() {

    lateinit var binding: FragmentTeacherStudentDetailsBinding
    lateinit var detailBinding: StudentDetailBinding
    lateinit var credentials: Credential
    lateinit var student: Student

    private val detailsViewModel: TeacherStudentDetailsViewModel by viewModels {
        val activity = requireActivity()
        val application = activity.application as BlackboardApplication
        TeacherStudentDetailsViewModel.Factory(application.teacherClient, Handler(activity.mainLooper)) {
            context?.showToast("Grades submitted")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTeacherStudentDetailsBinding.inflate(inflater, container, false)
        detailBinding = StudentDetailBinding.bind(binding.root)
        credentials = arguments!!.getCredential()
        student = arguments!!.getStudent()
        detailsViewModel.fetchGrades(credentials, student)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailBinding.studentNameTv.text = student.name

        detailBinding.apply {
            detailsViewModel.apply {
                grades.observe(viewLifecycleOwner) { gradesList ->
                    studentGradesRv.adapter = TeacherGradesStudentAdapter(gradesList) { editedGrades ->
                        println("editedGrades: $editedGrades")
                        detailsViewModel.setEditedGrades(editedGrades)
                    }
                }

                partialResult.observe(viewLifecycleOwner) { partialResultString ->
                    println("observe partialResult: $partialResultString")
                    studentPartialResultTv.text = partialResultString
                }

                finalResult.observe(viewLifecycleOwner) {
                    studentFinalResultTv.text = it
                }

                binding.detailBtn.setOnClickListener {
                    detailsViewModel.updateGrades(credentials, student.name)

                }

                examGrade.observe(viewLifecycleOwner) {
                    studentExamEt.setText("$it")
                }

                studentExamEt.setOnEditorActionListener { v, actionId, event ->
                    val inputIntValue = studentExamEt.text.toString().toIntOrNull() ?: -1
                    val normalizedInputValue = if(inputIntValue > 100) {
                        studentExamEt.setText("100")
                        100

                    } else inputIntValue
                    setEditedExamGrades(normalizedInputValue)
                    true
                }

                isExamEnabled.observe(viewLifecycleOwner) { isExamEnabled ->
                    if(isExamEnabled) {
                        studentExamEt.isEnabled = true
                        studentExamEt.setText(editedExamGrade.value?.toString() ?: "")

                    }
                    else {
                        studentExamEt.setText("")
                        studentExamEt.isEnabled = false
                    }
                }
            }
        }
    }
}