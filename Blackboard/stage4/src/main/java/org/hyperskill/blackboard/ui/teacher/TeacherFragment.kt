package org.hyperskill.blackboard.ui.teacher

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
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.hyperskill.blackboard.BlackboardApplication
import org.hyperskill.blackboard.R
import org.hyperskill.blackboard.data.model.Credential
import org.hyperskill.blackboard.data.model.Credential.Companion.getCredential
import org.hyperskill.blackboard.data.model.Credential.Companion.putCredential
import org.hyperskill.blackboard.data.model.Student.Companion.putStudent
import org.hyperskill.blackboard.databinding.BlackboardTitleBinding
import org.hyperskill.blackboard.databinding.FragmentTeacherBinding


class TeacherFragment : Fragment() {

    private lateinit var binding: FragmentTeacherBinding
    private lateinit var titleBinding: BlackboardTitleBinding
    lateinit var credentials: Credential

    private val studentsAdapter = StudentsRecyclerAdapter(listOf(), onStudentClick = { student ->
        val args = Bundle().apply {
            putStudent(student)
            putCredential(credentials)
        }
        findNavController()
            .navigate(R.id.action_teacherFragment_to_teacherStudentDetailsFragment, args)
    })

    private val teacherViewModel by viewModels<TeacherViewModel> {
        val activity = requireActivity()
        val application = activity.application as BlackboardApplication
        TeacherViewModel.Factory(application.teacherClient, Handler(activity.mainLooper))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        credentials = arguments!!.getCredential()
        binding = FragmentTeacherBinding.inflate(layoutInflater, container, false)
        titleBinding = BlackboardTitleBinding.bind(binding.root)
        teacherViewModel.fetchStudents(credentials)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.teacherStudentsListRv.adapter = studentsAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    teacherViewModel.students.collect {
                        studentsAdapter.students = it
                    }
                }
                launch {
                    teacherViewModel.networkErrorMessage.collect {
                        titleBinding.blackboardTitle.error = it
                        if(it != null) {
                            titleBinding.blackboardTitle.requestFocus()
                        }
                    }
                }
            }
        }
    }
}