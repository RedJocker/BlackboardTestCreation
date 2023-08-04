package org.hyperskill.blackboard.ui.student

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.hyperskill.blackboard.BlackboardApplication
import org.hyperskill.blackboard.data.model.Credential.Companion.getCredential
import org.hyperskill.blackboard.databinding.BlackboardTitleBinding
import org.hyperskill.blackboard.databinding.FragmentStudentBinding
import org.hyperskill.blackboard.databinding.StudentDetailBinding


class StudentFragment : Fragment() {

    private val studentViewModel: StudentViewModel by viewModels {
        val activity = requireActivity()
        val application = activity.application as BlackboardApplication
        val credential = arguments!!.getCredential()
        StudentViewModel.Factory(credential, application.studentClient, Handler(activity.mainLooper))
    }

    lateinit var binding: FragmentStudentBinding
    lateinit var titleBinding: BlackboardTitleBinding
    lateinit var studentDetailBinding: StudentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        studentViewModel.fetchGrades()
        binding = FragmentStudentBinding.inflate(inflater, container, false)
        studentDetailBinding = StudentDetailBinding.bind(binding.root)
        titleBinding = BlackboardTitleBinding.bind(studentDetailBinding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studentDetailBinding.apply {
            studentNameTv.text = studentViewModel.username
            studentViewModel.apply {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        launch {
                            grades.collect { grades ->
                                println("grades: $grades")
                                studentGradesRv.adapter = GradesRecyclerAdapter(grades)
                            }
                        }
                        launch {
                            examGrade.collect { examGrade ->
                                println("examGrade: $examGrade")
                                val examGradeStr = if(examGrade < 0) "" else "$examGrade"
                                studentExamEt.setText(examGradeStr)
                            }
                        }

                        launch {
                            partialGrade.collect { partialGrade ->
                                println("partialGrade: $partialGrade")
                                studentPartialResultTv.text = "Partial Result: $partialGrade"
                            }
                        }

                        launch {
                            finalGrade.collect { finalGrade ->
                                println("finalGrade: $finalGrade")
                                val finalGradeStr = if (finalGrade < 0) "" else "$finalGrade"
                                studentFinalResultTv.text = "Final Result: $finalGradeStr"
                            }
                        }

                        launch {
                            messageNetworkError.collect  {
                                titleBinding.blackboardTitle.error = it
                                titleBinding.blackboardTitle.requestFocus()
                            }
                        }
                    }
                }
            }
        }
    }
}