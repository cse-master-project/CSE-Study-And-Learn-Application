package com.cslu.cse_study_and_learn_application.ui.setting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.cslu.cse_study_and_learn_application.R
import com.cslu.cse_study_and_learn_application.databinding.FragmentEditUserInfoBinding
import com.cslu.cse_study_and_learn_application.ui.login.AccountAssistant
import com.cslu.cse_study_and_learn_application.ui.other.DesignToast
import com.cslu.cse_study_and_learn_application.ui.other.DialogQuestMessage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditUserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditUserInfoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentEditUserInfoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var settingViewModel: SettingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.apply {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
            val layoutParams = navHostFragment?.view?.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.apply {
                topMargin = 0
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditUserInfoBinding.inflate(inflater, container, false)
        settingViewModel = ViewModelProvider(requireActivity())[SettingViewModel::class.java]

        binding.ibBackPres.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.tvEmail.text = AccountAssistant.getUserEmail(requireContext())

        // 회원 정보 수정 하는 버튼
        initClickListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val userNickname = settingViewModel.userInfo.value?.nickname
        binding.etNickname.setText(userNickname)
        Log.d("test", "userNickname: $userNickname")
    }

    private fun initClickListener() {
        binding.ibExtend.setOnClickListener {
            val dialogQuestMessage = DialogQuestMessage(
                requireContext(),
                R.layout.dialog_quest_message,
                "회원 정보를 수정하시겠습니까?"
            )

            dialogQuestMessage.setPositive {
                val newNickname = binding.etNickname.text.toString()
                if (newNickname.isEmpty()) {
                    DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "닉네임을 입력해 주세요.").show()
                    dialogQuestMessage.dismiss()
                } else if (AccountAssistant.nickname == newNickname) {
                    DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "새로운 닉네임을 입력해 주세요.").show()
                    dialogQuestMessage.dismiss()
                } else {
                    val result = settingViewModel.updateUserInfo(requireContext(), binding.etNickname.text.toString())
                    dialogQuestMessage.dismiss()

                    if (result) {
                        // 회원정보 수정 성공 처리
                        DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.SUCCESS, "닉네임을 변경하였습니다.").show()
                        settingViewModel.closeEditFragment()
                        requireActivity().onBackPressed()
                    } else {
                        DesignToast.makeText(requireContext(), DesignToast.LayoutDesign.INFO, "이미 사용 중인 닉네임입니다.").show()
                    }
                }
            }

            dialogQuestMessage.setNegative {
                dialogQuestMessage.dismiss()
            }

            dialogQuestMessage.show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditUserInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditUserInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}