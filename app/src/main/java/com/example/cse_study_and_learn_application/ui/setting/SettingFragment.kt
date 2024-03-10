package com.example.cse_study_and_learn_application.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cse_study_and_learn_application.MainViewModel
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentSettingBinding

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

        val textView: TextView = binding.textNotifications
        settingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
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