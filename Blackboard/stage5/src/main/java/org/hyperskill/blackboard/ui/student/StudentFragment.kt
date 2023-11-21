package org.hyperskill.blackboard.ui.student

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.hyperskill.blackboard.BlackboardApplication
import org.hyperskill.blackboard.R
import org.hyperskill.blackboard.data.model.Credential.Companion.getCredential
import org.hyperskill.blackboard.databinding.BlackboardTitleBinding
import org.hyperskill.blackboard.databinding.FragmentStudentBinding
import org.hyperskill.blackboard.databinding.StudentDetailBinding
import org.hyperskill.blackboard.ui.HorizontalLinearLayoutManager


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
            val gradesAdapter = GradesRecyclerAdapter(
                onImeNext = { nextPos ->
                    if (nextPos < currentList.size) {
                        studentDetailBinding.studentGradesRv.scrollToPosition(nextPos)
                        studentDetailBinding.studentGradesRv
                            .findViewHolderForAdapterPosition(nextPos)
                            ?.itemView?.findViewById<EditText>(R.id.grade_value_et)
                            ?.requestFocus()
                    } else {
                        studentDetailBinding.studentExamEt.requestFocus()
                    }
                },
                onPredictionGradesChanged = {
                    println("========= PREDICTION GRADES $it =========")
                    studentViewModel.setPredictionGradesList(it)
                }
            )
            studentGradesRv.apply {
                layoutManager = HorizontalLinearLayoutManager(context)
                adapter = gradesAdapter
            }
            studentViewModel.apply {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                        launch {
                            grades.collect { grades ->
                                println("grades: $grades")
                                gradesAdapter.submitList(grades)
                            }
                        }
                        launch {
                            examGrade.collect { examGrade ->
                                println("examGrade: $examGrade")
                                if(examGrade < 0) {
                                    studentDetailBinding.studentExamEt.isEnabled = true
                                    studentExamEt.setText("")
                                } else {
                                    studentDetailBinding.studentExamEt.isEnabled = false
                                    studentExamEt.setText("$examGrade")
                                }
                            }
                        }

                        launch {
                            partialGradeText.collect { partialGradeText ->
                                println("partialGrade: $partialGradeText")
                                studentPartialResultTv.text = partialGradeText
                            }
                        }

                        launch {
                            finalGradeText.collect { finalGradeText ->
                                println("finalGrade: $finalGradeText")
                                studentFinalResultTv.text = finalGradeText
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