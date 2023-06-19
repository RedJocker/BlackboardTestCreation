package org.hyperskill.blackboard.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.hyperskill.blackboard.data.model.Credential.Companion.getCredential
import org.hyperskill.blackboard.databinding.FragmentStudentBinding


class StudentFragment : Fragment() {

    private val studentViewModel: StudentViewModel by viewModels {
//        val activity = requireActivity()
//        val application = activity.application as BlackboardApplication
        val credential = arguments!!.getCredential()
        StudentViewModel.Factory(credential)
    }

    lateinit var binding: FragmentStudentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            studentNameTv.text = studentViewModel.username
            studentGradesRv.adapter = GradesRecyclerAdapter(listOf(10, 30, 50, 100, 90, 50, 65, 100))
        }
    }
}