package com.example.cse_study_and_learn_application.ui.setting

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cse_study_and_learn_application.MainViewModel
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentSettingBinding
import com.example.cse_study_and_learn_application.ui.other.DialogQuestMessage

/**
 * Setting fragment
 *
 * @constructor Create empty Setting fragment
 *
 * @author kjy
 * @since 2024-03-05
 */
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)

        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val root: View = binding.root

        binding.linEditUser.setOnClickListener {
            (activity as AppCompatActivity).let {
                it.supportActionBar?.apply {
                    hide()
                }
            }
            val action = SettingFragmentDirections.actionNavigationSettingToEditUserInfoFragment()
            findNavController().navigate(action)

        }

        binding.linLogout.setOnClickListener {
            val dialogQuestMessage = DialogQuestMessage(requireContext(), "정말로 로그아웃 하시겠습니까?")

            dialogQuestMessage.setPositive {
                Toast.makeText(context, "네 클릭", Toast.LENGTH_SHORT).show()
                dialogQuestMessage.dismiss()
            }

            dialogQuestMessage.setNegative {
                Toast.makeText(context, "아니요 클릭", Toast.LENGTH_SHORT).show()
                dialogQuestMessage.dismiss()
            }

            dialogQuestMessage.show()
        }

        binding.linWithdraw.setOnClickListener {
            val dialogQuestMessage = DialogQuestMessage(requireContext(), "정말로 탈퇴 하시겠습니까?")

            dialogQuestMessage.setPositive {
                Toast.makeText(context, "네 클릭", Toast.LENGTH_SHORT).show()
                dialogQuestMessage.dismiss()
            }

            dialogQuestMessage.setNegative {
                Toast.makeText(context, "아니요 클릭", Toast.LENGTH_SHORT).show()
                dialogQuestMessage.dismiss()
            }

            dialogQuestMessage.show()
        }


        return root
    }

    override fun onResume() {
        super.onResume()
        // 액티비티 레벨의 앱바를 다시 보여주고 마진 적용
        (activity as AppCompatActivity).let {
            it.supportActionBar?.apply {
                it.supportActionBar?.apply {
                    show()
                    val navHostFragment = it.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                    val layoutParams = navHostFragment?.view?.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.topMargin = mainViewModel.appBarHeight
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
