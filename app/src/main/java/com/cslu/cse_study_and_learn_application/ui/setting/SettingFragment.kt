package com.cslu.cse_study_and_learn_application.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cslu.cse_study_and_learn_application.BuildConfig
import com.cslu.cse_study_and_learn_application.MainViewModel
import com.cslu.cse_study_and_learn_application.R
import com.cslu.cse_study_and_learn_application.databinding.FragmentSettingBinding
import com.cslu.cse_study_and_learn_application.ui.login.AccountAssistant
import com.cslu.cse_study_and_learn_application.ui.login.SignInActivity
import com.cslu.cse_study_and_learn_application.ui.other.DesignToast
import com.cslu.cse_study_and_learn_application.ui.other.DialogQuestMessage
import com.cslu.cse_study_and_learn_application.utils.HighlightHelper
import com.cslu.cse_study_and_learn_application.utils.HighlightItem
import com.cslu.cse_study_and_learn_application.utils.HighlightPosition
import com.cslu.cse_study_and_learn_application.utils.dpToPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
                DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.SUCCESS, "로그아웃 되었습니다.").show()
                val intent = Intent(requireContext(), SignInActivity::class.java)
                startActivity(intent)
                requireActivity().finish()


            } else {
                //Toast.makeText(requireContext(), "로그아웃에 실패했습니다.", Toast.LENGTH_SHORT).show()
                DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.ERROR, "로그아웃이 실패하였습니다.").show()
            }
        })

        settingViewModel.deactivateResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                // 회원탈퇴 성공 처리
                DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.SUCCESS, "회원탈퇴 되었습니다.").show()
                val intent = Intent(requireContext(), SignInActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            } else {
                // 회원탈퇴 실패 처리
                //Toast.makeText(requireContext(), "회원탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show()
                DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.ERROR, "회원탈퇴를 실패하였습니다.").show()
            }
        })

        settingViewModel.userInfo.observe(viewLifecycleOwner) {
            binding.tvUserName.text = "${it.nickname} 님"
            AccountAssistant.nickname = it.nickname
        }

        initClickListener(settingViewModel)


        val highlightHelper = HighlightHelper(
            requireContext(),
            this,
            listOf(
                HighlightItem(
                    R.id.lin_user_info,
                    "여기서 사용자의 닉네임을 볼 수 있습니다.",
                    showPosition = HighlightPosition.UI_BOTTOM,
                    scaleFactor = 1.1f
                ),
                HighlightItem(
                    R.id.lin_edit_user,
                    "여기서 사용자 정보를 수정할 수 있습니다.",
                    showPosition = HighlightPosition.UI_BOTTOM,
                    scaleFactor = 1.1f
                )
            ),
            debugMode = false,
            bubblePadding = requireContext().dpToPx(20),
            screenName = SettingFragment::class.java.name
        )

        binding.root.post {
            highlightHelper.showHighlights()
        }

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

        binding.linServiceInfo.setOnClickListener {
            // 서비스 정보 다이얼로그 생성 및 표시
            val builder = MaterialAlertDialogBuilder(requireContext())
            val dialogLayout = layoutInflater.inflate(R.layout.dialog_service_info, null)

            // 다이얼로그 레이아웃에서 버전명을 설정
            val versionTextView = dialogLayout.findViewById<TextView>(R.id.tv_version)
            versionTextView.text = "앱 버전: ${BuildConfig.VERSION_NAME}"

            builder.setView(dialogLayout)
                .setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.linContactUs.setOnClickListener {
            // 이메일 앱을 여는 인텐트를 사용하여 문의하기 기능 구현
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("jyh4282002@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "문의 사항")
            }
            startActivity(Intent.createChooser(emailIntent, "이메일 클라이언트를 선택하세요"))
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
