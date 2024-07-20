package com.example.cse_study_and_learn_application

import android.os.Bundle
import android.util.TypedValue
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

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_statistics, R.id.navigation_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Use setOnItemSelectedListener instead of the deprecated setOnNavigationItemSelectedListener
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
                    navController.navigate(R.id.navigation_setting)
                    true
                }
                else -> false
            }
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
