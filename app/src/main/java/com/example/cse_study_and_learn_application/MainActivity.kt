package com.example.cse_study_and_learn_application

import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cse_study_and_learn_application.databinding.ActivityMainBinding
import com.example.cse_study_and_learn_application.ui.other.DesignToast
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 * @autor kjy
 * @since 2024-03-05
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var mainViewModel: MainViewModel
    private var lastBackPressTime: Long = 0
    private var myPageClickCount = 0 // 마이페이지 클릭 카운트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        if (savedInstanceState == null) {
            val typedValue = TypedValue()
            theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
            val actionBarSize = TypedValue.complexToDimensionPixelSize(
                typedValue.data,
                resources.displayMetrics
            )
            mainViewModel.setAppBarHeight(actionBarSize)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_statistics, R.id.navigation_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // BottomNavigationView 항목 선택 처리 및 클릭 카운트
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.popBackStack(R.id.navigation_home, false)
                    true
                }
                R.id.navigation_statistics -> {
                    navController.navigate(R.id.navigation_statistics)
                    true
                }
                R.id.navigation_setting -> {
                    myPageClickCount++ // "마이페이지" 클릭 카운트 증가
                    if (myPageClickCount == 10) {
                        addLectureMenuBetween(navView)
                        Toast.makeText(this, "강의 메뉴가 추가되었습니다!", Toast.LENGTH_SHORT).show()
                    }
                    navController.navigate(R.id.navigation_setting)
                    true
                }
                R.id.navigation_lecture -> {
                    navController.navigate(R.id.navigation_lecture) // 강의 화면으로 이동
                    true
                }
                else -> false
            }
        }
    }

    // "강의" 메뉴 항목을 홈과 통계 사이에 추가하는 함수
    private fun addLectureMenuBetween(navView: BottomNavigationView) {
        val menu = navView.menu
        if (menu.findItem(R.id.navigation_lecture) == null) {
            // 메뉴 재구성: 강의가 홈과 통계 사이에 위치하도록 추가
            menu.clear() // 기존 메뉴 초기화
            menu.add(Menu.NONE, R.id.navigation_home, Menu.NONE, "홈").setIcon(R.drawable.ic_home_black_24dp)
            menu.add(Menu.NONE, R.id.navigation_lecture, Menu.NONE, "강의").setIcon(R.drawable.baseline_menu_book_24) // 강의 메뉴 추가
            menu.add(Menu.NONE, R.id.navigation_statistics, Menu.NONE, "통계").setIcon(R.drawable.ic_dashboard_black_24dp)
            menu.add(Menu.NONE, R.id.navigation_setting, Menu.NONE, "마이페이지").setIcon(R.drawable.ic_report_24)
        }
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.navigation_home -> {
                if (System.currentTimeMillis() - lastBackPressTime < 2000) {
                    super.onBackPressed()
                } else {
                    lastBackPressTime = System.currentTimeMillis()
                    DesignToast.makeText(this, DesignToast.LayoutDesign.INFO, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.navigation_statistics, R.id.navigation_setting -> {
                navController.popBackStack(R.id.navigation_home, false)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}