package org.hyperskill.blackboard.ui.teacher.details

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
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.data.model.Credential.Companion.getCredential
import org.hyperskill.blackboard.data.model.Student
import org.hyperskill.blackboard.data.model.Student.Companion.getStudent
import org.hyperskill.blackboard.databinding.BlackboardTitleBinding
import org.hyperskill.blackboard.databinding.FragmentTeacherStudentDetailsBinding
import org.hyperskill.blackboard.databinding.StudentDetailBinding
import org.hyperskill.blackboard.ui.HorizontalLinearLayoutManager
import org.hyperskill.blackboard.util.Extensions.showToast
import org.hyperskill.blackboard.util.Util


class TeacherStudentDetailsFragment : Fragment() {

    lateinit var binding: FragmentTeacherStudentDetailsBinding
    lateinit var detailBinding: StudentDetailBinding
    lateinit var titleBinding: BlackboardTitleBinding
    lateinit var credentials: Credential
    lateinit var student: Student

    private val detailsViewModel: TeacherStudentDetailsViewModel by viewModels {
        val activity = requireActivity()
        val application = activity.application as BlackboardApplication
        TeacherStudentDetailsViewModel.Factory(application.teacherClient, Handler(activity.mainLooper)) {
            context?.showToast("Grades submitted")
        }
    }

    private val teacherStudentGradesAdapter = TeacherGradesStudentAdapter(
            onImeNext = { nextPos ->
                if (nextPos < currentList.size) {
                    detailBinding.studentGradesRv.scrollToPosition(nextPos)
                    detailBinding.studentGradesRv
                            .findViewHolderForAdapterPosition(nextPos)
                            ?.itemView?.findViewById<EditText>(R.id.grade_value_et)
                            ?.requestFocus()
                } else {
                    detailBinding.studentExamEt.requestFocus()
                }
            },
            onEditedGradesChanged = { editedGrades ->
                println("onGradesChanged editedGrades: $editedGrades")
                detailsViewModel.setEditedGrades(editedGrades)
            }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTeacherStudentDetailsBinding.inflate(inflater, container, false)
        detailBinding = StudentDetailBinding.bind(binding.root)
        titleBinding = BlackboardTitleBinding.bind(binding.root)
        credentials = arguments!!.getCredential()
        student = arguments!!.getStudent()
        detailsViewModel.fetchGrades(credentials, student)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailBinding.studentNameTv.text = student.name

        detailBinding.studentGradesRv.apply {
            adapter = teacherStudentGradesAdapter
            layoutManager = HorizontalLinearLayoutManager(context)
        }

        detailBinding.apply {
            detailsViewModel.apply {
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        launch {
                            grades.collect { gradesList ->
                                println("observer grades $gradesList")
                                detailsViewModel.setEditedGrades(gradesList.map { it.value })
                                teacherStudentGradesAdapter.submitList(gradesList)
                            }
                        }
                        launch {
                            networkErrorMessage.collect {
                                println("observe networkErrorMessage: $it")
                                titleBinding.blackboardTitle.error = it
                                if(it != null) {
                                    titleBinding.blackboardTitle.requestFocus()
                                }
                            }
                        }

                        launch {
                            examGrade.collect {
                                println("observe examGrade: ${it}")
                                studentExamEt.setText("${it}")
                            }
                        }

                        launch {
                            partialResult.collect { partialResultString ->
                                println("observe partialResult: $partialResultString")
                                studentPartialResultTv.text = partialResultString
                            }
                        }

                        launch {
                            finalResult.collect {
                                println("observe finalResult: $it")
                                studentFinalResultTv.text = it
                            }
                        }
                    }
                }

                binding.detailSubmitBtn.setOnClickListener {
                    detailsViewModel.updateGrades(credentials, student.name)
                }

                studentExamEt.setOnEditorActionListener { v, actionId, event ->
                    val inputIntValue = studentExamEt.text.toString().toIntOrNull() ?: -1
                    val normalizedInputValue = if(inputIntValue > 100) {
                        studentExamEt.setTextKeepState("100")
                        100
                    } else if(inputIntValue <= -1){
                        studentExamEt.setTextKeepState("-1")
                        -1
                    } else {
                        studentExamEt.setTextKeepState("$inputIntValue")
                        inputIntValue
                    }

                    setEditedExamGrades(normalizedInputValue)
                    true
                }
                studentExamEt.onFocusChangeListener = Util.onFocusEditTextPutCursorEnd
            }
        }
    }
}