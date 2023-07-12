package org.hyperskill.blackboard.ui.student

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.hyperskill.blackboard.BlackboardApplication
import org.hyperskill.blackboard.data.model.Credential.Companion.getCredential
import org.hyperskill.blackboard.databinding.BlackboardTitleBinding
import org.hyperskill.blackboard.databinding.FragmentStudentBinding


class StudentFragment : Fragment() {

    private val studentViewModel: StudentViewModel by viewModels {
        val activity = requireActivity()
        val application = activity.application as BlackboardApplication
        val credential = arguments!!.getCredential()
        StudentViewModel.Factory(credential, application.studentClient, Handler(activity.mainLooper))
    }

    lateinit var binding: FragmentStudentBinding
    lateinit var titleBinding: BlackboardTitleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        studentViewModel.fetchGrades()
        binding = FragmentStudentBinding.inflate(inflater, container, false)
        titleBinding = BlackboardTitleBinding.bind(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            studentNameTv.text = studentViewModel.username
            studentViewModel.apply {
                grades.observe(viewLifecycleOwner) { grades ->
                    studentGradesRv.adapter = GradesRecyclerAdapter(grades)
                }
                examGrade.observe(viewLifecycleOwner) { examGrade ->
                    val examGradeStr = if(examGrade < 0) " " else "$examGrade"
                    studentExamEt.setText(examGradeStr)
                }
                partialGrade.observe(viewLifecycleOwner) { partialGrade ->
                    studentPartialResultTv.text = "Partial Result: $partialGrade"
                }
                finalGrade.observe(viewLifecycleOwner) {
                    val finalGradeStr = if (it == null) " " else "$it"
                    studentFinalResultTv.text = "Final Result: $finalGradeStr"
                }
                messageNetworkError.observe(viewLifecycleOwner) {
                    titleBinding.blackboardTitle.error = it
                    titleBinding.blackboardTitle.requestFocus()
                }

            }
        }
    }
}