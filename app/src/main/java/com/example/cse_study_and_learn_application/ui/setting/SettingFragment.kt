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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cse_study_and_learn_application.MainViewModel
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentSettingBinding
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.ui.other.DialogQuestMessage

/**
 * Setting fragment
 *
 * @constructor Create empty Setting fragment
 *
 * @author kjy
 * @since 2024-03-05
 *
 */
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    private lateinit var settingViewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingViewModel = ViewModelProvider(requireActivity())[SettingViewModel::class.java]

        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val root: View = binding.root

        settingViewModel.logoutResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "로그아웃에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })

        settingViewModel.deactivateResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                // 회원탈퇴 성공 처리
                requireActivity().finish()
            } else {
                // 회원탈퇴 실패 처리
                Toast.makeText(requireContext(), "회원탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show()

            }
        })

        settingViewModel.userInfo.observe(viewLifecycleOwner) {
            binding.tvUserName.text = "${it.nickname} 님"
            AccountAssistant.nickname = it.nickname
        }

        initClickListener(settingViewModel)


        return root
    }

    private fun initClickListener(settingViewModel: SettingViewModel) {
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
            val dialogQuestMessage = DialogQuestMessage(
                requireContext(),
                R.layout.dialog_quest_message,
                "정말로 로그아웃 하시겠습니까?"
            )

            dialogQuestMessage.setPositive {
                //Toast.makeText(context, "네 클릭", Toast.LENGTH_SHORT).show()
                settingViewModel.logout(requireContext())   // 로그아웃 메서드
                dialogQuestMessage.dismiss()
            }

            dialogQuestMessage.setNegative {
                //Toast.makeText(context, "아니요 클릭", Toast.LENGTH_SHORT).show()
                dialogQuestMessage.dismiss()
            }

            dialogQuestMessage.show()
        }

        binding.linWithdraw.setOnClickListener {
            val dialogQuestMessage = DialogQuestMessage(
                requireContext(),
                R.layout.dialog_quest_message,
                "정말로 탈퇴 하시겠습니까?"
            )

            dialogQuestMessage.setPositive {
                //Toast.makeText(context, "네 클릭", Toast.LENGTH_SHORT).show()
                settingViewModel.deactivate(requireContext())   // 회원탈퇴 메서드
                dialogQuestMessage.dismiss()
            }

            dialogQuestMessage.setNegative {
                //Toast.makeText(context, "아니요 클릭", Toast.LENGTH_SHORT).show()
                dialogQuestMessage.dismiss()
            }

            dialogQuestMessage.show()
        }
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

        settingViewModel.getUserInfo(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
